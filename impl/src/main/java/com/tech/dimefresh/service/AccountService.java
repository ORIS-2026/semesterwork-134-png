package com.tech.dimefresh.service;


import com.tech.dimefresh.dto.AccountCreateDto;
import com.tech.dimefresh.dto.AccountCreatedDto;
import com.tech.dimefresh.dto.AccountInfoDto;
import com.tech.dimefresh.entity.Account;
import com.tech.dimefresh.service.mapper.AccountMapper;
import com.tech.dimefresh.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;


    public AccountInfoDto getAccountInfo(Long accountId) {
        return accountMapper.toInfoResp(
                accountRepository.findById(accountId).orElseThrow()
        );//TODO: 401
    }

    public AccountCreatedDto createAccount(AccountCreateDto dto) {
        Account account = new Account();
        account.setName(dto.username());
        account.setEmail(dto.email());
        account.setHashedPassword(dto.password());
        account.setOauthed(dto.oauthed());
        account.setGoogleId(dto.googleId());

        return accountMapper.toCreatedResp(accountRepository.save(account));
    }

    public boolean existsAccountWithEmail(String email) {
        return accountRepository.findByAccountByEmail(email).isPresent();
    }

    public AccountInfoDto findByEmail(String email) {
        return accountMapper.toInfoResp(
                accountRepository.findByAccountByEmail(email).orElseThrow()
        );
    }

}
