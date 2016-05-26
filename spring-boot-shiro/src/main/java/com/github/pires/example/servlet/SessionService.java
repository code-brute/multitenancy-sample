package com.github.pires.example.servlet;

import org.apache.shiro.session.Session;

public interface SessionService {
	
	Session getSession(String sessionId);
	
	void updateSession(Session session);
}
