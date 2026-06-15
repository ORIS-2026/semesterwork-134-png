package com.tech.dimefresh.repository;

import com.tech.dimefresh.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("select a from Account a where a.name = ?1 and a.oauthed=false ")
    Optional<Account> findByNameWithPassword(String name);

    @Query("select a from Account a where a.email = ?1")
    Optional<Account> findByAccountByEmail(String email);

    @Query("select a from Account a where a.email = ?1 and a.oauthed = false")
    Optional<Account> findNotOAuthedAccountWithEmail(String email);
}
