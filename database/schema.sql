CREATE TABLE IF NOT EXISTS tarefas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    descricao VARCHAR(1000) NOT NULL,
    status VARCHAR(20) NOT NULL,
    observacoes VARCHAR(2000),
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL,
    CONSTRAINT ck_tarefas_status
        CHECK (status IN ('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA'))
);

CREATE INDEX IF NOT EXISTS idx_tarefas_status ON tarefas (status);
CREATE INDEX IF NOT EXISTS idx_tarefas_data_criacao ON tarefas (data_criacao DESC);
