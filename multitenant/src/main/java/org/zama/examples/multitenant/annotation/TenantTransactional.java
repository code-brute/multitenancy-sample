package org.zama.examples.multitenant.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Transactional
public @interface TenantTransactional {
	
	public static final String DEFAULT_NAME="tenantTransactionManager";
	
	/**
	 * Alias for {@link #transactionManager}.
	 * 
	 * @see #transactionManager
	 */
	@AliasFor(annotation = Transactional.class, attribute = "value")
	String value() default DEFAULT_NAME;

	/**
	 * A <em>qualifier</em> value for the specified transaction.
	 * <p>
	 * May be used to determine the target transaction manager, matching the
	 * qualifier value (or the bean name) of a specific
	 * {@link org.springframework.transaction.PlatformTransactionManager} bean
	 * definition.
	 * 
	 * @since 4.2
	 * @see #value
	 */
	@AliasFor(annotation = Transactional.class, attribute = "transactionManager")
	String transactionManager() default DEFAULT_NAME;

	/**
	 * The transaction propagation type.
	 * <p>
	 * Defaults to {@link Propagation#REQUIRED}.
	 * 
	 * @see org.springframework.transaction.interceptor.TransactionAttribute#getPropagationBehavior()
	 */
	@AliasFor(annotation = Transactional.class, attribute = "propagation")
	Propagation propagation() default Propagation.REQUIRED;

	/**
	 * The transaction isolation level.
	 * <p>
	 * Defaults to {@link Isolation#DEFAULT}.
	 * 
	 * @see org.springframework.transaction.interceptor.TransactionAttribute#getIsolationLevel()
	 */
	@AliasFor(annotation = Transactional.class, attribute = "isolation")
	Isolation isolation() default Isolation.DEFAULT;

	/**
	 * The timeout for this transaction.
	 * <p>
	 * Defaults to the default timeout of the underlying transaction system.
	 * 
	 * @see org.springframework.transaction.interceptor.TransactionAttribute#getTimeout()
	 */
	@AliasFor(annotation = Transactional.class, attribute = "timeout")
	int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

	/**
	 * {@code true} if the transaction is read-only.
	 * <p>
	 * Defaults to {@code false}.
	 * <p>
	 * This just serves as a hint for the actual transaction subsystem; it will
	 * <i>not necessarily</i> cause failure of write access attempts. A
	 * transaction manager which cannot interpret the read-only hint will
	 * <i>not</i> throw an exception when asked for a read-only transaction.
	 * 
	 * @see org.springframework.transaction.interceptor.TransactionAttribute#isReadOnly()
	 */
	@AliasFor(annotation = Transactional.class, attribute = "readOnly")
	boolean readOnly() default false;

	/**
	 * Defines zero (0) or more exception {@link Class classes}, which must be
	 * subclasses of {@link Throwable}, indicating which exception types must
	 * cause a transaction rollback.
	 * <p>
	 * This is the preferred way to construct a rollback rule (in contrast to
	 * {@link #rollbackForClassName}), matching the exception class and its
	 * subclasses.
	 * <p>
	 * Similar to
	 * {@link org.springframework.transaction.interceptor.RollbackRuleAttribute#RollbackRuleAttribute(Class clazz)}
	 * 
	 * @see #rollbackForClassName
	 */
	@AliasFor(annotation = Transactional.class, attribute = "rollbackFor")
	Class<? extends Throwable>[] rollbackFor() default {};

	/**
	 * Defines zero (0) or more exception names (for exceptions which must be a
	 * subclass of {@link Throwable}), indicating which exception types must
	 * cause a transaction rollback.
	 * <p>
	 * This can be a substring of a fully qualified class name, with no wildcard
	 * support at present. For example, a value of {@code "ServletException"}
	 * would match {@link javax.servlet.ServletException} and its subclasses.
	 * <p>
	 * <b>NB:</b> Consider carefully how specific the pattern is and whether to
	 * include package information (which isn't mandatory). For example,
	 * {@code "Exception"} will match nearly anything and will probably hide
	 * other rules. {@code "java.lang.Exception"} would be correct if
	 * {@code "Exception"} were meant to define a rule for all checked
	 * exceptions. With more unusual {@link Exception} names such as
	 * {@code "BaseBusinessException"} there is no need to use a FQN.
	 * <p>
	 * Similar to
	 * {@link org.springframework.transaction.interceptor.RollbackRuleAttribute#RollbackRuleAttribute(String exceptionName)}
	 * 
	 * @see #rollbackFor
	 */
	@AliasFor(annotation = Transactional.class, attribute = "rollbackForClassName")
	String[] rollbackForClassName() default {};

	/**
	 * Defines zero (0) or more exception {@link Class Classes}, which must be
	 * subclasses of {@link Throwable}, indicating which exception types must
	 * <b>not</b> cause a transaction rollback.
	 * <p>
	 * This is the preferred way to construct a rollback rule (in contrast to
	 * {@link #noRollbackForClassName}), matching the exception class and its
	 * subclasses.
	 * <p>
	 * Similar to
	 * {@link org.springframework.transaction.interceptor.NoRollbackRuleAttribute#NoRollbackRuleAttribute(Class clazz)}
	 * 
	 * @see #noRollbackForClassName
	 */
	@AliasFor(annotation = Transactional.class, attribute = "noRollbackFor")
	Class<? extends Throwable>[] noRollbackFor() default {};

	/**
	 * Defines zero (0) or more exception names (for exceptions which must be a
	 * subclass of {@link Throwable}) indicating which exception types must
	 * <b>not</b> cause a transaction rollback.
	 * <p>
	 * See the description of {@link #rollbackForClassName} for further
	 * information on how the specified names are treated.
	 * <p>
	 * Similar to
	 * {@link org.springframework.transaction.interceptor.NoRollbackRuleAttribute#NoRollbackRuleAttribute(String exceptionName)}
	 * 
	 * @see #noRollbackFor
	 */
	@AliasFor(annotation = Transactional.class, attribute = "noRollbackForClassName")
	String[] noRollbackForClassName() default {};

}
