-- Adicionando colunas de auditoria na tabela clientes
ALTER TABLE clientes
ADD COLUMN data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN data_atualizacao TIMESTAMP,
ADD COLUMN criado_por VARCHAR(255) NOT NULL DEFAULT 'SISTEMA',
ADD COLUMN atualizado_por VARCHAR(255);

-- Adicionando colunas de auditoria na tabela empresas
ALTER TABLE empresas
ADD COLUMN data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN data_atualizacao TIMESTAMP,
ADD COLUMN criado_por VARCHAR(255) NOT NULL DEFAULT 'SISTEMA',
ADD COLUMN atualizado_por VARCHAR(255);

-- Adicionando colunas de auditoria na tabela usuarios
ALTER TABLE usuarios
ADD COLUMN data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN data_atualizacao TIMESTAMP,
ADD COLUMN criado_por VARCHAR(255) NOT NULL DEFAULT 'SISTEMA',
ADD COLUMN atualizado_por VARCHAR(255);

-- Criando tabela de bancos com auditoria
CREATE TABLE IF NOT EXISTS bancos (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    agencia VARCHAR(20) NOT NULL,
    conta VARCHAR(20) NOT NULL,
    chave_api VARCHAR(255),
    token_acesso VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    webhook_url VARCHAR(255),
    webhook_secret VARCHAR(255),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP,
    criado_por VARCHAR(255) NOT NULL,
    atualizado_por VARCHAR(255),
    FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

-- Criando tabela de transacoes com auditoria
CREATE TABLE IF NOT EXISTS transacoes (
    id BIGSERIAL PRIMARY KEY,
    banco_id BIGINT NOT NULL,
    empresa_id BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    valor DECIMAL(19,2) NOT NULL,
    data_transacao TIMESTAMP NOT NULL,
    descricao TEXT,
    id_transacao_banco VARCHAR(255),
    dados_adicionais TEXT,
    conciliada BOOLEAN NOT NULL DEFAULT FALSE,
    data_conciliacao TIMESTAMP,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP,
    criado_por VARCHAR(255) NOT NULL,
    atualizado_por VARCHAR(255),
    FOREIGN KEY (banco_id) REFERENCES bancos(id),
    FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

-- Criando tabela de conciliacoes com auditoria
CREATE TABLE IF NOT EXISTS conciliacoes (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    banco_id BIGINT NOT NULL,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP NOT NULL,
    concluida BOOLEAN NOT NULL DEFAULT FALSE,
    total_transacoes INTEGER,
    transacoes_conciliadas INTEGER,
    transacoes_pendentes INTEGER,
    data_conclusao TIMESTAMP,
    observacoes TEXT,
    arquivo_relatorio VARCHAR(255),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP,
    criado_por VARCHAR(255) NOT NULL,
    atualizado_por VARCHAR(255),
    FOREIGN KEY (empresa_id) REFERENCES empresas(id),
    FOREIGN KEY (banco_id) REFERENCES bancos(id)
);

-- Criando índices para melhorar performance de consultas
CREATE INDEX idx_transacoes_banco_id ON transacoes(banco_id);
CREATE INDEX idx_transacoes_empresa_id ON transacoes(empresa_id);
CREATE INDEX idx_transacoes_data ON transacoes(data_transacao);
CREATE INDEX idx_conciliacoes_empresa_id ON conciliacoes(empresa_id);
CREATE INDEX idx_conciliacoes_banco_id ON conciliacoes(banco_id);
CREATE INDEX idx_conciliacoes_data ON conciliacoes(data_inicio, data_fim);
