package com.tech.dimefresh.exception.rest.auth;


public class SimpleUnauthorizedExceptionRest extends UnauthorizedExceptionRest {
    public SimpleUnauthorizedExceptionRest() {
        super("Не авторизованы");
    }
}
