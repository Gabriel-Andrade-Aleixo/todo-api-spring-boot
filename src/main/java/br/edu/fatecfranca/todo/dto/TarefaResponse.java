package br.edu.fatecfranca.todo.dto;

import br.edu.fatecfranca.todo.model.StatusTarefa;
import br.edu.fatecfranca.todo.model.Tarefa;

import java.time.LocalDateTime;

public record TarefaResponse(
        Long id,
        String nome,
        String descricao,
        StatusTarefa status,
        String observacoes,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
    public static TarefaResponse de(Tarefa tarefa) {
        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getNome(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getObservacoes(),
                tarefa.getDataCriacao(),
                tarefa.getDataAtualizacao()
        );
    }
}
