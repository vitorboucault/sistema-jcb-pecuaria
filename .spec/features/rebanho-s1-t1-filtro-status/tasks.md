# Tasks: Filtro de status na listagem de animais

> feature: rebanho-s1-t1-filtro-status

## T-101 — Testar e implementar filtro de status [concluida]

- Refs: US-101, AC-101, AC-102, AC-103, AC-104, AC-105, AC-106, AC-107
- Arquivos: .spec/features/rebanho-s1-t1-filtro-status/spec.md, .spec/features/rebanho-s1-t1-filtro-status/tasks.md, onpspec.config.json, core/src/main/java/com/br/core/domain/repository/AnimalRepository.java, infra/src/main/java/com/br/infra/persistence/adapter/AnimalRepositoryImpl.java, application/src/main/java/com/br/application/rest/AnimalController.java, application/src/test/java/com/br/application/rest/AnimalControllerTest.java, infra/src/test/java/com/br/infra/persistence/adapter/AnimalRepositoryImplTest.java, infra/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker
- Notas: Reutilizar `findByStatus(String, Pageable)`, preservar a consulta padrão e manter o enriquecimento em lote.
