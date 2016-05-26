package com.github.pires.example.servlet;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.web.servlet.SimpleCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionUtils {
	private static final transient Logger log = LoggerFactory.getLogger(SimpleCookie.class);

	public static String readValue(HttpServletRequest request, String name) {
		String value = null;
		javax.servlet.http.Cookie cookie = getCookie(request, name);
		if (cookie != null) {
			value = cookie.getValue();
			log.debug("Found '{}' cookie value [{}]", name, value);
		} else {
			log.trace("No '{}' cookie value", name);
		}

		return value;
	}

	/**
	 * Returns the cookie with the given name from the request or {@code null}
	 * if no cookie with that name could be found.
	 *
	 * @param request the current executing http request.
	 * @param cookieName the name of the cookie to find and return.
	 * @return the cookie with the given name from the request or {@code null}
	 *         if no cookie with that name could be found.
	 */
	private static javax.servlet.http.Cookie getCookie(HttpServletRequest request, String cookieName) {
		javax.servlet.http.Cookie cookies[] = request.getCookies();
		if (cookies != null) {
			for (javax.servlet.http.Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					return cookie;
				}
			}
		}
		return null;
	}
}
