package com.tech.dimefresh.security.controller;


import com.tech.dimefresh.security.controller.util.CookiesUtils;
import com.tech.dimefresh.dto.AccountRegisterDto;
import com.tech.dimefresh.dto.UsernamePasswordDto;
import com.tech.dimefresh.service.AuthService;
import com.tech.dimefresh.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.tech.dimefresh.security.controller.util.CookiesUtils.SESSION_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/auth")
public class AuthController {
    private final SessionService sessionService;
    private final AuthService authService;

    @PostMapping(path = "/register")
    public ResponseEntity<?> register(@RequestBody AccountRegisterDto dto) {
        return ResponseEntity.ok(authService.registerAccount(dto));
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody UsernamePasswordDto dto,
                                        HttpServletResponse response) {
        Long accountId = authService.authenticateAccount(dto);
        String sessionId = sessionService.createSession(accountId);

        response.addCookie(
                CookiesUtils.prepareSessionCookie(sessionId)
        );

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = SESSION_ID_HEADER, required = false) String sessionId,
                                    HttpServletResponse response) {
        if (sessionId != null) {
            sessionService.deleteSession(sessionId);
            Cookie cookie = new Cookie(SESSION_ID_HEADER, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return ResponseEntity.ok("Logged out");
    }

    @GetMapping("/info")
    public ResponseEntity<?> getAccountInfo(@CookieValue(value = SESSION_ID_HEADER, required = false) String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new RuntimeException();//TODO: 401
        }

        return ResponseEntity.ok(sessionService.getAccountInfo(sessionId));
    }
}
