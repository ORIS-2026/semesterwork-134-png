package com.tech.dimefresh.exception.rest.auth;

import com.tech.dimefresh.exception.rest.RestServiceException;
import org.springframework.http.HttpStatus;

public class UnauthorizedExceptionRest extends RestServiceException {
    public UnauthorizedExceptionRest(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
