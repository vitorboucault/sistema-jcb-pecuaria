# Tasks: Fechamento pós-review da Sprint 1

> feature: rebanho-s1-close

<!--
  Como ler este arquivo (o formato é verificado por `onp-spec audit`):
  - T-xxx = tarefa (código de rastreio, único no projeto inteiro).
  - Toda tarefa referencia em `Refs:` pelo menos uma história de usuário
    (US-xxx) ou critério de aceite (AC-xxx).
  - Toda tarefa lista os arquivos que cria/altera em `Arquivos:` — capriche:
    é o que decide o que `onp-spec plano` roda em PARALELO (arquivos
    disjuntos) e o que roda em sequência.
  - Campos opcionais por tarefa, usados pelo plano de execução:
    `- Modelo: claude-sonnet-5` e `- Esforço: alto` (baixo|medio|alto|xalto|max).
  - Uma tarefa só pode virar [concluida] quando os critérios de aceite dela
    tiverem prova PASS registrada por `onp-spec verify`.
  Status: pendente | em-andamento | concluida
    (atalho: `onp-spec tarefa <feature> <T-xxx> <status>`)
-->

## T-301 — Implementar e validar o fechamento pós-review [concluida]

- Refs: US-301, AC-301, US-302, AC-302, US-303, AC-303, US-304, AC-304, AC-305, AC-306, AC-307, AC-308, AC-309
- Arquivos: onpspec.config.json, frontend/src/features/rebanho/pages/RebanhoPage.tsx, frontend/src/features/auth/hooks/useAuth.tsx, frontend/src/features/auth/session.ts, frontend/src/shared/api/client.ts, frontend/tests/client.test.js, frontend/tests/auth-session.test.js, frontend/tests/auth-contract.test.js, frontend/tests/rebanho-contract.test.js, frontend/package.json, scripts/check-frontend.sh, docs/product/rebanho.md, infra/src/main/java/com/br/infra/security/SecurityConfig.java, infra/src/test/java/com/br/infra/security/SecurityConfigTest.java, .spec/features/rebanho-s1-close/spec.md, .spec/features/rebanho-s1-close/tasks.md
- Notas: Reutilizar o node:test existente, não adicionar dependências, não implementar refresh token e manter o login público.
