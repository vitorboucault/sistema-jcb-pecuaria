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
- **Frontend (npm ci + lint + build Vite)**:
  ```bash
  ./scripts/check-frontend.sh
  ```
- **Validação Completa (ambos)**:
  ```bash
  ./scripts/check-all.sh
  ```

---

## Testing

- **Mudança de regra de negócio exige teste**: toda nova funcionalidade ou alteração em regras deve ser coberta.
- **Bug fix exige teste de regressão**: reproduzir o defeito em teste antes ou junto à correção.
- **Testes de domínio no `core`**: cobrem invariantes, validações e transições de estado puras.
- **Mockito no `usecase`**: valida regras de aplicação, fluxos condicionais e efeitos colaterais em portas.
- **MockMvc no `application`**: valida contratos HTTP, roteamento, serialização e códigos de status.
- **Testes de integração no `infra`**: validam mapeamentos JPA, queries customizadas e persistência real/H2.
- **Nunca afirmar que teste passou sem executá-lo**: rode a suíte antes de reportar conclusão.

---

## Code Style

- **Consistência local**: siga o padrão de formatação e nomenclatura dos arquivos próximos.
- **Constructor Injection**: use injeção via construtor (evite `@Autowired` em campos).
- **Controllers enxutos**: mantenha controllers sem regras de negócio; apenas orquestram chamadas a casos de uso.
- **Evite refactors fora de escopo**: limite as mudanças estritamente ao objetivo da tarefa.
- **TypeScript estrito**: utilize tipos explícitos para entidades, payloads e retornos.
- **Evite `any`**: não use `any` para contornar checagens de compilação.

---

## Security

- **Segredos protegidos**: nunca commitar `.env`, secrets, tokens ou credenciais no repositório.
- **Exemplos fictícios**: `.env.example` deve conter apenas valores placeholder/fictícios para desenvolvimento local.
- **Sem atalhos de teste**: não desabilitar filtros de segurança ou validações para facilitar testes.
- **Logs seguros**: nunca logar senhas, tokens JWT ou dados sensíveis em logs de aplicação.
- **Ocultação de detalhes internos**: não expor stack traces ou erros internos de infraestrutura ao cliente HTTP.

---

## Git & Fluxo de Trabalho

- **Nunca trabalhar diretamente na `main`**.
- **Uma feature por branch**.
- **Não fazer merge automaticamente**.
- **Commits pequenos e coerentes**: preferir prefixos convencionais (`feat:`, `fix:`, `test:`, `docs:`, `ci:`, `chore:`).
- **Descrição de PR**: PRs devem explicar o que mudou, por quê e como foi validado.
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
