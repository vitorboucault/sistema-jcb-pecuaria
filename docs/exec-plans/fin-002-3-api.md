# Cadastrar e consultar lançamentos pela API

Documento vivo conforme [.agents/PLANS.md](../../.agents/PLANS.md). Executar somente FIN-002.3 da SPEC `.spec/features/financeiro-essencial/spec.md`; FIN-002 geral permanece pendente. Sistema iniciado do zero, sem tratamento de legado.

## Purpose / Big Picture

Permitir POST e GET por UUID em `/api/v1/financeiro/lancamentos`, com JWT, dados financeiros completos e centros de custo existentes. A API retorna o UUID criado e não aceita origem operacional definida pelo cliente. Reenvios não são idempotentes nesta etapa; chaveOperacao permanece FIN-002.5.

## Progress

- [x] (2026-10-16) Revisar SPEC, portas, Builder, segurança, handler e padrões de teste. Confirmada branch feature/financeiro-core; alteração prévia de scripts/check-all.sh preservada.
- [x] (2026-10-16) Implementar command e casos de uso de cadastro/consulta.
- [x] (2026-10-16) Implementar DTOs/controller e tradução de recurso ausente para 404.
- [x] (2026-10-16) Escrever Mockito e MockMvc com JWT real, H2 e PostgreSQL; incluir suíte HTTP PostgreSQL no harness.
- [x] (2026-10-16) Validar backend, frontend, PostgreSQL, harness, diff e auditoria; registrar resultados e pendências.

## Surprises & Discoveries

- O handler global fica em infra, assim como a segurança JWT. Os contratos HTTP completos serão testados em infra, com casos de uso e persistência reais; não desabilitar filtros.
- usecase possuía junit-jupiter-api, mas não junit-jupiter-params. A primeira compilação dos testes parametrizados revelou a dependência ausente; adicionada somente no escopo de teste, seguindo core/application.

## Decision Log

- (2026-10-16) Construir pelo Builder antes de consultar centros; o domínio valida estrutura e invariantes, o caso de uso valida existência nas portas. Isso evita duplicar regras ou consultar centro para um comando estruturalmente inválido.
- (2026-10-16) Command usa CategoriaFinanceira tipada. DTO recebe o nome da categoria e converte para o enum definido pelo tipo; nomes inválidos tornam-se erro 400. Conversão pertence ao payload, compatibilidade continua validada no domínio.
- (2026-10-16) Consulta ausente usa LancamentoFinanceiroNaoEncontradoException no usecase, sem Spring; GlobalExceptionHandler traduz somente essa exceção para o padrão ApiErrorResponse com 404.
- (2026-10-16) Reutilizar a suíte HTTP em H2 e PostgreSQL via herança de teste, padrão já usado na FIN-002.2. A classe PostgreSQL usa Flyway e ddl-auto=validate; o harness a seleciona explicitamente.

## Outcomes & Retrospective

check-all passou com 345 testes backend, 58 frontend e 55 PostgreSQL, sem skips. Os 24 novos casos Mockito e os 40 casos HTTP (executados em H2 e PostgreSQL) comprovam cadastro/consulta com centros, erros, JWT e preservação dos dados. git diff --check passou. Auditoria SDD retorna 1 (189 erros e 94 avisos globais), pois FIN-002 completa e outras áreas ainda possuem requisitos/provas pendentes. Escopo exclui confirmação posterior, correção/exclusão, auditoria, idempotência, recorrências, listagem/filtros/resumo, frontend e integração de vendas. Nenhuma migration ou implementação de persistência será alterada. Compras, vendas e DespesaController permanecem como estão.

## Context and Orientation

Domínio e Builder: `core/src/main/java/com/br/core/domain/model/LancamentoFinanceiro.java` e `model/builders/LancamentoFinanceiroBuilder.java`. Portas ficam em `core/src/main/java/com/br/core/domain/repository/`. A nova implementação de aplicação fica em `usecase/src/main/java/com/br/usecase/financeiro/`, com command em `usecase/dto`. Entrada HTTP fica em `application/src/main/java/com/br/application/rest/FinanceiroController.java` e os DTOs em `application/dto`. Segurança e GlobalExceptionHandler estão em `infra/src/main/java/com/br/infra/`. O repositório de lançamento da FIN-002.2 salva e busca por UUID.

