package com.tech.dimefresh.exception.rest.badreq;

import com.tech.dimefresh.exception.rest.RestServiceException;
import org.springframework.http.HttpStatus;


public class BadRequestExceptionRest extends RestServiceException {
    public BadRequestExceptionRest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
