package com.tech.dimefresh.controller.util;

import jakarta.servlet.http.Cookie;
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
}
