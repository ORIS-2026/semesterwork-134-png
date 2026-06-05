package com.tech.dimefresh.exception.rest.notfound;

import com.tech.dimefresh.exception.rest.RestServiceException;
import org.springframework.http.HttpStatus;

public class NotFoundExceptionRest extends RestServiceException {
    public NotFoundExceptionRest(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
