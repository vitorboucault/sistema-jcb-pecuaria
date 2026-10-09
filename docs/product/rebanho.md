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
  - `COMPRA`: Animal cuja origem conhecida é aquisição por compra, independentemente de a compra ter sido registrada operacionalmente pelo sistema ou informada posteriormente como dado histórico.
  - `NASCIMENTO`: Animal cuja origem conhecida é nascimento na propriedade.
  - `DESCONHECIDO`: Origem não conhecida com segurança.
- `dataNascimento`: Data de nascimento do animal.
- `dataMorte`: Data em que ocorreu o óbito (preenchida apenas se `status == MORTO`).

---

## 2. Ciclo de Vida e Operações

```
 [Cadastro inicial / Nascimento / Compra]
                 |
                 v
             +-------+
             | ATIVO | <----+ (Reversão de Morte)
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
- **Cadastro inicial**: É uma operação de cadastro de animal já existente, exposta por `POST /api/v1/animais/cadastrar`; não representa nova entrada física, compra operacional ou nascimento atual. Quando não houver informação confiável, o animal permanece com origem `DESCONHECIDO`.

O nascimento operacional continua exposto por `POST /api/v1/animais` e exige
data de nascimento e peso ao nascer. A compra operacional permanece disponível
no backend, mas não é oferecida pelo modal atual do Rebanho.

No cadastro inicial, `dataNascimento = null` significa nascimento desconhecido.
O campo pode ser corrigido posteriormente pela edição cadastral sem criar um
evento operacional.

A origem persistida é independente da operação de cadastro. Operações distintas
podem convergir para a mesma origem quando o histórico disponível for o mesmo,
e `origem = COMPRA` não significa, por si só, que uma compra operacional foi
registrada pelo sistema nem que houve efeito financeiro ou comercial. Somente o
fluxo operacional de compra pode gerar esses efeitos. Um animal antigo migrado
do caderno pode ter `origem = COMPRA` sem gerar novo lançamento financeiro. O
custo de aquisição pertence ao animal; custos futuros de lote
devem ser compostos a partir dos animais, sem acumular compras diretamente em
um campo mutável do lote.

Quando não houver custo ou data histórica confiável, a ausência permanece sem
ser substituída por zero ou por uma data fictícia.

#### Aquisição histórica do animal

`origem = COMPRA` não é sinônimo de compra operacional. No cadastro inicial,
`AquisicaoAnimal` guarda opcionalmente a data e/ou o valor conhecidos da
aquisição daquele animal. O valor histórico é o custo de aquisição do animal e
acompanha o animal quando ele muda de lote; ele não cria `Despesa`, lançamento
em `transacao_financeira` ou saída de caixa atual.

Se nenhum dado de aquisição for conhecido, o animal permanece com origem
`COMPRA` e sem registro `AquisicaoAnimal`. Data e valor são independentes:
informação ausente permanece `null`, sem preenchimento fictício.

### 2.2. Manejo e Pesagens
- **Pesagem Inicial**: Registrada com origem `OrigemPesagem.CADASTRO_INICIAL`.
- **Pesagens Operacionais**: Registradas com origem `OrigemPesagem.OPERACIONAL` para acompanhamento de manejo e cálculo de Ganho Médio Diário (GMD).
- **Movimentação de Lote**: `MovimentarAnimalUseCase` altera o `loteId`, sem registrar histórico individual de transferências. A tabela `movimentacao_lote` representa a permanência de lotes em pastos, não a passagem de animais por lotes. O histórico individual ainda é uma evolução futura.

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
