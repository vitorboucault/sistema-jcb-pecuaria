# Documentar e comprovar o Rebanho existente

Este ExecPlan segue `.agents/PLANS.md`. É vivo: manter Progress, Surprises &
Discoveries, Decision Log e Outcomes & Retrospective atualizados.

## Purpose / Big Picture

Entregar somente os pontos 1 e 5: documentação coerente e testes reais do
comportamento já disponível, sem implementar novos fluxos. Verificar cadastro
inicial pela API, interface de status/matrizes e migração de dados legados.

## Progress

- [x] 2026-09-20: Inspecionar documentação, contratos, testes e IDs livres.
- [x] 2026-09-20: Delimitar escopo e corrigir documentação/mensagem de data futura.
- [x] 2026-09-21: Executar e validar novos testes HTTP e de componentes.
- [x] 2026-09-21: Executar migrations PostgreSQL vazio e V8 com dados legados.
- [x] 2026-09-21: Executar check-all e verify; registrar audit global pendente.

## Surprises & Discoveries

O Docker estava parado; o usuário autorizou iniciar Docker Desktop. O frontend
usava somente node:test e verificações textuais; Vitest/Testing Library/jsdom
são adicionados apenas como dependências de desenvolvimento. As SPECs estavam
parcialmente ignoradas: liberar fontes, mas não logs/provas/execuções geradas.
O primeiro teste HTTP precisou de flush explícito antes das consultas JDBC,
pois o rollback transacional de teste posterga a escrita JPA.

## Decision Log

- Recorte confirmado: pontos 1 e 5, não pontos 2–4.
- Sequencial nesta sessão, sem subprocessos de agentes ou troca de modelo.
- Não criar testes pulados para funcionalidades futuras; registrar dependências
  na SPEC como fora de escopo e cobertura pendente.
- Mensagem de nascimento futuro neutra; nenhuma nova validação de lote/peso.
- PostgreSQL isolado via Testcontainers; falha de Docker não vira aprovação.

## Context and Orientation

`core` contém domínio e portas; `usecase` orquestra regras; `application` expõe
HTTP; `infra` fornece segurança, handler global, JPA e migrations.
`frontend/src/features/rebanho` contém tela, formulário e serviço atuais.
`POST /api/v1/animais/cadastrar` já retorna 201 com UUID. O formulário ainda
usa o endpoint operacional; essa integração nova não será implementada aqui.

## Plan of Work

T-601 corrige documentação, ASM-501 e mensagem, libera fontes SDD no Git e
adiciona teste de documentação. T-602 usa MockMvc com contexto Spring completo,
JWT e H2, sem mocks de usecases/repositórios, para consultar o estado persistido.
T-603 renderiza RebanhoPage com serviços controlados e exercita listarMatrizes
real com transporte simulado. T-604 usa PostgreSQL descartável, Flyway e JDBC.

## Concrete Steps

Na raiz, executar `./scripts/check-all.sh`: Maven, frontend (node:test, tipos,
Vitest, lint, build) e `bash scripts/check-postgres.sh`.
Diagnóstico isolado: `./scripts/check-backend.sh`, `./scripts/check-frontend.sh`
ou `bash scripts/check-postgres.sh`. O CI executa PostgreSQL no job backend.

Executar `node .agents/skills/onp-spec-driven/scripts/onp-spec.mjs verify rebanho-documentacao-testes`,
depois `audit` e `audit --ci` pela mesma ferramenta. Não enfraquecer o gate
para eliminar problemas antigos de rastreabilidade.

## Validation and Acceptance

Três origens persistidas sem transação financeira/pesagem, 401 sem autenticação,
400 com dados inválidos e reversão conservando lote encerrado. Na UI, ações por
status, filtro existente, matrizes independentes da tabela e recuperação de erro.
No PostgreSQL, V1–V9 em banco vazio e V8–V9 preservando legado; origem inválida
ou nula deve violar constraint. Preservar AC-310/AC-319 existentes.

## Idempotence and Recovery

Testes HTTP usam rollback; Testcontainers remove somente bancos descartáveis.
Não executar migrations no banco do usuário. Scripts podem ser repetidos.
Dependências no lockfile, instalação por npm ci. Sem merge.

## Interfaces and Dependencies

Nenhum contrato REST ou enum muda. Acrescentar testes, documentação,
Testcontainers PostgreSQL test-scope, Vitest/Testing Library/jsdom dev-only e
harness. `check-postgres.sh` executa explicitamente RebanhoPostgresIT;
check-backend.sh continua executando os testes que não dependem de Docker.

## Outcomes & Retrospective

Resultado: os cinco critérios desta feature tiveram prova PASS no `verify`.
O `audit --ci` global ainda não fecha com exit 0 porque o repositório possui
arquivos órfãos e verificações antigas desatualizadas em outras features; os
novos critérios aparecem apenas com o aviso de prova fraca do reporter global.
Permanecem fora: novos vínculos com lote encerrado, três fluxos na UI, pesagem
em duas etapas e paginação visual/busca global.
