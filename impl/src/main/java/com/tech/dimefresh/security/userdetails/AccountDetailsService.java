package com.tech.dimefresh.security.userdetails;

import com.tech.dimefresh.entity.Account;
import com.tech.dimefresh.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Getting user from UserDetailsService");
        Account account = accountRepository.findByNameWithPassword(username)
                .orElseThrow(()-> new UsernameNotFoundException("Пользователь с таким именем не найден"));//TODO: заменить на свой exception
        System.out.printf("User with name %s has been fetched\n", account.getName());

        return AccountDetails.builder()
                .id(account.getId())
                .username(account.getName())
                .hashedPassword(account.getHashedPassword())
                .authorities(List.of())
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();
    }
}
