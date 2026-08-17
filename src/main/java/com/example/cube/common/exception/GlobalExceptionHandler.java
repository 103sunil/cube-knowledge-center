package com.example.cube.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(BadCredentialsAppException.class)
    public void handleBadCredentials(BadCredentialsAppException ex, HttpServletRequest req, HttpServletResponse res) throws IOException {
        write(res, HttpStatus.UNAUTHORIZED, ex.getMessage(), req);
    }

    @ExceptionHandler({AccessDeniedAppException.class, AccessDeniedException.class})
    public void handleAccessDenied(RuntimeException ex, HttpServletRequest req, HttpServletResponse res) throws IOException {
        write(res, HttpStatus.FORBIDDEN, ex.getMessage(), req);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleNotFound(ResourceNotFoundException ex, HttpServletRequest req, HttpServletResponse res) throws IOException {
        write(res, HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public void handleDuplicate(DuplicateResourceException ex, HttpServletRequest req, HttpServletResponse res) throws IOException {
        write(res, HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(ResourceInUseException.class)
    public void handleResourceInUse(ResourceInUseException ex, HttpServletRequest req, HttpServletResponse res) throws IOException {
        write(res, HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(FileValidationException.class)
    public void handleFileValidation(FileValidationException ex, HttpServletRequest req, HttpServletResponse res) throws IOException {
        write(res, HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(FileStorageException.class)
    public void handleFileStorage(FileStorageException ex, HttpServletRequest req, HttpServletResponse res) throws IOException {
        write(res, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req, HttpServletResponse res) throws IOException {
        String message = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Validation failed"
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        write(res, HttpStatus.BAD_REQUEST, message, req);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public void handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest req, HttpServletResponse res) throws IOException {
        write(res, HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req, HttpServletResponse res) throws IOException {
        String message = "Invalid value for '" + ex.getName() + "': expected "
                + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "a different type")
                + " but got '" + ex.getValue() + "'";
        write(res, HttpStatus.BAD_REQUEST, message, req);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public void handleMaxUploadSize(MaxUploadSizeExceededException ex, HttpServletRequest req, HttpServletResponse res) throws IOException {
        write(res, HttpStatus.BAD_REQUEST, "Uploaded file(s) exceed the configured size limit", req);
    }

    @ExceptionHandler(Exception.class)
    public void handleGeneric(Exception ex, HttpServletRequest req, HttpServletResponse res) throws IOException {
        write(res, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", req);
    }

    private void write(HttpServletResponse response, HttpStatus status, String message, HttpServletRequest req) throws IOException {
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(req.getRequestURI())
                .build();

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(error));
        response.getWriter().flush();
    }
}