# Spec: Modelar origem do animal

> feature: rebanho-s2-t1-origem-animal
> status: em-implementacao

## Contexto

O domínio de Rebanho precisa distinguir a origem persistida do animal da
operação que o cadastrou. Um animal pode ser conhecido por ter sido comprado,
ter nascido na fazenda ou ter origem ainda desconhecida, sem que essa
informação seja confundida com a operação de cadastro inicial.

Esta evolução introduz a origem como dado de domínio e persistência, mantendo
as operações que cadastram o animal separadas desse atributo. O cadastro
inicial representa a migração do plantel já existente para o sistema, sem
representar uma nova compra, nascimento ou movimentação financeira.

## Histórias

### US-310 — Identificar a origem do animal

Como produtor, quero identificar a origem de cada animal, para preservar a
rastreabilidade do rebanho sem inventar informações históricas nem misturar a
origem com a operação usada para cadastrá-lo.

#### AC-316 — Origem possui conjunto fechado de valores

- **Dado** o atributo de origem de um animal
- **Quando** seus valores válidos forem consultados
- **Então** ele aceitará somente `COMPRA`, `NASCIMENTO` ou `DESCONHECIDO`

#### AC-317 — Cadastro inicial é operação, não origem

- **Dado** um animal incluído por uma operação de `CADASTRO_INICIAL`
- **Quando** sua origem persistida for determinada
- **Então** `CADASTRO_INICIAL` não será armazenado como origem e o animal
  receberá a origem conhecida pela informação disponível ou
  `DESCONHECIDO` quando ela não existir

#### AC-318 — Origem desconhecida não inventa histórico

- **Dado** um animal cuja origem não possa ser determinada
- **Quando** ele for persistido ou consultado
- **Então** sua origem permanecerá `DESCONHECIDO`, sem presunções, custo zero,
  datas fictícias ou outros valores usados para simular um histórico ausente

#### AC-319 — Origem de compra não implica operação financeira

- **Dado** um animal cuja origem persistida seja `COMPRA`
- **Quando** a origem for interpretada separadamente da operação que registrou
  o animal
- **Então** `COMPRA` significará aquisição por compra conhecida, mas não provará
  por si só que uma compra operacional foi registrada nem que houve efeito
  financeiro ou comercial; somente o fluxo operacional de compra poderá gerar
  esses efeitos

O caminho de um animal antigo cuja origem `COMPRA` seja informada como dado
histórico, sem uma compra operacional no sistema, é comprovado pelo caso de
uso de cadastro inicial descrito a seguir.

### US-311 — Cadastrar animal já existente no plantel

Como produtor, quero cadastrar um animal que já fazia parte do plantel antes
da implantação do sistema, para migrar seu histórico conhecido sem criar uma
compra operacional, nascimento atual ou movimentação financeira.

#### AC-320 — Cadastro inicial preserva a origem informada

- **Dado** um animal histórico válido com origem conhecida `NASCIMENTO`
- **Quando** ele for cadastrado inicialmente
- **Então** será salvo como `ATIVO`, com a categoria, sexo e lote informados,
  mãe e data de morte nulas, e origem `NASCIMENTO`

#### AC-321 — Cadastro inicial aceita origem desconhecida

- **Dado** um animal histórico válido cuja origem não seja conhecida
- **Quando** ele for cadastrado inicialmente com origem `DESCONHECIDO`
- **Então** será salvo com origem `DESCONHECIDO`, sem presumir compra ou
  nascimento

#### AC-322 — Cadastro inicial com compra histórica não gera efeito financeiro

- **Dado** um animal histórico válido com origem `COMPRA`
- **Quando** ele for cadastrado inicialmente
- **Então** será salvo com origem `COMPRA` sem criar despesa, lançamento
  financeiro ou acionar o fluxo de compra operacional

#### AC-323 — Cadastro inicial rejeita brinco duplicado

- **Dado** que já exista um animal com o mesmo brinco/RGD
- **Quando** for tentado o cadastro inicial
- **Então** a operação será rejeitada e nenhum novo animal será salvo

#### AC-324 — Cadastro inicial rejeita dados inválidos

- **Dado** um cadastro inicial com brinco vazio, data de nascimento nula ou
  futura, sexo nulo, categoria nula ou origem nula
