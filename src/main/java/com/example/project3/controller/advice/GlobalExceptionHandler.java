package com.example.project3.controller.advice;

import com.example.project3.exception.FileUploadException;
import com.example.project3.exception.MissingFileException;
import com.example.project3.exception.NotImageFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("유효성검사에 실패했습니다.");

        BindingResult bindingResult = ex.getBindingResult();

        Map<String, String> errorResponse = new HashMap<>();

        bindingResult.getFieldErrors().forEach(error ->
                errorResponse.put(error.getField(), error.getDefaultMessage())
        );
        return errorResponse;
    }

    @ExceptionHandler({UsernameNotFoundException.class, EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUsernameNotFoundException() {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("error", "인증 실패");
        return errorResponse;
    }

    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleFileUploadException() {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("error", "파일 업로드 중 오류 발생");
        return errorResponse;
    }

    @ExceptionHandler(NotImageFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotImageFileException() {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("error", "이미지 파일이 아닙니다.");
        return errorResponse;
    }
    @ExceptionHandler(MissingFileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleMissingFileException() {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("error", "서버에서 에러가 있습니다, 문의 주세요.");
        return errorResponse;
    }
}