# Tasks: Jwt expirado frontend

> feature: jwt-expirado-frontend

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

## T-001 — Especificar o tratamento de sessão expirada [concluida]

- Refs: US-001, AC-001, AC-002, AC-003, AC-004, AC-005
- Arquivos: .spec/features/jwt-expirado-frontend/spec.md, docs/exec-plans/front-end/auth/jwt-expirado-frontend.md
- Notas: Contrato definido antes da alteração de código.

## T-002 — Testar e implementar interceptor de resposta [concluida]

- Refs: AC-001, AC-002, AC-003, AC-004, AC-005
- Arquivos: frontend/src/shared/api/client.ts, frontend/tests/client.test.js
- Notas: O interceptor deve tratar qualquer `401` fora da rota de login e preservar os demais erros.

## T-003 — Executar validação e auditoria [concluida]

- Refs: AC-001, AC-002, AC-003, AC-004, AC-005
- Arquivos: onpspec.config.json, frontend/tests/client.test.js
- Notas: Depende de T-002.
