CREATE TABLE venda_animal (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              animal_id UUID NOT NULL REFERENCES animal(id),
                              data_venda DATE NOT NULL,
                              modalidade VARCHAR(50) NOT NULL,
                              peso_vivo_kg DOUBLE PRECISION NOT NULL,
                              rendimento_carcaca_percentual DOUBLE PRECISION,
                              preco_acordado NUMERIC(15,2) NOT NULL
);