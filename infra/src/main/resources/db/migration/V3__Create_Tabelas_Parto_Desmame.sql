-- 1. Tabela PARTO (O nascimento)
CREATE TABLE parto (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       matriz_id UUID NOT NULL REFERENCES animal(id),
                       bezerro_id UUID REFERENCES animal(id), -- Null se nasceu morto
                       estacao_monta_id UUID NOT NULL REFERENCES estacao_monta(id),
                       data_parto DATE NOT NULL,
                       tipo VARCHAR(50) NOT NULL, -- NORMAL ou DISTOCICO
                       condicao VARCHAR(50) NOT NULL, -- VIVO ou MORTO
                       peso_nascimento_kg DOUBLE PRECISION
);

-- 2. Tabela DESMAME (Onde o bezerro vira dinheiro potencial)
CREATE TABLE desmame (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         bezerro_id UUID NOT NULL REFERENCES animal(id),
                         estacao_monta_id UUID NOT NULL REFERENCES estacao_monta(id),
                         data_nascimento DATE NOT NULL,
                         data_desmame DATE NOT NULL,
                         peso_nascimento DOUBLE PRECISION NOT NULL,
                         peso_desmame DOUBLE PRECISION NOT NULL
);