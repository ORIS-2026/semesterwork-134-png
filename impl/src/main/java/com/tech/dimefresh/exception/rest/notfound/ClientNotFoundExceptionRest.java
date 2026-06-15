package com.tech.dimefresh.exception.rest.notfound;

public class ClientNotFoundExceptionRest extends NotFoundExceptionRest {
    public ClientNotFoundExceptionRest() {
        super("Пользователь не найден");
    }
}
