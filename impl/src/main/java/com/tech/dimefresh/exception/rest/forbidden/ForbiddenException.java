package com.tech.dimefresh.exception.rest.forbidden;

import com.tech.dimefresh.exception.rest.RestServiceException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends RestServiceException {
    public ForbiddenException() {
        super("Запрещено", HttpStatus.FORBIDDEN);
    }
}
