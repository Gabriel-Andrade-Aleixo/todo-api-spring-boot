package br.edu.fatecfranca.todo.service;

import br.edu.fatecfranca.todo.dto.TarefaRequest;
import br.edu.fatecfranca.todo.dto.TarefaResponse;
import br.edu.fatecfranca.todo.exception.TarefaNaoEncontradaException;
import br.edu.fatecfranca.todo.model.StatusTarefa;
import br.edu.fatecfranca.todo.model.Tarefa;
import br.edu.fatecfranca.todo.repository.TarefaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository repository;

    @InjectMocks
    private TarefaService service;

    @Test
    void deveCriarUmaTarefa() {
        TarefaRequest request = novaRequest(StatusTarefa.PENDENTE);
        when(repository.save(any(Tarefa.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        TarefaResponse resposta = service.criar(request);

        assertThat(resposta.nome()).isEqualTo("Estudar Spring Boot");
        assertThat(resposta.status()).isEqualTo(StatusTarefa.PENDENTE);
        verify(repository).save(any(Tarefa.class));
    }

    @Test
    void deveAtualizarUmaTarefaExistente() {
        Tarefa tarefa = new Tarefa(
                "Estudar Spring Boot",
                "Revisar os conceitos",
                StatusTarefa.PENDENTE,
                null
        );
        when(repository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(repository.save(tarefa)).thenReturn(tarefa);

        TarefaResponse resposta = service.atualizar(1L, novaRequest(StatusTarefa.CONCLUIDA));

        assertThat(resposta.status()).isEqualTo(StatusTarefa.CONCLUIDA);
        verify(repository).save(tarefa);
    }

    @Test
    void deveInformarQuandoATarefaNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(TarefaNaoEncontradaException.class)
                .hasMessageContaining("99");
    }

    @Test
    void naoDeveExcluirUmaTarefaInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(99L))
                .isInstanceOf(TarefaNaoEncontradaException.class);
        verify(repository, never()).delete(any(Tarefa.class));
    }

    private TarefaRequest novaRequest(StatusTarefa status) {
        return new TarefaRequest(
                "Estudar Spring Boot",
                "Revisar os conceitos",
                status,
                "Separar duas horas"
        );
    }
}
