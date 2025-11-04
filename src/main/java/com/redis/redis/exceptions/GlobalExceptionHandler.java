package com.redis.redis.exceptions;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.redis.redis.dto.ErrorResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // trata exceções da API
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDTO> handleApiException(ApiException ex){
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            ex.getStatus().value(),
            ex.getStatus().getReasonPhrase(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    // erro de validação (DTO COM @VALID)
    @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex){
            // extrai todas as mensagens de erro e junta em uma string
            String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    message,
                    LocalDateTime.now()
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }


    // trata exceções genéricas não previstas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex){
        ErrorResponseDTO error = new ErrorResponseDTO(
            500,
            "Internal Server erro",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(500).body(error);
    }
}
