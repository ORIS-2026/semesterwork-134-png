package com.tech.dimefresh.security.controller.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CookiesUtils {
    public static final String SESSION_ID_HEADER = "AI_SESSION_ID";

    public Cookie prepareSessionCookie(String sessionId) {
        Cookie sessionCookie = new Cookie(SESSION_ID_HEADER, sessionId);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(false);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(24 * 60 * 60);

        return sessionCookie;
    }

    public String getSessionIdFromCookie(HttpServletRequest request) {
        String sessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {

            for (Cookie cookie : cookies) {
                if (CookiesUtils.SESSION_ID_HEADER.equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }

        return sessionId;
    }
}
