package com.tech.dimefresh.security.filter;

import com.tech.dimefresh.security.controller.util.CookiesUtils;
import com.tech.dimefresh.security.userdetails.AccountDetails;
import com.tech.dimefresh.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionFilter extends OncePerRequestFilter {

    private final SessionService sessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("Зашли в фильтр сессий");
        String sessionId = CookiesUtils.getSessionIdFromCookie(request);
        log.info("Сессия из заголовка: {}", sessionId);

        if (sessionId != null) {
            log.info("Сессия есть");
            Optional<Long> accountId = sessionService.getAccountIdBySession(sessionId);

            if(accountId.isPresent()) {
                log.info("Сессия действительна");
                AccountDetails accountDetails = AccountDetails.builder()
                        .id(accountId.get())
                        .authorities(List.of())
                        .isAccountNonExpired(true)
                        .isAccountNonLocked(true)
                        .isCredentialsNonExpired(true)
                        .isEnabled(true)
                        .build();

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        accountDetails, null, accountDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }


        filterChain.doFilter(request, response);
    }
}
