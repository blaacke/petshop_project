package com.tzanotto.metayway.petshop.config;

import com.tzanotto.metayway.petshop.api.ErrorResponse;
import com.tzanotto.metayway.petshop.exceptions.DuplicatedCpfException;
import com.tzanotto.metayway.petshop.exceptions.InactiveUserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
    public ResponseEntity<ErrorResponse> handleBodyValidation(Exception ex, HttpServletRequest req) {
        var errors = new LinkedHashMap<String, String>();
        var bindingResult = ex instanceof MethodArgumentNotValidException manv
                ? manv.getBindingResult()
                : ((BindException) ex).getBindingResult();

        bindingResult.getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage()));

        var body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Validação falhou", req.getRequestURI(), errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleParamValidation(ConstraintViolationException ex, HttpServletRequest req) {
        var errors = new LinkedHashMap<String, String>();
        ex.getConstraintViolations().forEach(v ->
                errors.put(v.getPropertyPath().toString(), v.getMessage()));
        var body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Constraint violation", req.getRequestURI(), errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        var body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(), req.getRequestURI(), null);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest req
    ) {
        var errors = new LinkedHashMap<String, String>();
        errors.put("cpf", "CPF já cadastrado");

        var body = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Violação de campo unico",
                req.getRequestURI(),
                errors
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(DuplicatedCpfException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCpf(
            DuplicatedCpfException ex,
            HttpServletRequest req
    ) {
        var errors = new LinkedHashMap<String, String>();
        errors.put("cpf", "CPF já cadastrado");

        var body = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                req.getRequestURI(),
                errors
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(InactiveUserException.class)
    public ResponseEntity<ErrorResponse> handleInactiveUser(
            DuplicatedCpfException ex,
            HttpServletRequest req
    ) {
        var errors = new LinkedHashMap<String, String>();
        errors.put("error", "Usuário inativo. Consulte um administrador para regulizar sua situação");

        var body = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                req.getRequestURI(),
                errors
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
