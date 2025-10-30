package com.template.reactive.api.common.exceptions;

import com.template.reactive.api.common.AppConstants;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.MissingRequestValueException;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MissingRequestValueException.class)
    public ResponseEntity<String> handleMissingRequestValue(MissingRequestValueException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(String.format(AppConstants.ERROR_MESSAGE_FORMAT, "Missing required Request parameter: " + ex.getName()));
    }

    @ExceptionHandler(DecodingException.class)
    public ResponseEntity<String> handleDecodingException(DecodingException ex) {
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String body = AppConstants.DEFAULT_ERROR_MESSAGE;

        if (ex.getMessage().contains("No request body")) {
            status = HttpStatus.BAD_REQUEST.value();
            body = "Expected a request body but none found!";
        } else if (ex.getMessage().contains("JSON decoding error")) {
            status = HttpStatus.BAD_REQUEST.value();
            body = "Unable to process request Body, please ensure correct formatting.";
        }

        return ResponseEntity
                .status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(String.format(AppConstants.ERROR_MESSAGE_FORMAT, body));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> handleApiException(ApiException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(String.format(AppConstants.ERROR_MESSAGE_FORMAT, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(AppConstants.DEFAULT_ERROR_MESSAGE);
    }
}
