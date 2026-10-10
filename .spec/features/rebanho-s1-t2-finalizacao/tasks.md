# Tasks: Finalização do Rebanho

> feature: rebanho-s1-t2-finalizacao

## T-201 — Testar e implementar finalização do ciclo do Rebanho [concluida]
- Refs: US-201, AC-201, AC-202, AC-203, AC-204, US-202, AC-205, AC-206, AC-207, AC-208, AC-209, AC-210, AC-211
- Arquivos: .spec/features/rebanho-s1-t2-finalizacao/spec.md, .spec/features/rebanho-s1-t2-finalizacao/design.md, .spec/features/rebanho-s1-t2-finalizacao/tasks.md, onpspec.config.json, core/src/main/java/com/br/core/domain/model/Animal.java, core/src/test/java/com/br/core/domain/model/AnimalTest.java, infra/src/main/java/com/br/infra/persistence/repository/SpringDataAnimalRepository.java, infra/src/main/java/com/br/infra/persistence/adapter/AnimalRepositoryImpl.java, infra/src/test/java/com/br/infra/persistence/repository/SpringDataAnimalRepositoryTest.java, infra/src/test/java/com/br/infra/persistence/adapter/AnimalRepositoryImplTest.java, usecase/src/test/java/com/br/usecase/manejo/ManejoLoteEMorteTest.java, usecase/src/test/java/com/br/usecase/manejo/ReverterMorteAnimalUseCaseTest.java, frontend/src/features/rebanho/api/rebanhoService.ts, frontend/src/features/rebanho/pages/RebanhoPage.tsx, frontend/src/features/rebanho/components/AnimalModalForm.tsx, frontend/tests/rebanho-contract.test.js
- Notas: Preservar as alterações não commitadas da Task 1; não criar migration, dependência ou reversão de venda.
