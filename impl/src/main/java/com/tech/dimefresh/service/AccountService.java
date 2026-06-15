package com.tech.dimefresh.service;


import com.tech.dimefresh.config.properties.S3Properties;
import com.tech.dimefresh.dto.AccountCreateDto;
import com.tech.dimefresh.dto.AccountCreatedDto;
import com.tech.dimefresh.dto.AccountInfoDto;
import com.tech.dimefresh.dto.AddAvatarDto;
import com.tech.dimefresh.entity.Account;
import com.tech.dimefresh.entity.S3Object;
import com.tech.dimefresh.exception.rest.RestServiceException;
import com.tech.dimefresh.exception.rest.badreq.BadRequestExceptionRest;
import com.tech.dimefresh.exception.rest.internal.InternalServerTroubleExceptionRest;
import com.tech.dimefresh.repository.AccountRepository;
import com.tech.dimefresh.repository.S3ObjectRepository;
import com.tech.dimefresh.s3.S3Manager;
import com.tech.dimefresh.s3.S3Model;
import com.tech.dimefresh.service.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final S3ObjectRepository s3ObjectRepository;

    public static final List<String> VALID_AVATAR_FORMATS = List.of(
            "image/png", "image/jpeg"
    );

    private final S3Properties s3Properties;
    private final S3Manager s3Manager;

    @Transactional
    @CacheEvict(value = "accounts_avatars", key = "#accountId")
    public void deleteAvatar(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(InternalServerTroubleExceptionRest::new);

        if(account.getAvatarS3Object() != null) {
            account.setAvatarS3Object(null);
            accountRepository.save(account);
        }
    }

    @Transactional
    @CacheEvict(value = "accounts_avatars", key = "#dto.accountId()")
    public void addAvatar(AddAvatarDto dto) {
        String contentType = dto.multipartFile().getContentType();
        if (VALID_AVATAR_FORMATS
                .stream()
                .noneMatch(format ->
                        format.equals(dto.multipartFile().getContentType())))
            throw new BadRequestExceptionRest("Неверный формат файла");

        byte[] avatarFile;
        try {
            avatarFile = dto.multipartFile().getBytes();
        } catch (IOException e) {
            throw new RestServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        S3Object avatarS3Object = new S3Object();
        avatarS3Object.setBucket(s3Properties.getImagesBucket());
        avatarS3Object.setWeight(avatarFile.length);
        avatarS3Object.setContentType(contentType);
        avatarS3Object = s3ObjectRepository.save(avatarS3Object);

        Account authorizedAccount = accountRepository.findById(dto.accountId())
                .orElseThrow(InternalServerTroubleExceptionRest::new);
        authorizedAccount.setAvatarS3Object(avatarS3Object);
        accountRepository.save(authorizedAccount);

        s3Manager.save(new S3Model(
                avatarS3Object.getId().toString(), contentType, s3Properties.getImagesBucket(), avatarFile
        ));
    }

    @Transactional
    public AccountInfoDto getAccountInfo(Long accountId) {
        Account account =accountRepository.findById(accountId).orElseThrow(InternalServerTroubleExceptionRest::new);
        String avatarUrl = getAvatarUrl(accountId);

        return new AccountInfoDto(
                account.getId(), account.getName(), account.getEmail(), account.isOauthed(), avatarUrl
        );
    }

    @Transactional
    @Cacheable(value = "accounts_avatars", key = "#accountId")
    public String getAvatarUrl(Long accountId) {
        S3Object avatarS3Object = s3ObjectRepository.findAvatarByAccountId(accountId).orElse(null);
        if(avatarS3Object == null)
            return null;
        return s3Manager.get(avatarS3Object.getId().toString(), avatarS3Object.getBucket());
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

    public boolean existsNotOAuthedAccountWithEmail(String email) {
        return accountRepository.findNotOAuthedAccountWithEmail(email).isPresent();
    }

    public Long findAccountIdByEmail(String email) {
        return accountRepository.findByAccountByEmail(email)
                .orElseThrow(InternalServerTroubleExceptionRest::new).getId();

    }

}
