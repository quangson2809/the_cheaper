package com.example.the_cheaper.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.the_cheaper.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalExceptionHandler {
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException e,
                        HttpServletRequest request) {
                ApiResponse<Void> res = new ApiResponse<>(
                                HttpStatus.NOT_FOUND.value(),
                                e.getMessage(),
                                null,
                                LocalDateTime.now(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        @ExceptionHandler(SystemException.class)
        public ResponseEntity<ApiResponse<Void>> handleSystemException(SystemException e, HttpServletRequest request) {
                ApiResponse<Void> res = new ApiResponse<>(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                e.getMessage(),
                                null,
                                LocalDateTime.now(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

        @ExceptionHandler(ResourceAlreadyExistsException.class)
        public ResponseEntity<ApiResponse<Void>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e,
                        HttpServletRequest request) {
                ApiResponse<Void> res = new ApiResponse<>(
                                HttpStatus.BAD_REQUEST.value(),
                                e.getMessage(),
                                null,
                                LocalDateTime.now(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        @ExceptionHandler(InvalidInputException.class)
        public ResponseEntity<ApiResponse<Void>> handleInvalidInputException(InvalidInputException e,
                        HttpServletRequest request) {
                ApiResponse<Void> res = new ApiResponse<>(
                                HttpStatus.BAD_REQUEST.value(),
                                e.getMessage(),
                                null,
                                LocalDateTime.now(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException e,
                        HttpServletRequest request) {
                ApiResponse<Void> res = new ApiResponse<>(
                                HttpStatus.BAD_REQUEST.value(),
                                e.getMessage(),
                                null,
                                LocalDateTime.now(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        @ExceptionHandler(NotImplementedException.class)
        public ResponseEntity<ApiResponse<Void>> handleNotImplementedException(NotImplementedException e,
                        HttpServletRequest request) {
                ApiResponse<Void> res = new ApiResponse<>(
                                HttpStatus.NOT_IMPLEMENTED.value(),
                                e.getMessage(),
                                null,
                                LocalDateTime.now(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(res);
        }
}
