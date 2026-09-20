# Spec: Fechamento pós-review da Sprint 1

> feature: rebanho-s1-close
> status: auditada

<!--
  Como ler este arquivo (o formato é verificado por `onp-spec audit`):
  - US-xxx = história de usuário · AC-xxx = critério de aceite
    ASM-xxx = suposição · Q-xxx = pergunta em aberto
    São códigos de rastreio: ligam a especificação às tarefas e aos testes.
  - Toda história de usuário precisa de pelo menos um critério de aceite.
  - Todo critério de aceite precisa de Dado/Quando/Então completos.
  - Os códigos são únicos no projeto inteiro (nunca reutilize um número).
  - Suposições e Perguntas em aberto são OBRIGATÓRIAS: se não há nenhuma,
    escreva "Nenhuma." — mas desconfie: quase toda feature esconde uma.
-->

## Contexto

Este fechamento corrige pendências de revisão da Sprint 1 sem adicionar
funcionalidades de produto: matrizes independentes do filtro visual,
documentação coerente, testes frontend no harness e expiração de sessão JWT
segura no backend e frontend.

## Histórias

<!-- História de usuário: quem precisa, o que precisa e por quê. -->

### US-301 — Cadastro de nascimento independente da tabela

Como produtor, quero cadastrar nascimentos usando matrizes elegíveis mesmo
quando a tabela estiver filtrada por mortos ou vendidos, para não perder as
opções de mãe.

#### AC-301 — Matrizes não dependem do filtro visual

- **Dado** a tabela filtrada por `MORTO` ou `VENDIDO`
- **Quando** o cadastro de nascimento for aberto
- **Então** a seleção de matriz contém animais `ATIVO`, `FEMEA` e de categoria `VACA` ou `NOVILHA`, carregados separadamente da lista filtrada

### US-302 — Documentação coerente do ciclo do animal

Como mantenedor, quero que a documentação descreva a preservação do lote na
morte e reversão, para evitar regressões de regra de negócio.

#### AC-302 — Documentação reflete preservação do lote

- **Dado** a documentação de Rebanho
- **Quando** as regras de morte, reversão e composição do lote forem consultadas
- **Então** ela registra `loteId` preservado em morte/reversão e mortos fora da composição operacional, sem afirmar que o lote é zerado

### US-303 — Harness frontend completo

Como mantenedor, quero que os testes frontend existentes sejam executados pelo
harness, para que regressões de contrato não dependam de execução manual.

#### AC-303 — Testes frontend executados no harness

- **Dado** o projeto frontend
- **Quando** `scripts/check-frontend.sh` for executado
- **Então** ele executa `npm run test`, `npm run lint` e `npm run build`, usando o `node:test` existente e sem nova dependência

### US-304 — Sessão autenticada segura

Como usuário autenticado, quero que tokens inválidos ou expirados encerrem a
sessão de forma previsível, para não permanecer em uma tela protegida sem
autenticação válida.

#### AC-304 — Backend responde 401 para autenticação inválida

- **Dado** um endpoint protegido acessado sem autenticação, com token inválido ou com token expirado
- **Quando** a requisição chegar ao backend
- **Então** a resposta será HTTP 401 por meio do `AuthenticationEntryPoint`

#### AC-305 — Sessão expirada ao iniciar é removida

- **Dado** um `@jcb:user` com JWT expirado ou inválido no `localStorage`
- **Quando** o `AuthProvider` for inicializado
- **Então** a sessão será removida, `user` será nulo e a rota protegida poderá redirecionar para `/login`

#### AC-306 — Sessão expira automaticamente durante o uso

- **Dado** um JWT válido com claim `exp`
- **Quando** o horário de expiração for atingido com a aplicação aberta
- **Então** o frontend agenda o logout, remove `@jcb:user` e deixa `user` nulo

#### AC-307 — Qualquer 401 encerra sessão fora do login

- **Dado** uma resposta HTTP 401 fora da rota `/login`
- **Quando** o interceptor global do Axios processar a resposta
- **Então** ele remove `@jcb:user` e redireciona para `/login`, mesmo sem depender de `hasBearerToken`

#### AC-308 — Login permanece público e funcional

- **Dado** uma requisição `POST /api/v1/auth/login`
- **Quando** ela for processada pelo Spring Security
- **Então** a rota continua pública e credenciais válidas continuam recebendo token

#### AC-309 — Fechamento não adiciona escopo proibido

- **Dado** o conjunto de alterações revisado
- **Quando** código, dependências e chamadas de autenticação forem inspecionados
- **Então** não haverá refresh token, renovação silenciosa, venda, reversão de venda ou dependência nova de JWT

## Fora de escopo

- Refresh token, renovação silenciosa, vendas, reversão de venda, Financeiro,
  telas de Lotes/Pesagens, novas dependências e refactors não relacionados.

## Suposições

<!-- O que estamos ASSUMINDO sem confirmação. Status: aberta | confirmada | invalidada -->

| ID | Suposição | Status | Resolução |
|---|---|---|---|
| ASM-301 | O endpoint paginado por status é suficiente para carregar as matrizes elegíveis enquanto não existe uma porta específica de matrizes. | confirmada | A regra exige independência do filtro visual; a consulta explícita `status=ATIVO` e filtro local por sexo/categoria atende sem alterar o domínio. |
| ASM-302 | JWTs emitidos pelo backend sempre possuem claim `exp`. | confirmada | `TokenService.gerarToken` já define `withExpiresAt`; tokens sem `exp` serão tratados como sessão inválida no frontend. |

## Perguntas em aberto

<!-- O que ainda não sabemos. Status: aberta | respondida -->

| ID | Pergunta | Status | Resposta |
|---|---|---|---|
| Q-301 | Nenhuma pergunta em aberto para este fechamento. | respondida | A SPEC define explicitamente o escopo e os comportamentos esperados. |