- **Quando** o cadastro for solicitado
- **Então** a operação será rejeitada antes da persistência

### US-312 — Expor cadastro inicial pela API

Como produtor, quero registrar pela API um animal que já fazia parte do
plantel antes da implantação do sistema, para migrar o caderno sem acionar os
fluxos operacionais de compra ou nascimento.

#### AC-325 — Endpoint registra animal inicial e retorna 201

- **Dado** um request de cadastro inicial válido
- **Quando** for enviado para `POST /api/v1/animais/cadastrar`
- **Então** o endpoint delegará para `RegistrarAnimalInicialUseCase` e
  retornará `201 Created` com o UUID do animal criado

#### AC-326 — Endpoint preserva a origem informada

- **Dado** um request de cadastro inicial com origem `COMPRA`
- **Quando** ele for enviado ao endpoint de cadastro
- **Então** o command manterá a origem `COMPRA` e somente
  `RegistrarAnimalInicialUseCase` será utilizado

#### AC-327 — Lote é opcional no cadastro inicial pela API

- **Dado** um request de cadastro inicial sem `loteId`
- **Quando** ele for enviado ao endpoint de cadastro
- **Então** será aceito e o command receberá `loteId` nulo

#### AC-328 — Request inválido não executa o cadastro inicial

- **Dado** um request com brinco vazio, data de nascimento, sexo, categoria ou
  origem ausentes, ou origem inválida
- **Quando** ele for enviado ao endpoint de cadastro
- **Então** a API retornará `400 Bad Request` sem executar o caso de uso

## Decisões de modelagem

- `OrigemAnimal` é um enum de domínio associado à entidade `Animal`.
- `CADASTRO_INICIAL` representa uma operação de cadastro e não será incluído
  no enum de origem.
- A origem persistida será separada da operação que criou ou atualizou o
  cadastro.
- Os casos de uso de compra, nascimento e cadastro inicial atribuem a origem
  correspondente à informação disponível, sem transformar o nome da operação
  em origem.
- `origem = COMPRA` não implica que a compra foi registrada operacionalmente;
  `RegistrarAnimalInicialUseCase` registra a compra histórica sem efeito
  financeiro ou comercial.
- `POST /api/v1/animais/cadastrar` é a entrada REST específica para cadastro
  inicial e delega exclusivamente para `RegistrarAnimalInicialUseCase`.
- A persistência é introduzida pela migration Flyway V9. Registros legados sem
  origem confiável são preenchidos com `DESCONHECIDO`.
- O custo de aquisição pertence ao animal. A composição futura do custo do
  lote deverá partir dos animais e não acumular uma compra diretamente em um
  campo mutável do lote.
- A ausência de custo ou de data histórica será representada pela ausência de
  informação definida pelo modelo, nunca por zero ou por uma data fictícia.

## Fora de escopo

- `TipoEntradaAnimal` e `OrigemHistoricaAnimal`.
- Frontend, Financeiro e custos de lote.
- Importação por planilha.
- Datas estimadas e valores históricos de compra.
- Pesagem inicial, data de compra, valor de compra e campos de custo no
  animal.
- Alterações em migrations já existentes e `OrigemAnimal`.
- Alterações nos endpoints operacionais existentes, especialmente
  `POST /api/v1/animais`, e em `AnimalInputDTO`.

## Suposições

| ID | Suposição | Status | Resolução |
|---|---|---|---|
| ASM-501 | O caso de uso específico de cadastro inicial não faz parte da T-501. | confirmada | Limite histórico da T-501 preservado. A operação dedicada foi implementada na T-502 e exposta pela API na T-503. |
| ASM-502 | A categoria informada no cadastro inicial é histórica e confiável. | confirmada | O cadastro inicial a preserva sem inferi-la a partir da idade. |
| ASM-503 | A resposta de sucesso do cadastro inicial seguirá o padrão do endpoint de animais atual. | confirmada | A API retorna `201 Created` com o UUID do animal no corpo. |

## Perguntas em aberto

| ID | Pergunta | Status | Resposta |
|---|---|---|---|
| Q-501 | Nenhuma pergunta em aberto para esta especificação. | respondida | O conjunto de valores, a separação entre origem e operação e os limites da implementação foram definidos no requisito. |
