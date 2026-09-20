# Tasks: Correções da revisão do Rebanho S1

> feature: rebanho-s1-review-fixes

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

## T-401 — Corrigir elegibilidade da mãe no caso de uso [concluida]
- Refs: US-305, AC-310, AC-311
- Arquivos: usecase/src/main/java/com/br/usecase/manejo/RegistrarNascimentoUseCase.java, usecase/src/test/java/com/br/usecase/manejo/RegistrarNascimentoUseCaseTest.java
- Notas: Corrigir a combinação lógica da validação e cobrir mãe válida, macho, inativa e categoria inelegível. Não criar regra no controller.

## T-402 — Sincronizar matrizes após mutações do rebanho [concluida]
- Refs: US-306, AC-312
- Arquivos: frontend/src/features/rebanho/pages/RebanhoPage.tsx, frontend/tests/rebanho-contract.test.js
- Notas: Reutilizar o fluxo de recarga existente; não criar estado global nem endpoint novo. O teste deve cobrir a atualização da lista após os caminhos de sucesso.

## T-403 — Remover o limite arbitrário da carga de matrizes [concluida]
- Refs: US-307, AC-313
- Arquivos: frontend/src/features/rebanho/api/rebanhoService.ts, frontend/src/features/rebanho/pages/RebanhoPage.tsx, frontend/tests/rebanho-contract.test.js
- Notas: Implementar paginação somente para a consulta de matrizes, preservando a paginação/contrato da tabela principal. Depende da decisão Q-401.

## T-404 — Eliminar recarga duplicada após cadastro [concluida]
- Refs: US-309, AC-314
- Arquivos: frontend/src/features/rebanho/pages/RebanhoPage.tsx, frontend/src/features/rebanho/components/AnimalModalForm.tsx, frontend/tests/rebanho-contract.test.js
- Notas: Manter uma única chamada a `recarregarDados()` no sucesso de `handleCadastrarAnimal`, sem callback duplicado no formulário.

## T-405 — Preservar matrizes em falha de atualização [concluida]
- Refs: US-308, AC-315
- Arquivos: frontend/src/features/rebanho/pages/RebanhoPage.tsx, frontend/tests/rebanho-contract.test.js
- Notas: Em falha, manter a última lista conhecida e exibir aviso; em carga posterior bem-sucedida, limpar o aviso.
