# Arquitetura Backend - Sistema JCB Pecuária

O backend do Sistema JCB é desenvolvido em **Java 24** com **Spring Boot 4.1.1**, estruturado como um projeto multi-módulos Maven. O design segue estritamente os princípios de Clean Architecture.

---

## 1. Organização dos Módulos Maven

```
sistemajcb (pom.xml raiz)
├── core
├── usecase
├── application
├── infra
└── coverage
```

### 1.1. `core`
- **Finalidade**: Contém o coração do negócio pecuário.
- **Regra**: **Zero dependências do Spring Framework**.
- **Conteúdo**:
  - `com.br.core.domain.model`: Entidades de domínio ricas (ex: `Animal`, `Lote`, `Pesagem`, `VendaAnimal`). As entidades contêm métodos com validação de invariantes de negócio (ex: `animal.reverterVenda()`, `animal.reverterMorte()`).
  - `com.br.core.domain.enums`: Enums de domínio (ex: `Status`, `Sexo`, `Categoria`, `OrigemPesagem`).
  - `com.br.core.domain.repository`: Portas (interfaces puras) que definem contratos de persistência (ex: `AnimalRepository`, `VendaAnimalRepository`).

> Exceção arquitetural legada: a regra preferencial é manter as portas de persistência no lado de domínio (`core`). Atualmente existe uma exceção em `usecase/port`, como `FornecimentoRacaoRepositoryPort`. Não criar novas portas nesse local sem necessidade e não refatorar essa exceção nesta tarefa.

### 1.2. `usecase`
- **Finalidade**: Orquestra fluxos e casos de uso da aplicação.
- **Regra**: Depende exclusivamente do módulo `core` e de anotações Jakarta (`jakarta.inject.Named`, `jakarta.transaction.Transactional`). Não referencia classes do Spring Boot nem da infraestrutura.
- **Conteúdo**:
  - `com.br.usecase.manejo`: Classes de caso de uso nomeadas com `@Named`, expondo método `executar(...)`.
  - `com.br.usecase.dto`: Records e DTOs de comandos de entrada e respostas de aplicação (ex: `RegistrarVendaCommand`, `RegistrarPesagemCommand`).

### 1.3. `application`
- **Finalidade**: Camada de interface e adaptadores de entrada (HTTP / REST).
- **Regra**: Responsável por receber requisições HTTP, validar payloads de entrada com Bean Validation (`@Valid`), delegar para os casos de uso correspondentes e formatar as respostas HTTP. **Não deve conter regras de negócio.**
- **Conteúdo**:
  - `com.br.application.rest`: Controladores REST (`@RestController`) mapeados sob o prefixo `/api/v1/` (ex: `AnimalController`, `VendaController`, `LoteController`).
  - `com.br.application.dto`: DTOs de requisição e resposta REST.

### 1.4. `infra`
- **Finalidade**: Implementações de adaptadores de saída, persistência, frameworks e infraestrutura geral.
- **Conteúdo**:
  - `com.br.infra.SistemajcbApplication`: Classe principal de inicialização do Spring Boot.
  - `com.br.infra.persistence.entity`: Entidades JPA mapeadas para tabelas do PostgreSQL.
  - `com.br.infra.persistence.repository`: Repositórios Spring Data JPA (`JpaRepository`).
  - `com.br.infra.persistence.adapter`: Implementações das portas do `core` (ex: `AnimalRepositoryImpl` que implementa `AnimalRepository` e delega para o `SpringDataAnimalRepository`).
  - `com.br.infra.persistence.mapper`: Conversores bidirecionais entre entidades JPA e modelos de domínio.
  - `com.br.infra.security`: Configurações de autenticação, filtro de tokens JWT (`SecurityFilterChain`).
  - `src/main/resources/db/migration`: Scripts SQL versionados do Flyway.

### 1.5. `coverage`
- Módulo agregador JaCoCo para geração de relatórios consolidados de cobertura de testes.

---

## 2. Injeção de Dependências e Componentes

Para garantir que o módulo `usecase` permaneça desacoplado do Spring:
- Casos de uso são anotados com `@Named` (JSR-330 padrão Jakarta).
- O Spring Boot reconhece `@Named` como `@Component` automaticamente durante o escaneamento de pacotes (`@ComponentScan`).
- `@Transactional` utilizada em casos de uso provém de `jakarta.transaction.Transactional`.

---

## 3. Padrões de Testes

- **Testes Unitários (Core e Usecase)**:
  - JUnit 5 (`junit-jupiter`), AssertJ e Mockito.
  - Não sobem contexto do Spring; utilizam mocks puros para portas de repositório.
- **Testes de Integração e Controllers (Application e Infra)**:
  - Spring Boot Test / MockMvc.
  - Banco em memória: H2 configurado em modo de compatibilidade PostgreSQL (`jdbc:h2:mem:sistemajcb_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE`).
- **Comando de validação rápida**:
  ```bash
  ./scripts/check-backend.sh
  ```
