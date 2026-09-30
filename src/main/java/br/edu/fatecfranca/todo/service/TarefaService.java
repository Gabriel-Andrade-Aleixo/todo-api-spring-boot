package br.edu.fatecfranca.todo.service;

import br.edu.fatecfranca.todo.dto.TarefaRequest;
import br.edu.fatecfranca.todo.dto.TarefaResponse;
import br.edu.fatecfranca.todo.exception.TarefaNaoEncontradaException;
import br.edu.fatecfranca.todo.model.Tarefa;
import br.edu.fatecfranca.todo.repository.TarefaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TarefaService {

    private final TarefaRepository repository;

    public TarefaService(TarefaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TarefaResponse> listar() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "dataCriacao"))
                .stream()
                .map(TarefaResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public TarefaResponse buscarPorId(Long id) {
        return TarefaResponse.de(buscarEntidade(id));
    }

    @Transactional
    public TarefaResponse criar(TarefaRequest request) {
        Tarefa tarefa = new Tarefa(
                request.nome(),
                request.descricao(),
                request.status(),
                request.observacoes()
        );
        return TarefaResponse.de(repository.save(tarefa));
    }

    @Transactional
    public TarefaResponse atualizar(Long id, TarefaRequest request) {
        Tarefa tarefa = buscarEntidade(id);
        tarefa.atualizar(
                request.nome(),
                request.descricao(),
                request.status(),
                request.observacoes()
        );
        return TarefaResponse.de(repository.save(tarefa));
    }

    @Transactional
    public void excluir(Long id) {
        Tarefa tarefa = buscarEntidade(id);
        repository.delete(tarefa);
    }

    private Tarefa buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));
    }
}
