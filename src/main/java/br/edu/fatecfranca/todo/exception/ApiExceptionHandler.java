package br.edu.fatecfranca.todo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(TarefaNaoEncontradaException.class)
    public ResponseEntity<ApiError> tratarNaoEncontrada(
            TarefaNaoEncontradaException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI(),
                Map.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> tratarValidacao(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : exception.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }

        return resposta(
                HttpStatus.BAD_REQUEST,
                "Existem campos invalidos",
                request.getRequestURI(),
                campos
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> tratarJsonInvalido(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.BAD_REQUEST,
                "JSON invalido. Verifique os tipos e os valores informados",
                request.getRequestURI(),
                Map.of()
        );
    }

    private ResponseEntity<ApiError> resposta(
            HttpStatus status,
            String mensagem,
            String caminho,
            Map<String, String> campos
    ) {
        ApiError erro = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                caminho,
                LocalDateTime.now(ZoneOffset.UTC),
                campos
        );
        return ResponseEntity.status(status).body(erro);
    }
}
