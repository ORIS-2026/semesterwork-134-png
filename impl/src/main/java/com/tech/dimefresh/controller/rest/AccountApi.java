package com.tech.dimefresh.controller.rest;


import com.tech.dimefresh.dto.AccountInfoDto;
import com.tech.dimefresh.dto.AddAvatarDto;
import com.tech.dimefresh.exception.rest.auth.SimpleUnauthorizedExceptionRest;
import com.tech.dimefresh.security.util.AuthenticationFacade;
import com.tech.dimefresh.service.AccountService;
import com.tech.dimefresh.service.SessionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.tech.dimefresh.security.controller.util.CookiesUtils.SESSION_ID_HEADER;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountApi {
    private final SessionService sessionService;
    private final AccountService accountService;
    private final AuthenticationFacade authenticationFacade;

    @GetMapping("/info")
    public ResponseEntity<AccountInfoDto> getAccountInfo(
            @CookieValue(value = SESSION_ID_HEADER, required = false) String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new SimpleUnauthorizedExceptionRest();
        }
        return ResponseEntity.ok(sessionService.getAccountInfo(sessionId));
    }

    @PostMapping(path = "/avatar")
    public void addAvatar(@RequestParam(name = "avatar_file") MultipartFile multipartFile,
                          HttpServletResponse httpServletResponse) {
        Long accountId = authenticationFacade.getAuthenticatedAccountId();
        accountService.addAvatar(new AddAvatarDto(accountId, multipartFile));
    }

    @DeleteMapping(path = "/avatar")
    public void deleteAvatar() {
        Long authenticatedAccountId = authenticationFacade.getAuthenticatedAccountId();
        accountService.deleteAvatar(authenticatedAccountId);
    }
}
