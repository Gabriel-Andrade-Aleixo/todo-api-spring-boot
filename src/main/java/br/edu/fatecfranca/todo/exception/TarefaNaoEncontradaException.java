package br.edu.fatecfranca.todo.exception;

public class TarefaNaoEncontradaException extends RuntimeException {

    public TarefaNaoEncontradaException(Long id) {
        super("Tarefa com id " + id + " nao encontrada");
    }
}
