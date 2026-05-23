package com.tech.dimefresh.security.util;


import com.tech.dimefresh.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationFacade {
    private final AccountRepository accountRepository;

    /**
     * работает только на защищенных ресурсах
     */
    public Long getAuthenticatedUserId(){
        Long userId = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null)
                System.out.println("Something goes wrong");
            System.out.println("Authentication class is:" + authentication.getClass().getName());

            

        }
        catch (NullPointerException exc) {
            throw new RuntimeException("Пользователь не аутентифицирован");
        }
        return userId;
    }
}
