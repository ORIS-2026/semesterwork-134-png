package com.tech.dimefresh.exception.rest.badreq;


public class RoleNotFoundExceptionRest extends BadRequestExceptionRest {

    public RoleNotFoundExceptionRest() {
        super("Роль не найдена");
    }
}
