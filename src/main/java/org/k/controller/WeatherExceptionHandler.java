package org.k.controller;

import org.k.dto.ErrorMessageDto;
import org.k.exception.WeatherServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WeatherExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(WeatherExceptionHandler.class);

    @ExceptionHandler(WeatherServiceException.class)
    public ResponseEntity<ErrorMessageDto> handleWeatherServiceException(
            WeatherServiceException e) {
        logger.warn(e.getMessage());
        return new ResponseEntity<>(new ErrorMessageDto("Search parameters are not correct."),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessageDto> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        logger.warn(e.getMessage());
        return new ResponseEntity<>(new ErrorMessageDto("Search parameters are not correct."),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDto> handleException(
            Exception e) {
        logger.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorMessageDto("Internal server error."),
                HttpStatus.NOT_FOUND);
    }
}
