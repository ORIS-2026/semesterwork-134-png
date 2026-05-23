package com.tech.dimefresh.mapper;


import com.tech.dimefresh.dto.AccountCreatedDto;
import com.tech.dimefresh.dto.AccountInfoDto;
import com.tech.dimefresh.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountInfoDto toInfoResp(Account account);

    AccountCreatedDto toCreatedResp(Account account);
}
