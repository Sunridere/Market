package org.sunrider.market.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.sunrider.market.exception.BadRequestException;
import org.sunrider.market.exception.InternalServerException;
import org.sunrider.market.exception.ItemAlreadyInCartException;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.exception.UnauthorizedException;
import org.sunrider.market.exception.UserAlreadyExistsException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponseDto handleBadRequestException(RuntimeException e) {
        return new ErrorResponseDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse("Ошибка валидации");

        return new ErrorResponseDto(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleInvalidFormat(HttpMessageNotReadableException e) {
        return new ErrorResponseDto("Неверный формат JSON запроса");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponseDto handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return new ErrorResponseDto(String.format("Параметр '%s' должен быть типа '%s'",
            e.getName(),
            e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown"
        ));
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

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({UserAlreadyExistsException.class, ItemAlreadyInCartException.class})
    public ErrorResponseDto handleUserAlreadyExists(RuntimeException e) {
        return new ErrorResponseDto(e.getMessage());
    }
}