## Plan of Work

Implementar RegistrarLancamentoFinanceiroUseCase com quatro portas e transação Jakarta: construir, validar existência conforme o tipo do centro e salvar. ConsultarLancamentoFinanceiroUseCase busca pela porta ou lança a exceção específica. Controller converte request/response e retorna 201 com Location ou 200. Acrescentar somente o tratamento 404 ao handler global. Testes Mockito verificam efeitos/ausência de persistência; testes HTTP verificam payloads, erros, JWT e integração com o banco. scripts/check-postgres.sh deve selecionar FinanceiroHttpPostgresIT além das suítes existentes.

## Concrete Steps

Na raiz: `./scripts/check-backend.sh`, `./scripts/check-frontend.sh`, `./scripts/check-postgres.sh`, `./scripts/check-all.sh`, `git diff --check` e `node .agents/skills/onp-spec-driven/scripts/onp-spec.mjs audit --ci`. PostgreSQL exige Docker, sem skip; usar acesso fora do sandbox quando necessário. Registrar saídas reais em tasks.md.

## Validation and Acceptance

POST recebe receita/despesa PENDENTE ou EFETIVADO válido e devolve todos os atributos, UUID gerado e Location. GET devolve os dados persistidos; ausente é 404. Centro inexistente, payload incompatível, valor inválido e data efetiva inválida são 400 e não salvam. Ambos os endpoints devolvem 401 sem JWT, com token inválido ou expirado. Campos de ID/origem não pertencem ao request. Testar em PostgreSQL migrado sem gerar schema com Hibernate.

## Idempotence and Recovery

O produto ainda não tem idempotência de cadastro: cada POST válido pode criar novo registro. Isso é documentado como pendência da FIN-002.5. Testes isolam bancos descartáveis/rollback; nenhum banco de uso real é acessado. Não editar migrations. Em falha, corrigir implementação e repetir o check afetado; preservar a alteração prévia de scripts/check-all.sh.

## Interfaces and Dependencies

RegistrarLancamentoFinanceiroCommand contém somente os atributos básicos do domínio, sem UUID/chave/origem. RegistrarLancamentoFinanceiroUseCase.executar(command) e ConsultarLancamentoFinanceiroUseCase.executar(UUID) retornam LancamentoFinanceiro. Request/response são records separados; a resposta expõe categoria como nome do enum. As novas classes usecase não dependem de Spring. Nenhuma dependência de produção nova.

Atualização inicial: plano da etapa autorizada, executada localmente e sequencialmente, sem delegação ou executor headless.

## Artifacts and Notes

Resultados da versão final:

    RegistrarLancamentoFinanceiroUseCaseTest: Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
    ConsultarLancamentoFinanceiroUseCaseTest: Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
    FinanceiroHttpTest: Tests run: 40, Failures: 0, Errors: 0, Skipped: 0
    FinanceiroHttpPostgresIT: Tests run: 40, Failures: 0, Errors: 0, Skipped: 0
    check-postgres.sh: Tests run: 55, Failures: 0, Errors: 0, Skipped: 0
    check-all.sh: Todas as verificações passaram com sucesso! (exit 0)
    onp-spec audit --ci: 189 erro(s), 94 aviso(s) (exit 1)

Atualização final: registros normalizados retornam o mesmo conteúdo via GET após flush/clear, inclusive no PostgreSQL migrado. UUID/origem adicionais no JSON não controlam os campos persistidos. Docker foi acessado fora do sandbox; nenhuma conexão de uso real foi usada. FIN-002.3 está validada; FIN-002 permanece pendente. O contrato incremental e a ausência de idempotência foram documentados na SPEC e no produto.
