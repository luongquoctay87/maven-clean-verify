package com.service.order.exception;

import com.service.order.config.Translator;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        body.put("error", "Validation error");

        // Get all errors
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        body.put("messages", errors);

        return new ResponseEntity<>(body, headers, status);
    }

    /**
     * 401 UNAUTHORIZED: Handle exception when request unauthenticated
     *
     * @param e
     * @param request
     * @return
     */
    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(UnAuthorizeException.class)
    public Error handleUnAuthorizeException(UnAuthorizeException e, WebRequest request) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(UNAUTHORIZED.value());
        error.setError(UNAUTHORIZED.getReasonPhrase());
        error.setMessages(e.getMessage());

        return error;
    }

    /**
     * 403 FORBIDDEN: Handle exception when request incorrect level of authorization to use a specific resource.
     *
     * @param e
     * @param request
     * @return
     */
    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public Error handleAccessForbiddenException(ForbiddenException e, WebRequest request) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(FORBIDDEN.value());
        error.setError(FORBIDDEN.getReasonPhrase());
        error.setMessages(e.getMessage());

        return error;
    }

    /**
     * Handle exception when request not found resource
     *
     * @param e
     * @param request
     * @return
     */
    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public Error handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(NOT_FOUND.value());
        error.setError(NOT_FOUND.getReasonPhrase());
        error.setMessages(e.getMessage());

        return error;
    }

    /**
     * Handle exception when internal server error
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public Error handleCommonException(Exception e, WebRequest request, HttpServletResponse response) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));

        if (e instanceof NullPointerException) {
            error.setStatus(BAD_REQUEST.value());
            error.setError(BAD_REQUEST.getReasonPhrase());
            error.setMessages(e.getMessage());
            response.setStatus(BAD_REQUEST.value());
        } else if ("Bad credentials".equals(e.getMessage())) {
            error.setStatus(FORBIDDEN.value());
            error.setError(FORBIDDEN.getReasonPhrase());
            error.setMessages(Translator.toLocale("user-authenticate.fail"));
            response.setStatus(FORBIDDEN.value());
        } else {
            error.setStatus(INTERNAL_SERVER_ERROR.value());
            error.setError(INTERNAL_SERVER_ERROR.getReasonPhrase());
            error.setMessages(e.getMessage());
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
        return error;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public void constraintViolationException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @Data
    private static class Error {
        private Date timestamp;
        private int status;
        private String path;
        private String error;
        private String messages;
    }

}
