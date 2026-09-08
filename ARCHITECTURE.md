# Arquitetura do Sistema JCB Pecuária

O **Sistema JCB Pecuária** é uma plataforma para gestão de pecuária de corte de ciclo completo (cria, recria e engorda), focada em precisão de manejo, rastreabilidade individual de animais e controle produtivo.

## Visão Geral e Princípios

O projeto adota os princípios de **Clean Architecture** (Arquitetura Limpa / Portas e Adaptadores):

```
       +---------------------------------------------+
       |             application / infra             |
       |  (Controllers REST, JPA, Spring Boot, Sec) |
       +---------------------------------------------+
                              |
                              v
       +---------------------------------------------+
       |                   usecase                   |
       |  (Orquestração, Regras de Aplicação, DTOs)  |
       +---------------------------------------------+
                              |
                              v
       +---------------------------------------------+
       |                    core                     |
       | (Entidades de Domínio, Enums, Ports/Repos)  |
       +---------------------------------------------+
```

### Regras Fundamentais de Dependência

1. **`core` é agnóstico ao framework**:
   - Não depende do ecossistema Spring.
   - Contém entidades ricas, regras de domínio invariantes e interfaces de portas (ex: `AnimalRepository`).
2. **`usecase` orquestra operações**:
   - Depende apenas de `core` e anotações padrão Jakarta (`@Named`, `@Transactional`).
   - Define comandos (`Command`), DTOs de resultado e orquestra a lógica de negócio chamando os repositórios/portas do `core`.
3. **`application` expõe a camada HTTP**:
   - Depende de `usecase` e `core`.
   - Contém `@RestController`, conversores de requisição/resposta e anotações `@Valid`.
   - Não implementa regras de negócio diretamente.
4. **`infra` implementa as portas e integra o ecossistema Spring**:
   - Depende de `core`, `usecase` e `application`.
   - Contém o ponto de entrada da aplicação (`SistemajcbApplication`), repositórios Spring Data JPA (`*RepositoryImpl`), segurança Spring Security, migrations Flyway e configurações de banco.

---

## Estrutura de Módulos e Tecnologias

| Camada / Módulo | Diretório | Tecnologias Principais | Responsabilidade |
| :--- | :--- | :--- | :--- |
| **Core** | `core/` | Java 24, Jakarta Inject | Entidades de domínio puras, Value Objects, Enums, Interfaces de Portas. |
| **Usecase** | `usecase/` | Java 24, Jakarta Inject, Jakarta Transaction | Casos de uso de negócio, Commands, DTOs de aplicação. |
| **Application** | `application/` | Java 24, Spring Web | Endpoints REST, mapeamento de requisições, validação de payload. |
| **Infra** | `infra/` | Spring Boot 4.1.1, Spring Data JPA, Flyway, PostgreSQL | Implementação de repositórios JPA, migrações de banco, segurança JWT, inicialização do Spring. |
| **Frontend** | `frontend/` | React 19, TypeScript, Vite, Tailwind CSS v4, Axios | Interface de usuário orientada a recursos (`features`). |

---

## Documentação Detalhada

Para detalhes técnicos e guias específicos de cada parte da aplicação, consulte:

- **Backend**: [docs/architecture/backend.md](docs/architecture/backend.md)
- **Frontend**: [docs/architecture/frontend.md](docs/architecture/frontend.md)
- **Banco de Dados & Migrations**: [docs/architecture/database.md](docs/architecture/database.md)
- **Domínio Rebanho**: [docs/product/rebanho.md](docs/product/rebanho.md)
- **Domínio Vendas**: [docs/product/vendas.md](docs/product/vendas.md)
- **Harness e Instruções do Agente**: [AGENTS.md](AGENTS.md)
