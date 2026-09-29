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

### US-314 — Registrar peso conhecido no cadastro inicial

Como produtor, quero informar o peso atual conhecido de um animal ao
transferi-lo do caderno para o sistema, para iniciar o acompanhamento de peso
sem inventar um peso de compra ou de nascimento.

#### AC-334 — Cadastro com peso cria pesagem inicial

- **Dado** um animal válido para cadastro inicial com `pesoAtual` e
  `dataPesagem` informados
- **Quando** o cadastro for concluído
- **Então** o animal será salvo e uma `Pesagem` será criada com o peso
  informado e origem `OrigemPesagem.CADASTRO_INICIAL`

#### AC-335 — Peso atual é opcional

- **Dado** um animal válido para cadastro inicial com `pesoAtual` e
  `dataPesagem` ausentes
- **Quando** o cadastro inicial for realizado
- **Então** o animal será salvo sem criar uma pesagem

#### AC-336 — Peso e data formam um par

- **Dado** que apenas `pesoAtual` ou apenas `dataPesagem` foi informado
- **Quando** o cadastro inicial for solicitado
- **Então** a operação será rejeitada e nenhum animal ou pesagem parcial
  permanecerá persistido

#### AC-337 — Pesagem inválida é rejeitada

- **Dado** um cadastro inicial com peso menor ou igual a zero, data futura ou
  data de pesagem anterior à data de nascimento
- **Quando** o cadastro inicial for solicitado
- **Então** a operação será rejeitada antes da persistência de animal ou
  pesagem

#### AC-338 — Cadastro e pesagem são atômicos

- **Dado** um cadastro inicial com pesagem informada
- **Quando** ocorrer falha ao persistir a pesagem
- **Então** o animal e a pesagem não permanecerão parcialmente persistidos

#### AC-339 — Peso atual não altera a origem do animal

- **Dado** qualquer `OrigemAnimal` válida e, opcionalmente, um peso atual
- **Quando** o cadastro inicial for processado
- **Então** a origem do animal permanecerá a informada e o peso será registrado
  exclusivamente no histórico de `Pesagem`

### US-315 — Integrar o cadastro inicial ao frontend

Como produtor, quero escolher entre cadastrar um animal já existente e
registrar um novo nascimento no modal do rebanho, para migrar o plantel sem
confundir histórico de origem com a operação de nascimento ou de compra.

#### AC-340 — Modal oferece somente os fluxos previstos

- **Dado** que o modal de cadastro de animal seja aberto
- **Quando** os fluxos disponíveis forem apresentados
- **Então** ele oferecerá `Animal já existente` e `Novo nascimento`, sem
  oferecer uma operação de compra ou aquisição

#### AC-341 — Cadastro histórico oferece origem e lote opcionais

- **Dado** que o fluxo `Animal já existente` esteja selecionado
- **Quando** o formulário for preenchido
- **Então** a origem histórica oferecerá `COMPRA`, `NASCIMENTO` e
  `DESCONHECIDO`, começará em `DESCONHECIDO` e o lote terá `Sem lote` como
  primeira opção sem seleção automática

#### AC-342 — Cadastro histórico usa o endpoint dedicado

- **Dado** um animal já existente válido
- **Quando** o cadastro for enviado pelo frontend
- **Então** `rebanhoService.cadastrarAnimalInicial()` chamará
  `POST /api/v1/animais/cadastrar`

#### AC-343 — Peso atual histórico é enviado com sua data

- **Dado** que o fluxo `Animal já existente` tenha peso atual e data da
  pesagem informados
- **Quando** o cadastro for enviado
- **Então** o payload conterá `pesoAtual` e `dataPesagem` juntos

#### AC-344 — Peso atual e data da pesagem formam um par no formulário

- **Dado** que somente o peso atual ou somente a data da pesagem tenha sido
  informado
- **Quando** o formulário for submetido
- **Então** o cadastro não será chamado e o formulário exibirá um erro de
  preenchimento do par

#### AC-345 — Nascimento usa o endpoint operacional

- **Dado** que o fluxo `Novo nascimento` esteja selecionado
- **Quando** o nascimento for enviado
- **Então** o frontend chamará `POST /api/v1/animais` com o peso ao nascer,
  a matriz e o lote do nascimento quando informados, sem enviar
  `pesoAtual`, `dataPesagem` ou dados de compra

#### AC-346 — Matriz fica restrita às matrizes elegíveis

- **Dado** que animais ativos sejam carregados para o formulário de
  nascimento
- **Quando** a lista de matrizes for apresentada
- **Então** somente fêmeas ativas das categorias `VACA` ou `NOVILHA` serão
  selecionáveis

#### AC-347 — Payloads permanecem isolados ao alternar o fluxo

