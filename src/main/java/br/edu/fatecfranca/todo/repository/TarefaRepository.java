package br.edu.fatecfranca.todo.repository;

import br.edu.fatecfranca.todo.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
}
