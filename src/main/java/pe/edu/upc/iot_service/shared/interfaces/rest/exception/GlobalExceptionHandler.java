package pe.edu.upc.iot_service.shared.interfaces.rest.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.upc.iot_service.iot.domain.model.exceptions.*;
import pe.edu.upc.iot_service.shared.interfaces.rest.resources.ErrorResponseResource;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Centralises exception-to-HTTP mapping for the IoT service.
 * Mirrors the same pattern used in iam-service's GlobalExceptionHandler.
 *
 * Domain exception         → HTTP status
 * ─────────────────────────────────────────
 * DeviceNotFoundException          404
 * DeviceAlreadyRegisteredException 409
 * UnauthorizedDeviceException      403
 * DeviceInactiveException          403
 * IllegalArgumentException         400  (Value Object validation failures)
 * MethodArgumentNotValidException  400  (Bean Validation @Valid failures)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<ErrorResponseResource> handleDeviceNotFound(
            DeviceNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(DeviceAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponseResource> handleDeviceAlreadyRegistered(
            DeviceAlreadyRegisteredException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(UnauthorizedDeviceException.class)
    public ResponseEntity<ErrorResponseResource> handleUnauthorizedDevice(
            UnauthorizedDeviceException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), req);
    }

    @ExceptionHandler(DeviceInactiveException.class)
    public ResponseEntity<ErrorResponseResource> handleDeviceInactive(
            DeviceInactiveException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseResource> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseResource> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message, req);
    }

    // ── private helper ────────────────────────────────────────────────────────

    private ResponseEntity<ErrorResponseResource> build(
            HttpStatus status, String message, HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(new ErrorResponseResource(LocalDateTime.now(), message, req.getRequestURI()));
    }
}
