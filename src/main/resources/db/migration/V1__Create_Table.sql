-- Habilita a extensão para geração automática de UUIDs no PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Tabela PASTO
CREATE TABLE pasto (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       nome VARCHAR(100) NOT NULL,
                       area_hectares DOUBLE PRECISION NOT NULL,
                       capacidade_suporte_ua DOUBLE PRECISION,
                       status_atual VARCHAR(50) DEFAULT 'PASTEJO'
);

-- 2. Tabela LOTE
CREATE TABLE lote (
                      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                      nome VARCHAR(100) NOT NULL,
                      fase_lote VARCHAR(50) NOT NULL, -- CRIA, RECRIA, ENGORDA
                      data_formacao DATE NOT NULL,
                      data_encerramento DATE
);

-- 3. Tabela ANIMAL (O Core)
CREATE TABLE animal (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        brinco_rgd VARCHAR(50) UNIQUE NOT NULL,
                        data_nascimento DATE NOT NULL,
                        sexo VARCHAR(10) NOT NULL,
                        categoria_atual VARCHAR(50) NOT NULL,
                        status VARCHAR(50) DEFAULT 'ATIVO',
                        mae_id UUID REFERENCES animal(id),
                        pai_id UUID REFERENCES animal(id),
                        lote_atual_id UUID REFERENCES lote(id)
);

-- 4. Tabela MOVIMENTACAO_LOTE (Rastreabilidade de Pasto)
CREATE TABLE movimentacao_lote (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   lote_id UUID NOT NULL REFERENCES lote(id),
                                   pasto_id UUID NOT NULL REFERENCES pasto(id),
                                   data_entrada DATE NOT NULL,
                                   data_saida DATE
);

-- 5. Tabela PESAGEM (Base para o GMD)
CREATE TABLE pesagem (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         animal_id UUID NOT NULL REFERENCES animal(id),
                         data_pesagem DATE NOT NULL,
                         peso_kg DOUBLE PRECISION NOT NULL
);

-- 6. Tabela EVENTO_ZOOTECNICO
CREATE TABLE evento_zootecnico (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   animal_id UUID NOT NULL REFERENCES animal(id),
                                   tipo_evento VARCHAR(50) NOT NULL,
                                   data_evento DATE NOT NULL,
                                   observacao TEXT
);

-- 7. Tabela TRANSACAO_FINANCEIRA
CREATE TABLE transacao_financeira (
                                      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                      tipo VARCHAR(20) NOT NULL, -- RECEITA, DESPESA
                                      data_transacao DATE NOT NULL,
                                      valor NUMERIC(12,2) NOT NULL,
                                      categoria VARCHAR(50) NOT NULL,
                                      centro_custo_id UUID, -- Polimórfico
                                      tipo_centro_custo VARCHAR(50) -- LOTE, PASTO, FAZENDA
);