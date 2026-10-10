# Spec: Finalização do Rebanho: status, reversão de morte e preservação do lote

> feature: rebanho-s1-t2-finalizacao
> status: auditada

## Contexto

A Task 1 disponibilizou a consulta de animais por `ATIVO`, `MORTO` e
`VENDIDO`. Esta task completa o ciclo de morte e reversão, preservando o lote
do animal e impedindo que animais mortos componham métricas operacionais.
Também conecta o filtro e as ações de status no frontend do Rebanho.

## Histórias

### US-201 — Corrigir o ciclo de morte e a composição operacional do lote

Como operador do rebanho, quero registrar e reverter uma morte sem perder o
lote original, para manter rastreabilidade e evitar que mortos sejam contados
como animais atuais.

#### AC-201 — Registrar morte preserva o lote

- **Dado** um animal `ATIVO` associado a um lote
- **Quando** a morte for registrada com uma data válida
- **Então** o animal passa a `MORTO`, recebe `dataMorte` e mantém o mesmo `loteId`

#### AC-202 — Reverter morte restaura o animal no mesmo lote

- **Dado** um animal `MORTO` com `dataMorte` e `loteId` preenchidos
- **Quando** a morte for revertida
- **Então** o animal passa a `ATIVO`, limpa `dataMorte` e mantém o mesmo `loteId`

#### AC-203 — Consulta de lote retorna somente animais ativos

- **Dado** um lote com animais `ATIVO` e `MORTO` associados
- **Quando** `buscarPorLote(loteId)` for executado
- **Então** somente os animais `ATIVO` são retornados

#### AC-204 — Métricas operacionais não incluem mortos

- **Dado** que quantidade e peso operacional usam `buscarPorLote(loteId)`
- **Quando** um lote possuir animais mortos associados
- **Então** os consumidores operacionais recebem somente a coleção de animais ativos

### US-202 — Operar a lista de animais por status

Como produtor, quero selecionar o status na tela do Rebanho, para consultar a
visão padrão, mortos ou vendidos e executar somente ações válidas para cada
status.

#### AC-205 — Serviço envia status e reverte morte

- **Dado** uma chamada ao serviço de listagem
- **Quando** o status for omitido ou for `ATIVO`, `MORTO` ou `VENDIDO`
- **Então** o Axios envia `tamanho=100` e, quando informado, `status` em `params`; a reversão usa `POST /v1/animais/{id}/reverter-morte`

#### AC-206 — Filtro de status consulta o backend e categoria permanece local

- **Dado** a página do Rebanho com filtro de status e filtro de categoria
- **Quando** o status for trocado
- **Então** uma nova listagem é carregada com o status selecionado, enquanto a categoria continua filtrando a coleção já recebida no frontend

#### AC-207 — Ações respeitam o status

- **Dado** um animal exibido na lista
- **Quando** suas ações forem renderizadas
- **Então** `ATIVO` permite editar, morte e exclusão; `MORTO` permite editar e reverter morte; `VENDIDO` permite somente editar

#### AC-208 — Badges distinguem os status

- **Dado** animais com status `ATIVO`, `MORTO` e `VENDIDO`
- **Quando** a tabela for renderizada
- **Então** os badges usam respectivamente estilos verde, cinza e âmbar

#### AC-209 — Data padrão da morte usa calendário local

- **Dado** que o operador abre o registro de morte
- **Quando** a data padrão for gerada
- **Então** ela será formatada como `YYYY-MM-DD` usando ano, mês e dia locais, sem conversão ISO para UTC

#### AC-210 — Lista sem resultados usa mensagem neutra

- **Dado** que os filtros selecionados não retornam animais
- **Quando** a tabela for renderizada
- **Então** a mensagem será `Nenhum animal encontrado para os filtros selecionados.` e não tratará o resultado vazio como erro do backend

#### AC-211 — Escopo preservado

- **Dado** o conjunto de alterações desta task
- **Quando** a implementação for revisada
- **Então** não haverá reversão de venda, coluna de lote anterior, migration ou dependência npm nova

## Fora de escopo

- Venda ou reversão de venda, histórico individual de lotes e novas migrations.
- Tela de Lotes, pesagens operacionais, Financeiro, Dashboard, Nutrição/Pastos e Reprodução.
- Novos pacotes npm e refactors não relacionados.

## Suposições

| ID | Suposição | Status | Resolução |
|---|---|---|---|
| ASM-201 | `buscarPorLote` é o contrato único usado por quantidade e peso operacional do lote. | confirmada | Confirmado pelos usos em `LoteController`, `CalcularConversaoAlimentarUseCase` e `CalcularCustoArrobaUseCase`. |
| ASM-202 | O `node:test` já usado no frontend é aceitável para testes de contrato sem instalar framework adicional. | confirmada | A validação usa a infraestrutura nativa já presente em `frontend/tests/client.test.js`. |

## Perguntas em aberto

| ID | Pergunta | Status | Resposta |
|---|---|---|---|
| Q-201 | Nenhuma pergunta em aberto para esta task. | respondida | A SPEC define explicitamente o comportamento de status, ações e preservação do lote. |
