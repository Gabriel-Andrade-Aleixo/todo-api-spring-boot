package br.edu.fatecfranca.todo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "tarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusTarefa status;

    @Column(length = 2000)
    private String observacoes;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    protected Tarefa() {
    }

    public Tarefa(String nome, String descricao, StatusTarefa status, String observacoes) {
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
        this.observacoes = observacoes;
    }

    @PrePersist
    void preencherDatasDeCriacao() {
        LocalDateTime agora = LocalDateTime.now(ZoneOffset.UTC);
        dataCriacao = agora;
        dataAtualizacao = agora;
    }

    @PreUpdate
    void preencherDataDeAtualizacao() {
        dataAtualizacao = LocalDateTime.now(ZoneOffset.UTC);
    }

    public void atualizar(String nome, String descricao, StatusTarefa status, String observacoes) {
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
        this.observacoes = observacoes;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
}