- **Dado** que o usuário alterne entre `Animal já existente` e `Novo
  nascimento`
- **Quando** cada fluxo for submetido
- **Então** cada chamada conterá somente os campos do seu contrato, sem
  transportar `maeId`, dados de compra ou campos de pesagem do outro fluxo

#### AC-348 — Cadastro bem-sucedido recarrega os dados uma única vez

- **Dado** que qualquer um dos fluxos de cadastro conclua com sucesso
- **Quando** o frontend atualizar a tela
- **Então** `recarregarDados()` será executado uma única vez após a chamada
  correspondente

### US-316 — Registrar dados históricos de aquisição

Como produtor, quero registrar os dados conhecidos da aquisição de um animal
já existente, para preservar seu custo econômico sem lançar uma compra atual
nem contaminar o Financeiro.

#### AC-349 — Compra histórica pode existir sem data ou valor conhecidos

- **Dado** um cadastro inicial válido com origem `COMPRA` e sem data ou valor
  histórico
- **Quando** o animal for cadastrado
- **Então** o `Animal` será salvo com origem `COMPRA`, nenhuma
  `AquisicaoAnimal` será criada e nenhuma transação financeira será gerada

#### AC-350 — Dados conhecidos de compra geram `AquisicaoAnimal`

- **Dado** um cadastro inicial válido com origem `COMPRA`, data e valor
  históricos válidos
- **Quando** o animal for cadastrado
- **Então** será criada uma única `AquisicaoAnimal` referenciando o animal
  correto e preservando os dados informados

#### AC-351 — Data e valor históricos são opcionais independentemente

- **Dado** um cadastro inicial com origem `COMPRA`
- **Quando** somente a data ou somente o valor histórico for conhecido
- **Então** o cadastro será aceito e a `AquisicaoAnimal` preservará o campo
  conhecido, mantendo o outro como `null`

#### AC-352 — Dados de aquisição são permitidos somente para origem `COMPRA`

- **Dado** um cadastro inicial com origem `NASCIMENTO` ou `DESCONHECIDO`
  e algum dado histórico de aquisição
- **Quando** o cadastro for solicitado
- **Então** a operação será rejeitada antes de qualquer persistência

#### AC-353 — Dados históricos inválidos são rejeitados

- **Dado** um cadastro inicial com valor menor ou igual a zero, data futura ou
  data de aquisição anterior à data de nascimento
- **Quando** o cadastro for solicitado
- **Então** a operação será rejeitada antes de persistir animal, pesagem ou
  aquisição

#### AC-354 — Aquisição histórica não gera lançamento financeiro

- **Dado** um cadastro inicial com origem `COMPRA` e valor histórico conhecido
- **Quando** o animal for cadastrado
- **Então** o número de linhas em `transacao_financeira` permanecerá inalterado

#### AC-355 — Animal, pesagem e aquisição são persistidos atomicamente

- **Dado** um cadastro inicial com pesagem e aquisição histórica
- **Quando** ocorrer falha ao salvar a `AquisicaoAnimal`
- **Então** o animal, a pesagem e a aquisição não permanecerão parcialmente
  persistidos

#### AC-356 — Exclusão física não deixa aquisição órfã

- **Dado** um animal com `AquisicaoAnimal` cadastrado
- **Quando** o animal for excluído fisicamente por correção de cadastro
- **Então** a aquisição histórica correspondente também será removida

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
- `pesoAtual` e `dataPesagem` são campos opcionais do cadastro inicial, mas
  formam um par: ambos ausentes significam nenhuma pesagem; ambos presentes
  criam uma `Pesagem` com origem `OrigemPesagem.CADASTRO_INICIAL`.
- `pesoAtual` não será persistido em `Animal`. O peso atual exibido continuará
  sendo derivado da última `Pesagem` conhecida.
- O cadastro inicial rejeita peso menor ou igual a zero, data futura e data de
  pesagem anterior à data de nascimento. A ausência de peso permanece como
  `null`; nenhum peso ou data será inventado.
- Animal e pesagem inicial devem ser persistidos dentro de uma única transação
  do caso de uso. Falha na pesagem deve desfazer também o cadastro do animal.
- A T-504 reutiliza `Pesagem`, `OrigemPesagem` e o repositório de pesagens.
  Não cria entidade, migration ou regra nova de jejum. O comportamento atual
  exigido pelo modelo para `jejum` será preservado até uma decisão específica.
- O peso informado nesta task é peso atual conhecido na implantação, mesmo
  quando `OrigemAnimal` for `COMPRA`; não representa peso de aquisição,
  nascimento ou entrada na fazenda.
- A persistência é introduzida pela migration Flyway V9. Registros legados sem
  origem confiável são preenchidos com `DESCONHECIDO`.
