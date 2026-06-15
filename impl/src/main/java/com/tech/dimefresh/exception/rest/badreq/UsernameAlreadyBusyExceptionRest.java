package com.tech.dimefresh.exception.rest.badreq;


public class UsernameAlreadyBusyExceptionRest extends BadRequestExceptionRest {

    public UsernameAlreadyBusyExceptionRest() {
        super("Имя пользователя уже занята");
    }
}
