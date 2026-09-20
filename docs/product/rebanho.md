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
- `origem`: Origem persistida do animal:
  - `COMPRA`: Animal introduzido por uma compra operacional.
  - `NASCIMENTO`: Animal registrado como nascido na fazenda.
  - `DESCONHECIDO`: Não há informação histórica confiável sobre a origem.
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
- **Cadastro inicial**: É uma operação de cadastro, não um valor de `origem`. Quando não houver informação confiável, o animal permanece com origem `DESCONHECIDO`.

A origem persistida é independente da operação de cadastro. Operações distintas
podem convergir para a mesma origem quando o histórico disponível for o mesmo,
mas somente a compra operacional gera efeitos financeiros ou comerciais de
aquisição. O custo de aquisição pertence ao animal; custos futuros de lote
devem ser compostos a partir dos animais, sem acumular compras diretamente em
um campo mutável do lote.

Quando não houver custo ou data histórica confiável, a ausência permanece sem
ser substituída por zero ou por uma data fictícia.

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
