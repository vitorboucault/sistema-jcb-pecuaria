# Domínio: Rebanho

O domínio de **Rebanho** é responsável pelo ciclo de vida completo dos animais na fazenda, desde a entrada (nascimento ou aquisição) até a saída (morte ou venda), controlando identificação, pesagens e movimentação por lotes.

---

## 1. Entidade Animal

### Atributos Principais
- `id`: UUID único.
- `brincoRgd`: Código identificador visual ou genealógico obrigatório.
- `sexo`: `MACHO` ou `FEMEA`.
- `categoriaAtual`: `BEZERRO`, `BEZERRA`, `GARROTE`, `NOVILHA`, `VACA`, `TOURO`, `BOI`.
- `status`: Estado atual do animal no rebanho:
  - `ATIVO`: Presente na fazenda e participando do manejo regular.
  - `MORTO`: Baixado por óbito.
  - `VENDIDO`: Comercializado.
- `loteId`: Identificador do lote atual ou do lote preservado durante uma baixa por morte.
- `dataNascimento`: Data de nascimento do animal.
- `dataMorte`: Data em que ocorreu o óbito (preenchida apenas se `status == MORTO`).

---

## 2. Ciclo de Vida e Operações

```
        [Nascimento / Compra]
                 |
                 v
             +-------+
             | ATIVO | <----+ (Reversão de Morte / Venda)
             +-------+      |
              |     |       |
      (Morte) |     | (Venda)
              v     v       |
          +-------+ +---------+
          | MORTO | | VENDIDO |
          +-------+ +---------+
```

### 2.1. Entrada no Rebanho
- **Nascimento**: Registrado via `RegistrarNascimentoUseCase`, vinculando opcionalmente mãe e lote de cria.
- **Compra**: Registrado via `RegistrarCompraAnimalUseCase`, com valor de aquisição, data e peso de entrada.

### 2.2. Manejo e Pesagens
- **Pesagem Inicial**: Registrada com origem `OrigemPesagem.CADASTRO_INICIAL`.
- **Pesagens Operacionais**: Registradas com origem `OrigemPesagem.OPERACIONAL` para acompanhamento de manejo e cálculo de Ganho Médio Diário (GMD).
- **Movimentação de Lote**: `MovimentarAnimalUseCase` altera o `loteId` e registra o histórico na tabela de movimentações.

### 2.3. Baixa por Morte e Reversão
- **Registrar Morte**:
  - Exige `status == ATIVO`.
  - Altera status para `MORTO`, define `dataMorte` e preserva o `loteId` para manter a rastreabilidade da perda no lote.
- **Reverter Morte**:
  - Exige `status == MORTO`.
  - Altera status de volta para `ATIVO`, limpa `dataMorte` e preserva o mesmo `loteId`.

Animais mortos mantêm o vínculo histórico com o lote, mas não participam da
composição operacional do lote. Consultas de animais atuais do lote retornam
somente animais com `status == ATIVO`.
