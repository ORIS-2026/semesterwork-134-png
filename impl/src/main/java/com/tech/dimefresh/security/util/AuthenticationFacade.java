package com.tech.dimefresh.security.util;


import com.tech.dimefresh.exception.rest.auth.UnauthorizedExceptionRest;
import com.tech.dimefresh.repository.AccountRepository;
import com.tech.dimefresh.security.userdetails.AccountDetails;
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
    public Long getAuthenticatedAccountId(){
        Long userId = null;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(authentication == null) {
                System.out.println("Something goes wrong");
            }
            else {
                System.out.println("Authentication class is:" + authentication.getClass().getName());

                Object principal = authentication.getPrincipal();
                System.out.println("Type of principal: " + principal.getClass().getName());
                if(principal.getClass() == AccountDetails.class)
                    userId = ((AccountDetails) principal).getId();
                else
                    throw new UnauthorizedExceptionRest("Пользователь не авторизован");
            }

        return userId;
    }
}
