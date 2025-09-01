package ru.practicum.server.shareit.exception.handling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.server.shareit.exception.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validation(final ValidationException e) {
        log.error("Ошибка валидации: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Произошла ошибка ValidationException: ", e.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse httpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException e) {
        log.error("Неподдерживаемый медиа-тип: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Произошла ошибка HttpMediaTypeNotSupportedException: ", e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse httpMessageNotReadable(final HttpMessageNotReadableException e) {
        log.error("Ошибка чтения HTTP сообщения: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Произошла ошибка HttpMessageNotReadableException: ", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse argumentTypeMismatch(final MethodArgumentTypeMismatchException e) {
        log.error("Несоответствие типа аргумента: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Произошла ошибка MethodArgumentTypeMismatchException: ", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse argumentNotValid(MethodArgumentNotValidException e) {
        log.error("Некорректный аргумент: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Произошла ошибка MethodArgumentTypeMismatchException: ", e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse forbidden(final ForbiddenException e) {
        log.error("Доступ запрещен: {}", e.getMessage());
        return new ErrorResponse("ERROR[403]: Произошла ошибка ForbiddenException: ", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(final NotFoundException e) {
        log.error("Ресурс не найден: {}", e.getMessage());
        return new ErrorResponse("ERROR[404]: Произошла ошибка NotFoundException: ", e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse conflict(ConflictException e) {
        log.error("Конфликт данных: {}", e.getMessage());
        return new ErrorResponse("ERROR[409]: Произошла ошибка Conflict: ", e.getMessage());
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse internalServerException(final InternalServerException e) {
        log.error("Внутренняя ошибка сервера: {}", e.getMessage());
        return new ErrorResponse("ERROR[500]: Произошла ошибка InternalServerException: ", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse exception(final Exception e) {
        log.error("Непредвиденная ошибка: {}", e.getMessage());
        return new ErrorResponse("ERROR[500]: Произошла ошибка Throwable: ", e.getMessage());
    }
}