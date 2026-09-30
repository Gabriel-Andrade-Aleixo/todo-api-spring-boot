package br.edu.fatecfranca.todo.integration;

import br.edu.fatecfranca.todo.model.Tarefa;
import br.edu.fatecfranca.todo.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TarefaApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TarefaRepository repository;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    @Test
    void deveExecutarOFluxoCompletoDoCrud() throws Exception {
        mockMvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("PENDENTE")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value("Estudar Spring Boot"))
                .andExpect(jsonPath("$.status").value("PENDENTE"));

        Tarefa criada = repository.findAll().get(0);

        mockMvc.perform(get("/api/tarefas/{id}", criada.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(criada.getId()))
                .andExpect(jsonPath("$.dataCriacao").exists());

        mockMvc.perform(put("/api/tarefas/{id}", criada.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("CONCLUIDA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        mockMvc.perform(get("/api/tarefas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Estudar Spring Boot"));

        mockMvc.perform(delete("/api/tarefas/{id}", criada.getId()))
                .andExpect(status().isNoContent());

        assertThat(repository.count()).isZero();
    }

    @Test
    void deveRejeitarCamposObrigatoriosVazios() throws Exception {
        mockMvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "",
                                  "descricao": "",
                                  "status": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.descricao").exists())
                .andExpect(jsonPath("$.campos.status").exists());
    }

    @Test
    void deveRetornar404ParaTarefaInexistente() throws Exception {
        mockMvc.perform(get("/api/tarefas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Tarefa com id 999 nao encontrada"));
    }

    private String json(String status) {
        return """
                {
                  "nome": "Estudar Spring Boot",
                  "descricao": "Revisar os conceitos de API REST",
                  "status": "%s",
                  "observacoes": "Separar duas horas"
                }
                """.formatted(status);
    }
}
