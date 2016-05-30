package org.zama.examples.multitenant.context.event;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.context.event.GenericApplicationListenerAdapter;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;
import org.zama.examples.multitenant.context.MultiTenantEvent;
import org.zama.examples.multitenant.context.MultiTenantListener;

@Service
public class SimpleMultiTenantEventMulticaster implements MultiTenantEventMulticaster, ApplicationContextAware {

	public final Set<MultiTenantListener<?>> multiTenantListeners;

	private Object retrievalMutex = new Object();

	public SimpleMultiTenantEventMulticaster() {
		this.multiTenantListeners = new LinkedHashSet<MultiTenantListener<?>>();
	}

	@Override
	public void addMultiTenantListener(MultiTenantListener<?> listener) {
		synchronized (this.retrievalMutex) {
			multiTenantListeners.add(listener);
		}
	}

	@Override
	public void removeMultiTenantListener(MultiTenantListener<?> listener) {
		synchronized (this.retrievalMutex) {
			multiTenantListeners.remove(listener);
		}
	}

	@Override
	public void removeAllListeners() {
		synchronized (this.retrievalMutex) {
			multiTenantListeners.clear();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		@SuppressWarnings("rawtypes")
		Collection<MultiTenantListener> listeners = applicationContext.getBeansOfType(MultiTenantListener.class).values();
		listeners.forEach(actlistener -> {
			multiTenantListeners.add(actlistener);
		});
	}

	@Override
	public void multicastEvent(MultiTenantEvent event) {
		multicastEvent(event, resolveDefaultEventType(event));
	}

	private void multicastEvent(final MultiTenantEvent event, ResolvableType eventType) {
		ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
		for (final MultiTenantListener<?> listener : retrieveApplicationListeners(event, type)) {
			Executor executor = getTaskExecutor();
			if (executor != null) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						invokeListener(listener, event);
					}
				});
			} else {
				invokeListener(listener, event);
			}
		}
	}

	private Executor taskExecutor;

	private ErrorHandler errorHandler;

	/**
	 * Set a custom executor (typically a
	 * {@link org.springframework.core.task.TaskExecutor}) to invoke each
	 * listener with.
	 * <p>
	 * Default is equivalent to
	 * {@link org.springframework.core.task.SyncTaskExecutor}, executing all
	 * listeners synchronously in the calling thread.
	 * <p>
	 * Consider specifying an asynchronous task executor here to not block the
	 * caller until all listeners have been executed. However, note that
	 * asynchronous execution will not participate in the caller's thread
	 * context (class loader, transaction association) unless the TaskExecutor
	 * explicitly supports this.
	 * 
	 * @see org.springframework.core.task.SyncTaskExecutor
	 * @see org.springframework.core.task.SimpleAsyncTaskExecutor
	 */
	public void setTaskExecutor(Executor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * Return the current task executor for this multicaster.
	 */
	protected Executor getTaskExecutor() {
		return this.taskExecutor;
	}

	/**
	 * Set the {@link ErrorHandler} to invoke in case an exception is thrown
	 * from a listener.
	 * <p>
	 * Default is none, with a listener exception stopping the current multicast
	 * and getting propagated to the publisher of the current event. If a
	 * {@linkplain #setTaskExecutor task executor} is specified, each individual
	 * listener exception will get propagated to the executor but won't
	 * necessarily stop execution of other listeners.
	 * <p>
	 * Consider setting an {@link ErrorHandler} implementation that catches and
	 * logs exceptions (a la
	 * {@link org.springframework.scheduling.support.TaskUtils#LOG_AND_SUPPRESS_ERROR_HANDLER}
	 * ) or an implementation that logs exceptions while nevertheless
	 * propagating them (e.g.
	 * {@link org.springframework.scheduling.support.TaskUtils#LOG_AND_PROPAGATE_ERROR_HANDLER}
	 * ).
	 * 
	 * @since 4.1
	 */
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * Return the current error handler for this multicaster.
	 * 
	 * @since 4.1
	 */
	protected ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	private ResolvableType resolveDefaultEventType(MultiTenantEvent event) {
		return ResolvableType.forInstance(event);
	}

	/**
	 * Invoke the given listener with the given event.
	 * 
	 * @param listener the ApplicationListener to invoke
	 * @param event the current event to propagate
	 * @since 4.1
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void invokeListener(MultiTenantListener listener, MultiTenantEvent event) {
		ErrorHandler errorHandler = getErrorHandler();
		if (errorHandler != null) {
			try {
				listener.onMultiTenantEvent(event);
			} catch (Throwable err) {
				errorHandler.handleError(err);
			}
		} else {
			try {
				listener.onMultiTenantEvent(event);
			} catch (ClassCastException ex) {
				// Possibly a lambda-defined listener which we could not resolve
				// the generic event type for
				LogFactory.getLog(getClass()).debug("Non-matching event type for listener: " + listener, ex);
			}
		}
	}

	/**
	 * Filter a listener early through checking its generically declared event
	 * type before trying to instantiate it.
	 * <p>
	 * If this method returns {@code true} for a given listener as a first pass,
	 * the listener instance will get retrieved and fully evaluated through a
	 * {@link #supportsEvent(ApplicationListener,ResolvableType, Class)} call
	 * afterwards.
	 * 
	 * @param listenerType the listener's type as determined by the BeanFactory
	 * @param eventType the event type to check
	 * @return whether the given listener should be included in the candidates
	 *         for the given event type
	 */
	protected boolean supportsEvent(Class<?> listenerType, ResolvableType eventType) {
		if (GenericApplicationListener.class.isAssignableFrom(listenerType) || SmartApplicationListener.class.isAssignableFrom(listenerType)) {
			return true;
		}
		ResolvableType declaredEventType = GenericMultiTenantListenerAdapter.resolveDeclaredEventType(listenerType);
		return (declaredEventType == null || declaredEventType.isAssignableFrom(eventType));
	}

	/**
	 * Determine whether the given listener supports the given event.
	 * <p>
	 * The default implementation detects the {@link SmartApplicationListener}
	 * and {@link GenericApplicationListener} interfaces. In case of a standard
	 * {@link ApplicationListener}, a {@link GenericApplicationListenerAdapter}
	 * will be used to introspect the generically declared type of the target
	 * listener.
	 * 
	 * @param listener the target listener to check
	 * @param eventType the event type to check against
	 * @param sourceType the source type to check against
	 * @return whether the given listener should be included in the candidates
	 *         for the given event type
	 */
	protected boolean supportsEvent(MultiTenantListener<?> listener, ResolvableType eventType, Class<?> sourceType) {
		GenericMultiTenantListener smartListener = (listener instanceof GenericMultiTenantListener ? (GenericMultiTenantListener) listener : new GenericMultiTenantListenerAdapter(listener));
		return (smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType));
	}

	private Collection<MultiTenantListener<?>> retrieveApplicationListeners(MultiTenantEvent event, ResolvableType eventType) {
		Object source = event.getSource();
		Class<?> sourceType = (source != null ? source.getClass() : null);
		LinkedList<MultiTenantListener<?>> allListeners = new LinkedList<MultiTenantListener<?>>();
		Set<MultiTenantListener<?>> listeners;
		synchronized (this.retrievalMutex) {
			listeners = new LinkedHashSet<MultiTenantListener<?>>(multiTenantListeners);
		}
		for (MultiTenantListener<?> listener : listeners) {
			if (supportsEvent(listener, eventType, sourceType)) {
				allListeners.add(listener);
			}
		}
		AnnotationAwareOrderComparator.sort(allListeners);
		return allListeners;
	}

}
