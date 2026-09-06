CREATE TABLE IF NOT EXISTS fornecimento_racao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lote_id UUID NOT NULL REFERENCES lote(id),
    data_fornecimento DATE NOT NULL,
    quantidade_kg DOUBLE PRECISION NOT NULL,
    teor_materia_seca DOUBLE PRECISION NOT NULL
    );

CREATE INDEX IF NOT EXISTS idx_fornecimento_lote_data ON fornecimento_racao (lote_id, data_fornecimento);