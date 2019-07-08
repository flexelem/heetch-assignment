package com.heetch.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {
        InvalidDriverIdException.class,
        InvalidLocationException.class
    })
    @ResponseStatus
    public ResponseEntity<ErrorDTO> handleInvalidDriverLocationMessage(HttpServletRequest request,
                                                                       RuntimeException ex) {
        log.error("Exception caused by ", ex);
        return new ResponseEntity<>(
                ErrorDTO.builder()
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                        .status(HttpStatus.BAD_REQUEST.value())
                        .requestPath(request.getServletPath())
                        .build(),
                HttpStatus.BAD_REQUEST);
    }
}
