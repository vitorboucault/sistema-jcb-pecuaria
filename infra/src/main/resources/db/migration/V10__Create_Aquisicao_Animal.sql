CREATE TABLE aquisicao_animal (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    animal_id UUID NOT NULL,
    data_aquisicao DATE,
    valor_aquisicao NUMERIC(12, 2),
    CONSTRAINT uq_aquisicao_animal_animal UNIQUE (animal_id),
    CONSTRAINT ck_aquisicao_animal_dado_conhecido
        CHECK (data_aquisicao IS NOT NULL OR valor_aquisicao IS NOT NULL),
    CONSTRAINT ck_aquisicao_animal_valor_positivo
        CHECK (valor_aquisicao IS NULL OR valor_aquisicao > 0),
    CONSTRAINT fk_aquisicao_animal_animal
        FOREIGN KEY (animal_id) REFERENCES animal(id) ON DELETE CASCADE
);

CREATE INDEX idx_aquisicao_animal_animal_id ON aquisicao_animal (animal_id);
