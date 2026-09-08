# Domínio: Vendas

O domínio de **Vendas** gerencia a saída comercial de animais do rebanho, calculando receitas de acordo com diferentes modalidades de comercialização e permitindo a reversão em caso de lançamentos indevidos.

---

## 1. Entidade VendaAnimal

### Atributos
- `id`: UUID da venda.
- `animalId`: Identificador do animal comercializado.
- `dataVenda`: Data do evento de venda.
- `modalidade`: Modalidade comercial da transação (`ModalidadeVenda`):
  - `FRIGORIFICO`: Venda baseada em rendimento de carcaça. A receita é calculada a partir do rendimento sobre o peso vivo dividido por 15 kg/@ e multiplicada pelo preço da arroba acordado. Exige `rendimentoCarcacaPercentual`.
  - `PESO`: Venda baseada em arroba de peso vivo (total de arrobas = `pesoVivoKg / 30.0`) multiplicada pelo preço acordado.
  - `POR_CABECA`: Valor fixo acordado por animal.
- `pesoVivoKg`: Peso do animal no momento da venda.
- `rendimentoCarcacaPercentual`: Percentual de rendimento de carcaça (obrigatório para `FRIGORIFICO`).
- `precoAcordado`: Valor monetário acordado (preço por arroba ou preço total por cabeça).

---

## 2. Regras de Negócio e Operações

### 2.1. Registro de Venda (`RegistrarVendaAnimalUseCase`)
- **Pré-condição**: O animal deve estar com status `ATIVO`.
- **Efeitos colaterais**:
  - O animal tem seu status alterado para `VENDIDO`.
  - O animal é desvinculado de seu lote atual (`loteId = null`).
  - Um registro de `VendaAnimal` é persistido na base.

### 2.2. Reversão de Venda (`ReverterVendaAnimalUseCase`)
- **Pré-condição**: O animal deve existir e possuir status `VENDIDO`. Deve haver exatamente um registro de venda correspondente.
- **Efeitos colaterais**:
  - O animal tem seu status revertido para `ATIVO` via `animal.reverterVenda()`.
  - O registro de `VendaAnimal` associado é excluído da base.
  - O animal permanece desvinculado de lote (`loteId = null`), permitindo ao operador destiná-lo ao lote correto de manejo.
