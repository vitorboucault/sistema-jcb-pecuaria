-- Financeiro iniciado do zero: todos os lançamentos exigem descrição e situação.
ALTER TABLE transacao_financeira
    ALTER COLUMN tipo_centro_custo SET NOT NULL,
    ADD COLUMN descricao TEXT NOT NULL,
    ADD COLUMN situacao VARCHAR(20) NOT NULL,
    ADD COLUMN data_vencimento DATE,
    ADD COLUMN data_efetivacao DATE,
    ADD COLUMN origem_tipo VARCHAR(30),
    ADD COLUMN origem_id UUID;

ALTER TABLE transacao_financeira
    ADD CONSTRAINT ck_financeiro_situacao CHECK (
        btrim(descricao) <> ''
        AND ((situacao = 'PENDENTE' AND data_efetivacao IS NULL)
             OR (situacao = 'EFETIVADO' AND data_efetivacao IS NOT NULL))
    ),
    ADD CONSTRAINT ck_financeiro_origem CHECK (
        (origem_tipo IS NULL AND origem_id IS NULL)
        OR (origem_tipo IS NOT NULL AND origem_id IS NOT NULL)
    );

CREATE INDEX idx_financeiro_tipo_referencia ON transacao_financeira (tipo, data_transacao);
CREATE INDEX idx_financeiro_centro ON transacao_financeira (tipo, tipo_centro_custo, centro_custo_id);
