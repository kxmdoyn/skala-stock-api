package com.sk.skala.stockapi.exception;

import java.util.stream.Collectors;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.Response;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 1️⃣ @Valid @RequestBody DTO 검증 실패
     * - 필드 값이 잘못됨 (빈 값, 범위 오류 등)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Response response = new Response();

        String detail = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        response.setError(Error.INVALID_PARAMETER.getCode(), detail);
        log.warn("Validation(MethodArgumentNotValid) failed: {}", detail);

        return response;
    }

    /**
     * 2️⃣ @Validated + @RequestParam / @PathVariable 검증 실패
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Response handleConstraintViolation(ConstraintViolationException e) {
        Response response = new Response();

        response.setError(Error.INVALID_PARAMETER.getCode(), e.getMessage());
        log.warn("Validation(ConstraintViolation) failed: {}", e.getMessage());

        return response;
    }

    /**
     * 3️⃣ JSON 파싱 / 역직렬화 실패
     * - 예: Integer 필드에 "aaa" 같은 문자열이 들어온 경우
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        Response response = new Response();

        String detail = e.getMessage();
        if (detail != null && detail.length() > 200) {
            detail = detail.substring(0, 200) + "...";
        }

        response.setError(
                Error.INVALID_PARAMETER.getCode(),
                "JSON parse error: " + detail
        );

        log.warn("JSON parse error: {}", e.getMessage());
        return response;
    }

    /**
     * 4️⃣ 인증/권한 예외
     */
    @ExceptionHandler(SecurityException.class)
    public Response takeSecurityException(SecurityException e) {
        Response response = new Response();
        response.setError(Error.NOT_AUTHENTICATED.getCode(), e.getMessage());
        log.error("GlobalExceptionHandler.SecurityException: {}", e.getMessage());
        return response;
    }

    /**
     * 5️⃣ 커스텀 파라미터 예외
     */
    @ExceptionHandler(ParameterException.class)
    public Response takeParameterException(ParameterException e) {
        Response response = new Response();
        response.setError(e.getCode(), e.getMessage());
        return response;
    }

    /**
     * 6️⃣ 커스텀 응답 예외
     */
    @ExceptionHandler(ResponseException.class)
    public Response takeResponseException(ResponseException e) {
        Response response = new Response();
        response.setError(e.getCode(), e.getMessage());
        return response;
    }

    /**
     * 7️⃣ NullPointerException
     */
    @ExceptionHandler(NullPointerException.class)
    public Response takeNullPointerException(NullPointerException e) {
        Response response = new Response();
        response.setError(Error.SYSTEM_ERROR.getCode(), e.getMessage());
        log.error("GlobalExceptionHandler.NullPointerException", e);
        return response;
    }

    /**
     * 8️⃣ 최종 예외 처리 (위에서 못 잡은 모든 예외)
     */
    @ExceptionHandler(Exception.class)
    public Response takeException(Exception e) {
        Response response = new Response();
        response.setError(Error.SYSTEM_ERROR.getCode(), e.getMessage());
        log.error("GlobalExceptionHandler.Exception", e);
        return response;
    }
}
