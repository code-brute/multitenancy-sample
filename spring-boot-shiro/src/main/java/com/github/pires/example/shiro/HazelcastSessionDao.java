package com.github.pires.example.shiro;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pires.example.service.SessionService;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * Data Access Object for Shiro {@link Session} persistence in Hazelcast.
 */
public class HazelcastSessionDao extends AbstractSessionDAO implements SessionService{

	private static final Logger log = LoggerFactory.getLogger(HazelcastSessionDao.class);
	private static final String HC_MAP = "sessions";
	private static final String HC_GROUP_NAME = "hc";
	private static final String HC_GROUP_PASSWORD = "oursessionssecret";
	private static final int HC_PORT = 5701;
	private static final String HC_MULTICAST_GROUP = "224.2.2.3";
	private static final int HC_MULTICAST_PORT = 54327;
	private String hcInstanceName = UUID.randomUUID().toString();
	private IMap<Serializable, Session> map;

	public HazelcastSessionDao() {
		log.info("Initializing Hazelcast Shiro session persistence..");

		// configure Hazelcast instance
		final Config cfg = new Config();
		cfg.setInstanceName(hcInstanceName);
		// group configuration
		cfg.setGroupConfig(new GroupConfig(HC_GROUP_NAME, HC_GROUP_PASSWORD));
		// network configuration initialization
		final NetworkConfig netCfg = new NetworkConfig();
		netCfg.setPortAutoIncrement(true);
		netCfg.setPort(HC_PORT);
		// multicast
		final MulticastConfig mcCfg = new MulticastConfig();
		mcCfg.setEnabled(false);
		mcCfg.setMulticastGroup(HC_MULTICAST_GROUP);
		mcCfg.setMulticastPort(HC_MULTICAST_PORT);
		// tcp
		final TcpIpConfig tcpCfg = new TcpIpConfig();
		tcpCfg.addMember("127.0.0.1");
		tcpCfg.setEnabled(false);
		// network join configuration
		final JoinConfig joinCfg = new JoinConfig();
		joinCfg.setMulticastConfig(mcCfg);
		joinCfg.setTcpIpConfig(tcpCfg);
		netCfg.setJoin(joinCfg);
		// ssl
		netCfg.setSSLConfig(new SSLConfig().setEnabled(false));

		// get map
		map = Hazelcast.newHazelcastInstance(cfg).getMap(HC_MAP);
		log.info("Hazelcast Shiro session persistence initialized.");
	}

	@Override
	protected Serializable doCreate(Session session) {
		final Serializable sessionId = generateSessionId(session);
		log.debug("Creating a new session identified by[{}]", sessionId);
		assignSessionId(session, sessionId);
		map.put(session.getId(), session);

		return sessionId;
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		log.debug("Reading a session identified by[{}]", sessionId);
		return map.get(sessionId);
	}

	@Override
	public void update(Session session) throws UnknownSessionException {
		log.debug("Updating a session identified by[{}]", session.getId());
		map.replace(session.getId(), session);
	}

	@Override
	public void delete(Session session) {
		log.debug("Deleting a session identified by[{}]", session.getId());
		map.remove(session.getId());
	}

	@Override
	public Collection<Session> getActiveSessions() {
		return map.values();
	}

	/**
	 * Retrieves a collection of sessions related to a user.
	 * <p>
	 * 
	 * @param email the authentication identifier to look sessions for.
	 * @return a collection of sessions.
	 */
	public Collection<Session> getSessionsForAuthenticationEntity(final String email) {
		log.debug("Looking up for sessions related to [{}]", email);
		final SessionAttributePredicate<String> predicate = new SessionAttributePredicate<>("email", email);
		return map.values(predicate);
	}

	/**
	 * Destroys currently allocated instance.
	 */
	public void destroy() {
		log.info("Shutting down Hazelcast instance [{}]..", hcInstanceName);
		final HazelcastInstance instance = Hazelcast.getHazelcastInstanceByName(hcInstanceName);
		if (instance != null) {
			instance.shutdown();
		}
	}

	@Override
	public Session getSession(String sessionId) {
		if(StringUtils.isNotBlank(sessionId)){
			return this.doReadSession(sessionId);
		}
		return null;
	}

	@Override
	public void updateSession(Session session) {
		if(session == null){
			return;
		}
		this.update(session);
	}

}
