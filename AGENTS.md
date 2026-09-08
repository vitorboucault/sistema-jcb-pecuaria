# Sistema JCB Pecuária

Sistema de gestão de pecuária de ciclo completo (cria, recria e engorda).

## Stack Tecnológica

- **Backend**: Java 24, Spring Boot 4.1.1, Maven multi-módulos, PostgreSQL, Flyway.
- **Frontend**: React 19, TypeScript 6, Vite 8, Tailwind CSS v4, Axios.

---

## Arquitetura & Regras Invioláveis

Fluxo de dependência permitido:
```
core  <--  usecase  <--  application / infra
```

Regras arquiteturais:
1. **`core` não depende de Spring** nem de infraestrutura.
2. **Regras de negócio pertencem exclusivamente ao domínio (`core`) e casos de uso (`usecase`)**.
3. **Controllers (`application`) não implementam regra de negócio**; apenas recebem requisições, validam payloads e delegam para usecases.
4. **`infra` implementa as portas** definidas no `core`.
5. **Migrations Flyway são estritamente imutáveis** após versionadas.

Documentação aprofundada:
- [ARCHITECTURE.md](ARCHITECTURE.md) - Visão geral e camadas
- [docs/architecture/backend.md](docs/architecture/backend.md) - Guia e padrões do backend
- [docs/architecture/frontend.md](docs/architecture/frontend.md) - Guia e padrões do frontend
- [docs/architecture/database.md](docs/architecture/database.md) - Banco de dados e Flyway

---

## Antes de Alterar Código

1. Leia a documentação do domínio afetado em `docs/product/`.
2. Inspecione a implementação e os testes existentes.
3. Não invente novas abstrações se o padrão arquitetural atual resolve o problema.
4. Preserve compatibilidade de APIs e contratos, salvo requisito explícito.

---

## Harness de Validação Obrigatória

Antes de submeter qualquer código ou concluir uma tarefa, execute os scripts de validação:

- **Backend (testes Maven)**:
  ```bash
  ./scripts/check-backend.sh
  ```
- **Frontend (lint + build Vite)**:
  ```bash
  ./scripts/check-frontend.sh
  ```
- **Validação Completa (ambos)**:
  ```bash
  ./scripts/check-all.sh
  ```

---

## Git & Fluxo de Trabalho

- **Nunca trabalhar diretamente na `main`**.
- **Uma feature por branch**.
- **Não fazer merge automaticamente**.
- Sempre mostrar arquivos alterados e testes executados no encerramento da tarefa.

---

## Domínios de Negócio

- **Rebanho**: [docs/product/rebanho.md](docs/product/rebanho.md)
- **Vendas**: [docs/product/vendas.md](docs/product/vendas.md)

---

## Planos de Execução (ExecPlans)

Ao planejar recursos complexos, migrações de dados ou refatorações significativas:
1. Siga o padrão descrito em [.agents/PLANS.md](.agents/PLANS.md).
2. Utilize o template [.agents/templates/exec-plan-template.md](.agents/templates/exec-plan-template.md).
3. Salve o plano em [docs/exec-plans/](docs/exec-plans/).