- O custo de aquisição pertence ao animal. A composição futura do custo do
  lote deverá partir dos animais e não acumular uma compra diretamente em um
  campo mutável do lote.
- `AquisicaoAnimal` é a relação opcional 1:0..1 que guarda os dados econômicos
  conhecidos da aquisição do animal; `dataCompraHistorica` e
  `valorCompraHistorico` não são colunas de `Animal`.
- Data e valor históricos são opcionais e independentes. Quando ambos forem
  desconhecidos, nenhuma `AquisicaoAnimal` será criada; nunca serão usados
  zero ou a data atual como substitutos.
- Dados históricos de aquisição somente são aceitos quando a origem do animal
  for `COMPRA`. O valor histórico é custo de aquisição do animal, não uma
  `Despesa`, lançamento financeiro ou saída de caixa atual.
- A aquisição histórica e a pesagem inicial participam da mesma transação do
  cadastro. A migration V10 cria a tabela com valor positivo, ao menos um
  dado conhecido, unicidade por animal e FK com `ON DELETE CASCADE`.
- A ausência de custo ou de data histórica será representada pela ausência de
  informação definida pelo modelo, nunca por zero ou por uma data fictícia.
- O frontend separa `FluxoCadastroAnimal` (operação `EXISTENTE` ou
  `NASCIMENTO`) de `OrigemAnimal` (histórico `COMPRA`, `NASCIMENTO` ou
  `DESCONHECIDO`). O fluxo `EXISTENTE` inicia com origem `DESCONHECIDO`.
- O cadastro histórico usa `POST /api/v1/animais/cadastrar`, sem acionar a
  compra operacional. O fluxo de nascimento preserva `POST /api/v1/animais`.
- O lote é opcional nos dois fluxos e não recebe seleção automática. O fluxo
  histórico pode enviar `pesoAtual` e `dataPesagem` somente em conjunto; o
  fluxo de nascimento envia somente seu peso ao nascer e a data do nascimento
  como data da pesagem operacional existente.

## Fora de escopo

- `TipoEntradaAnimal` e `OrigemHistoricaAnimal`.
- Frontend fora do modal coberto pela US-315, Financeiro e custos de lote.
- Importação por planilha.
- Alterar a regra de negócio do peso de nascimento está fora de escopo; o campo
  existente e preservado pelo fluxo `Novo nascimento` continua válido.
- Peso de compra e campos de custo calculado diretamente em `Animal`.
- Edição de pesagem, novo campo de jejum e qualquer alteração visual no
  frontend fora do modal coberto pela US-315.
- A compra operacional no frontend, a apresentação dos campos históricos no
  modal e qualquer alteração nos contratos operacionais fora do necessário
  para o fluxo de nascimento.
- Alterações em migrations já existentes e `OrigemAnimal`.
- Alterações nos endpoints operacionais existentes, especialmente
  `POST /api/v1/animais`, e em `AnimalInputDTO`.

## Suposições

| ID | Suposição | Status | Resolução |
|---|---|---|---|
| ASM-501 | O caso de uso específico de cadastro inicial não faz parte da T-501. | confirmada | Limite histórico da T-501 preservado. A operação dedicada foi implementada na T-502 e exposta pela API na T-503. |
| ASM-502 | A categoria informada no cadastro inicial é histórica e confiável. | confirmada | O cadastro inicial a preserva sem inferi-la a partir da idade. |
| ASM-503 | A resposta de sucesso do cadastro inicial seguirá o padrão do endpoint de animais atual. | confirmada | A API retorna `201 Created` com o UUID do animal no corpo. |
| ASM-504 | O comportamento atual exigido pelo campo `jejum` deve ser preservado, sem nova decisão de domínio nesta task. | confirmada | A T-504 trata somente peso, data da pesagem e `OrigemPesagem.CADASTRO_INICIAL`; não adiciona campo nem altera a semântica de jejum. |
| ASM-505 | O endpoint oficial da T-504 é `POST /api/v1/animais/cadastrar`. | confirmada | A T-503 já definiu e implementou esse contrato; a menção a `/cadastro` foi descartada como divergência textual. |
| ASM-506 | O custo e a data históricos precisam de ciclo próprio, separado de `Animal` e `Despesa`. | confirmada | A T-506 usa `AquisicaoAnimal`; o dado econômico acompanha o animal sem criar movimentação financeira atual. |

## Perguntas em aberto

| ID | Pergunta | Status | Resposta |
|---|---|---|---|
| Q-501 | Nenhuma pergunta em aberto para esta especificação. | respondida | O conjunto de valores, a separação entre origem e operação e os limites da implementação foram definidos no requisito. |
