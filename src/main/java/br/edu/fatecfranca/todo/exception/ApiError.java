package br.edu.fatecfranca.todo.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
        int status,
        String erro,
        String mensagem,
        String caminho,
        LocalDateTime instante,
        Map<String, String> campos
) {
}
