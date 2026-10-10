# Tasks: Documentação e testes do Rebanho existente

> feature: rebanho-documentacao-testes

## T-601 — Corrigir documentação e rastreabilidade [concluida]
- Refs: US-313, AC-329, AC-324
- Arquivos: .gitignore, docs/product/rebanho.md, docs/architecture/database.md, docs/architecture/backend.md, docs/architecture/frontend.md, docs/exec-plans/rebanho-documentacao-testes.md, .spec/features/rebanho-documentacao-testes/spec.md, .spec/features/rebanho-documentacao-testes/tasks.md, .spec/features/rebanho-s2-t1-origem-animal/spec.md, frontend/tests/rebanho-docs.test.js, usecase/src/main/java/com/br/usecase/manejo/RegistrarAnimalInicialUseCase.java, usecase/src/test/java/com/br/usecase/manejo/RegistrarAnimalInicialUseCaseTest.java
- Notas: A única mudança de produção é a mensagem de data futura. IDs T-504 em diante permanecem livres para a evolução funcional.

## T-602 — Provar cadastro e reversão com HTTP integrado [concluida]
- Refs: US-313, AC-330, AC-333, AC-322, AC-324, AC-325, AC-328, AC-201, AC-202
- Arquivos: infra/src/test/java/com/br/infra/rebanho/CadastroInicialHttpTest.java

## T-603 — Provar comportamentos da interface atual [concluida]
- Refs: US-313, AC-331, AC-206, AC-207, AC-301, AC-312, AC-313, AC-315
- Arquivos: frontend/package.json, frontend/package-lock.json, frontend/vitest.config.ts, frontend/tsconfig.test.json, frontend/tests/rebanho-ui.test.tsx, frontend/tests/rebanho-service.test.ts

## T-604 — Executar migrations reais no harness e CI [concluida]
- Refs: US-313, AC-332, AC-318
- Arquivos: infra/pom.xml, infra/src/test/java/com/br/infra/persistence/migration/RebanhoPostgresIT.java, scripts/check-postgres.sh, scripts/check-all.sh, .github/workflows/jcb-ci.yml
- Notas: Docker obrigatório. Falta de Docker falha explicitamente; banco real do usuário não é usado.
