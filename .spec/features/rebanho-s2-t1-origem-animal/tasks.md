# Tasks: Modelar origem do animal

> feature: rebanho-s2-t1-origem-animal

## T-501 — Implementar origem do animal [concluida]
- Refs: US-310, AC-316, AC-317, AC-318, AC-319
- Arquivos: .spec/features/rebanho-s2-t1-origem-animal/spec.md, .spec/features/rebanho-s2-t1-origem-animal/tasks.md, core/src/main/java/com/br/core/domain/enums/OrigemAnimal.java, core/src/main/java/com/br/core/domain/model/Animal.java, core/src/test/java/com/br/core/domain/model/AnimalTest.java, usecase/src/main/java/com/br/usecase/manejo/RegistrarNascimentoUseCase.java, usecase/src/main/java/com/br/usecase/manejo/RegistrarCompraAnimalUseCase.java, usecase/src/test/java/com/br/usecase/manejo/RegistrarNascimentoUseCaseTest.java, usecase/src/test/java/com/br/usecase/manejo/RegistrarCompraAnimalUseCaseTest.java, infra/src/main/java/com/br/infra/persistence/entity/AnimalEntity.java, infra/src/main/java/com/br/infra/persistence/mapper/AnimalMapper.java, infra/src/main/resources/db/migration/V9__Add_Origem_Animal.sql, infra/src/test/java/com/br/infra/persistence/repository/SpringDataAnimalRepositoryTest.java, infra/src/test/java/com/br/infra/persistence/migration/OrigemAnimalMigrationTest.java, docs/product/rebanho.md
- Notas: `CADASTRO_INICIAL` permanece uma operação, não um valor de `OrigemAnimal`. Construtores legados e registros sem origem usam `DESCONHECIDO`; compra usa `COMPRA` e nascimento usa `NASCIMENTO`. O caso de uso dedicado de cadastro inicial, importação, frontend, Financeiro e custos de lote permanecem fora desta tarefa.

## T-502 — Registrar animal já existente no plantel [concluida]
- Refs: US-311, AC-320, AC-321, AC-322, AC-323, AC-324
- Arquivos: .spec/features/rebanho-s2-t1-origem-animal/spec.md, .spec/features/rebanho-s2-t1-origem-animal/tasks.md, usecase/src/main/java/com/br/usecase/dto/RegistrarAnimalInicialCommand.java, usecase/src/main/java/com/br/usecase/manejo/RegistrarAnimalInicialUseCase.java, usecase/src/test/java/com/br/usecase/manejo/RegistrarAnimalInicialUseCaseTest.java
- Notas: O cadastro inicial migra o plantel já existente e preserva a origem histórica informada. O caso de uso depende somente de `AnimalRepository`; não aciona compra operacional, `Despesa`, Financeiro ou custo de lote.

## T-503 — Expor cadastro inicial pela API REST [concluida]
- Refs: US-312, AC-325, AC-326, AC-327, AC-328
- Arquivos: .spec/features/rebanho-s2-t1-origem-animal/spec.md, .spec/features/rebanho-s2-t1-origem-animal/tasks.md, application/pom.xml, application/src/main/java/com/br/application/dto/RegistrarAnimalInicialRequest.java, application/src/main/java/com/br/application/rest/AnimalController.java, application/src/test/java/com/br/application/rest/AnimalControllerTest.java
- Notas: `POST /api/v1/animais/cadastrar` recebe somente os dados de cadastro histórico e delega exclusivamente para `RegistrarAnimalInicialUseCase`. O endpoint operacional `POST /api/v1/animais` e `AnimalInputDTO` permanecem inalterados.
