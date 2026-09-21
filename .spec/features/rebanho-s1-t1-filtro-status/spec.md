# Spec: Filtro de status na listagem de animais

> feature: rebanho-s1-t1-filtro-status
> status: auditada

## Contexto

O endpoint `GET /api/v1/animais` lista animais paginados. Sem filtro, a
consulta mantém a visualização atual de animais `ATIVO` e de animais `MORTO`
com morte nos últimos 12 meses, sem incluir `VENDIDO`. O produtor precisa
consultar cada status separadamente.

## Histórias

### US-101 — Consultar animais por status

Como produtor, quero filtrar a listagem de animais por status, para consultar
ativos, mortos e vendidos separadamente sem perder o comportamento padrão atual.

#### AC-101 — Listagem sem filtro preserva a consulta padrão

- **Dado** uma requisição sem o parâmetro `status`, com `pagina=0` e `tamanho=10`
- **Quando** a listagem de animais for executada
- **Então** a porta `buscarTodosPaginado(0, 10)` é usada e a consulta por status não é usada

#### AC-102 — Filtro de ativos

- **Dado** uma requisição com `status=ATIVO`, `pagina=0` e `tamanho=10`
- **Quando** a listagem de animais for executada
- **Então** somente a operação `buscarPorStatusPaginado(Status.ATIVO, 0, 10)` é usada

#### AC-103 — Filtro de mortos sem limite temporal

- **Dado** uma requisição com `status=MORTO`, `pagina=0` e `tamanho=10`
- **Quando** a listagem de animais for executada
- **Então** a operação `buscarPorStatusPaginado(Status.MORTO, 0, 10)` é usada

#### AC-104 — Filtro de vendidos

- **Dado** uma requisição com `status=VENDIDO`, `pagina=0` e `tamanho=10`
- **Quando** a listagem de animais for executada
- **Então** a operação `buscarPorStatusPaginado(Status.VENDIDO, 0, 10)` é usada

#### AC-105 — Status inválido retorna bad request sem consulta

- **Dado** uma requisição com `status=INVALIDO`
- **Quando** o endpoint receber a requisição
- **Então** a resposta será HTTP 400 e nenhuma operação de consulta do repositório será executada

#### AC-106 — Paginação filtrada preserva metadados

- **Dado** que a consulta filtrada retorna uma página com conteúdo e metadados de paginação
- **Quando** a resposta da listagem for construída
- **Então** `numeroPagina`, `tamanhoPagina`, `totalElementos` e `totalPaginas` serão preservados

#### AC-107 — Enriquecimento continua em lote

- **Dado** uma página de animais obtida com ou sem filtro de status
- **Quando** a resposta resumida for montada
- **Então** lotes e últimas pesagens serão buscados em lote, sem consultas individuais por animal

## Fora de escopo

- Alterações no frontend, schema, migrations, ordenação ou combinações avançadas de filtros.
- Alterações nas regras de morte, venda, domínio ou dependências.

## Suposições

| ID | Suposição | Status | Resolução |
|---|---|---|---|
| ASM-101 | O binding de `@RequestParam Status` e o tratamento global existente já convertem status inválido em HTTP 400. | confirmada | Confirmado pela implementação de `GlobalExceptionHandler` para `MethodArgumentTypeMismatchException`. |

## Perguntas em aberto

| ID | Pergunta | Status | Resposta |
|---|---|---|---|
| Q-101 | Nenhuma pergunta em aberto para esta task. | respondida | O contrato fornecido determina os três valores e o comportamento legado. |
