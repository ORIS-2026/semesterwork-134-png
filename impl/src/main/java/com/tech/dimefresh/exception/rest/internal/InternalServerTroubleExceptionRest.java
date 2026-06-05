package com.tech.dimefresh.exception.rest.internal;

import com.tech.dimefresh.exception.rest.RestServiceException;
import org.springframework.http.HttpStatus;

public class InternalServerTroubleExceptionRest extends RestServiceException {
    public InternalServerTroubleExceptionRest() {
        super("Произошла непредвиденная ошибка", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
