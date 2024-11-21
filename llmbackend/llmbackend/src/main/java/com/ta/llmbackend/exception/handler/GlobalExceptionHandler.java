package com.ta.llmbackend.exception.handler;

import com.ta.llmbackend.exception.AuthException;
import com.ta.llmbackend.exception.BadRequestException;
import com.ta.llmbackend.exception.NotFoundException;
import com.ta.llmbackend.exception.UnprocessableEntity;
import com.ta.llmbackend.util.ResponseUtil;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public ResponseEntity<Object> constraintViolationException(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseUtil.badRequest("wrong given value", errors);
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ResponseEntity<Object> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(cv -> errors.add(cv.getMessage()));
        return ResponseUtil.badRequest("wrong given value", errors);
    }

    @ExceptionHandler(value = { HttpMessageNotReadableException.class })
    public ResponseEntity<Object> exception(HttpMessageNotReadableException e) {
        HttpStatus internal = HttpStatus.BAD_REQUEST;
        return ResponseUtil.exception(e.getMessage(), internal);
    }

    @ExceptionHandler(value = { AuthException.class })
    public ResponseEntity<Object> AuthHandler(AuthException e) {
        HttpStatus internal = HttpStatus.UNAUTHORIZED;
        return ResponseUtil.exception(e.getMessage(), internal);
    }

    @ExceptionHandler(value = { PropertyReferenceException.class })
    public ResponseEntity<Object> propPreferenceHandler(PropertyReferenceException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        return ResponseUtil.exception(e.getMessage(), badRequest);
    }

    @ExceptionHandler(value = { UnprocessableEntity.class })
    public ResponseEntity<Object> unprocessableEntityHandler(UnprocessableEntity e) {
        HttpStatus badRequest = HttpStatus.UNPROCESSABLE_ENTITY;
        return ResponseUtil.exception(e.getMessage(), badRequest);
    }

    @ExceptionHandler(value = { NotFoundException.class })
    public ResponseEntity<Object> notFoundHandler(NotFoundException e) {
        HttpStatus notFOund = HttpStatus.NOT_FOUND;
        return ResponseUtil.exception(e.getMessage(), notFOund);
    }

    @ExceptionHandler(value = { BadRequestException.class })
    public ResponseEntity<Object> notFoundHandler(BadRequestException e) {
        HttpStatus notFOund = HttpStatus.BAD_REQUEST;
        return ResponseUtil.exception(e.getMessage(), notFOund);
    }

}
