package com.aayaniqbal.spring_boot_url_shortener.web;

import com.aayaniqbal.spring_boot_url_shortener.domain.exceptions.ShortUrlNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ShortUrlNotFoundException.class)
    String handleShortUrlNotFoundException(ShortUrlNotFoundException ex) {
        log.error("Short URL not found: {}", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    String handleNoResourceFound(NoResourceFoundException ex) {
        log.debug("Resource not found: {}", ex.getResourcePath());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    String handleException(Exception ex) {
        log.error("Unhandled Exception: {}", ex.getMessage(), ex);
        return "error/500";
    }

}
