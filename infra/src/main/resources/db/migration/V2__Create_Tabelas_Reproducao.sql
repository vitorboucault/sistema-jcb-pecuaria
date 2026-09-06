-- 1. Tabela ESTACAO_MONTA (A Janela Reprodutiva)
CREATE TABLE estacao_monta (
                               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                               nome VARCHAR(100) NOT NULL, -- Ex: "Estaçao das Águas 2026"
                               data_inicio DATE NOT NULL,
                               data_fim DATE NOT NULL,
                               status VARCHAR(50) DEFAULT 'ABERTA'
);

-- 2. Tabela EVENTO_REPRODUTIVO (IATF, Monta Natural)
CREATE TABLE evento_reprodutivo (
                                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                    animal_id UUID NOT NULL REFERENCES animal(id),
                                    estacao_monta_id UUID NOT NULL REFERENCES estacao_monta(id),
                                    tipo_reproducao VARCHAR(50) NOT NULL,
                                    data_evento DATE NOT NULL,
                                    touro_id UUID REFERENCES animal(id), -- Quem foi o pai (se monta natural/repasse)
                                    observacao TEXT
);

-- 3. Tabela DIAGNOSTICO_GESTACAO (O Toque)
CREATE TABLE diagnostico_gestacao (
                                      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                      animal_id UUID NOT NULL REFERENCES animal(id),
                                      estacao_monta_id UUID NOT NULL REFERENCES estacao_monta(id),
                                      data_diagnostico DATE NOT NULL,
                                      resultado VARCHAR(50) NOT NULL, -- PRENHE, VAZIA, PERDA_GESTACIONAL
                                      data_provavel_parto DATE
);