package com.tech.dimefresh.dto;

public record AccountCreateDto (
        String username,
        String password,
        String email,
        Boolean oauthed,
        String googleId
){
}
