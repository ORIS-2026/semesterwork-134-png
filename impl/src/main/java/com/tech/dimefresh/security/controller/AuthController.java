package com.tech.dimefresh.security.controller;


import com.tech.dimefresh.dto.AccountRegisterDto;
import com.tech.dimefresh.dto.AccountInfoDto;
import com.tech.dimefresh.dto.UsernamePasswordDto;
import com.tech.dimefresh.exception.rest.auth.SimpleUnauthorizedExceptionRest;
import com.tech.dimefresh.security.controller.util.CookiesUtils;
import com.tech.dimefresh.service.AuthService;
import com.tech.dimefresh.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.tech.dimefresh.security.controller.util.CookiesUtils.SESSION_ID_HEADER;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/auth")
public class AuthController {
    private final SessionService sessionService;
    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute AccountRegisterDto dto) {
        try {
            authService.registerAccount(dto);
        } catch (RuntimeException e) {
            return "redirect:/auth/register?error";
        }
        return "redirect:/auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UsernamePasswordDto dto, HttpServletResponse response) {
        try {
            Long accountId = authService.authenticateAccount(dto);
            String sessionId = sessionService.createSession(accountId);
            log.info("Пользователь {} аутентифицирован с сессией {}", dto.username(), sessionId);
            response.addCookie(CookiesUtils.prepareSessionCookie(sessionId));
        } catch (RuntimeException e) {
            return "redirect:/auth/login?error";
        }
        return "redirect:/chat";
    }

    @PostMapping("/logout")
    public String logout(@CookieValue(value = SESSION_ID_HEADER, required = false) String sessionId,
                         HttpServletResponse response) {
        if (sessionId != null) {
            sessionService.deleteSession(sessionId);
            Cookie cookie = new Cookie(SESSION_ID_HEADER, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<AccountInfoDto> getAccountInfo(
            @CookieValue(value = SESSION_ID_HEADER, required = false) String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new SimpleUnauthorizedExceptionRest();
        }
        return ResponseEntity.ok(sessionService.getAccountInfo(sessionId));
    }
}
