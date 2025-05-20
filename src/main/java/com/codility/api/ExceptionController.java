package com.codility.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<OrderExceptionDto> handleOrderException(OrderException orderException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(new OrderExceptionDto()
                .setStatus(HttpStatus.BAD_REQUEST.value())
                .setMessage(orderException.getMessage()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<OrderExceptionDto> handleOrderNotFoundException(OrderNotFoundException orderException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(new OrderExceptionDto()
                .setStatus(HttpStatus.NOT_FOUND.value())
                .setMessage(orderException.getMessage()));
    }
}
