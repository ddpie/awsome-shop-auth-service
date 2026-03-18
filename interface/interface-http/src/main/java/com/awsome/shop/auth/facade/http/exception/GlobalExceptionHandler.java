package com.awsome.shop.auth.facade.http.exception;

import com.awsome.shop.auth.common.exception.BusinessException;
import com.awsome.shop.auth.common.exception.ParameterException;
import com.awsome.shop.auth.common.exception.SystemException;
import com.awsome.shop.auth.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * <p>统一使用 common.result.Result（String code）作为响应格式，
 * 与 Controller 层保持一致。</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ParameterException.class)
    public ResponseEntity<Result<Void>> handleParameterException(ParameterException e) {
        log.warn("[全局异常处理] 参数异常: code={}, message={}, errors={}",
                e.getErrorCode(), e.getMessage(), e.getValidationErrors());

        HttpStatus httpStatus = determineHttpStatus(e.getErrorCode());
        return ResponseEntity.status(httpStatus)
                .body(Result.failure(e.getErrorCode(), e.getErrorMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        log.warn("[全局异常处理] Spring参数验证失败: {} 个字段错误", e.getBindingResult().getFieldErrorCount());

        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.failure("BAD_REQUEST_001", message));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        log.warn("[全局异常处理] 业务异常: code={}, message={}", e.getErrorCode(), e.getErrorMessage());

        HttpStatus httpStatus = determineHttpStatus(e.getErrorCode());
        return ResponseEntity.status(httpStatus)
                .body(Result.failure(e.getErrorCode(), e.getErrorMessage()));
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<Result<Void>> handleSystemException(SystemException e) {
        log.error("[全局异常处理] 系统异常: code={}, message={}", e.getErrorCode(), e.getErrorMessage(), e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.failure("SYS_001", "系统异常，请稍后重试"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("[全局异常处理] 未知异常", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.failure("SYS_002", "系统错误，请稍后重试"));
    }

    private HttpStatus determineHttpStatus(String errorCode) {
        if (errorCode == null || errorCode.isEmpty()) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        int lastUnderscore = errorCode.lastIndexOf("_");
        String prefix = lastUnderscore > 0 ? errorCode.substring(0, lastUnderscore) : errorCode;

        return switch (prefix) {
            case "AUTH" -> HttpStatus.UNAUTHORIZED;
            case "AUTHZ", "FORBIDDEN" -> HttpStatus.FORBIDDEN;
            case "BAD_REQUEST", "PARAM" -> HttpStatus.BAD_REQUEST;
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "CONFLICT" -> HttpStatus.CONFLICT;
            case "LOCKED" -> HttpStatus.LOCKED;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
