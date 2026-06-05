package com.tech.dimefresh.service;


import com.tech.dimefresh.dto.AccountCreateDto;
import com.tech.dimefresh.dto.AccountRegisterDto;
import com.tech.dimefresh.dto.AccountCreatedDto;
import com.tech.dimefresh.dto.UsernamePasswordDto;
import com.tech.dimefresh.exception.rest.badreq.BadRequestExceptionRest;
import com.tech.dimefresh.security.userdetails.AccountDetails;
import com.tech.dimefresh.security.userdetails.AccountDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountDetailsService accountDetailsService;
    private final AccountService accountService;

    /**
     * аутентифицирует пользователя и возвращает его id
     */
    public Long authenticateAccount(UsernamePasswordDto dto) {
        AccountDetails userDetails = (AccountDetails) accountDetailsService.loadUserByUsername(dto.username());

        if(!passwordEncoder.matches(dto.password(), userDetails.getPassword()))
            throw new RuntimeException("Неверный пароль");

        return userDetails.getId();
    }

    public void registerAccount(AccountRegisterDto dto) {
        if(dto.email() == null)
            throw new BadRequestExceptionRest("Не введен email");
        if(accountService.existsAccountWithEmail(dto.email()))
            throw new BadRequestExceptionRest("Пользователь с таким email уже зарегистрирован");

        accountService.createAccount(new AccountCreateDto(
                dto.username(),
                passwordEncoder.encode(dto.password()),
                dto.email(),
                false,
                null
        ));
    }
}
