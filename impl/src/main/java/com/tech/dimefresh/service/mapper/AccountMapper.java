package com.tech.dimefresh.service.mapper;


import com.tech.dimefresh.dto.AccountCreatedDto;
import com.tech.dimefresh.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountCreatedDto toCreatedResp(Account account);
}
