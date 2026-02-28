package org.sunrider.market.exception.handler;

import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.sunrider.market.exception.InternalServerException;
import org.sunrider.market.exception.UnauthorizedException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponseDto handleBadRequestException(RuntimeException e) {
        return new ErrorResponseDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerException.class)
    public ErrorResponseDto handleInternalServerException(RuntimeException e) {
        return new ErrorResponseDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponseDto handleNotFoundException(RuntimeException e) {
        return new ErrorResponseDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ErrorResponseDto handleUnauthorized(RuntimeException e) {
        return new ErrorResponseDto(e.getMessage());
    }


}
