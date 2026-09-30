package br.edu.fatecfranca.todo.dto;

import br.edu.fatecfranca.todo.model.StatusTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TarefaRequest(
        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 120, message = "O nome deve ter no maximo 120 caracteres")
        String nome,

        @NotBlank(message = "A descricao e obrigatoria")
        @Size(max = 1000, message = "A descricao deve ter no maximo 1000 caracteres")
        String descricao,

        @NotNull(message = "O status e obrigatorio")
        StatusTarefa status,

        @Size(max = 2000, message = "As observacoes devem ter no maximo 2000 caracteres")
        String observacoes
) {
}
