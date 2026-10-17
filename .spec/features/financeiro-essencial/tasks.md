# Tasks: Financeiro Essencial

> feature: financeiro-essencial

Premissa atual confirmada pelo usuário: o financeiro começa do zero, sem registros antigos.

FIN-001 é documental. FIN-002 será executada incrementalmente; FIN-002.1 (domínio), FIN-002.2 (persistência) e FIN-002.3 (cadastro/consulta por UUID e validação dos centros) estão autorizadas nas entregas incrementais. FIN-002.4–FIN-002.5 e FIN-003–FIN-006 permanecem planejamento. As tarefas permanecem pendentes no motor até as provas integrais aplicáveis; o registro documental de FIN-001 não simula PASS funcional. IDs SDD globais são distintos dos códigos das entregas.

## T-700 — FIN-001 — Especificação e modelagem [pendente]
- Refs: US-700, US-701, US-702, US-703, US-704, US-705
- Arquivos: .spec/features/financeiro-essencial/spec.md, .spec/features/financeiro-essencial/tasks.md, docs/product/financeiro.md
- Notas: Entrega documental produzida nesta sessão. Revisar evidências, DER de duas tabelas, inicialização do sistema e contratos. Não representa implementação dos critérios funcionais.

## T-701 — FIN-002 — Receitas e despesas: backend, persistência e API [pendente]
- Refs: AC-700, AC-701, AC-702, AC-703, AC-704, AC-711, AC-712, AC-713, AC-714, AC-715
- Arquivos: core/src/main/java/com/br/core/domain/model/LancamentoFinanceiro.java, core/src/main/java/com/br/core/domain/model/builders/LancamentoFinanceiroBuilder.java, core/src/main/java/com/br/core/domain/enums/interfaces/CategoriaFinanceira.java, core/src/main/java/com/br/core/domain/enums/TipoLancamentoFinanceiro.java, core/src/main/java/com/br/core/domain/enums/SituacaoFinanceira.java, core/src/main/java/com/br/core/domain/enums/CategoriaReceita.java, core/src/main/java/com/br/core/domain/enums/CategoriaDespesa.java, core/src/test/java/com/br/core/domain/model/LancamentoFinanceiroTest.java, core/src/main/java/com/br/core/domain/repository/DespesaRepository.java, usecase/src/main/java/com/br/usecase/financeiro/RegistrarDespesaUseCase.java, infra/src/main/java/com/br/infra/persistence/entity/DespesaEntity.java, infra/src/main/java/com/br/infra/persistence/repository/SpringDataDespesaRepository.java, application/src/main/java/com/br/application/rest/DespesaController.java, core/src/main/java/com/br/core/domain/repository/LancamentoFinanceiroRepository.java, infra/src/main/java/com/br/infra/persistence/mapper/LancamentoFinanceiroMapper.java, infra/src/main/java/com/br/infra/persistence/mapper/DespesaMapper.java, infra/src/main/java/com/br/infra/persistence/adapter/LancamentoFinanceiroRepositoryImpl.java, infra/src/main/java/com/br/infra/persistence/adapter/DespesaRepositoryImpl.java, infra/src/main/resources/db/migration/V12__Evolve_Transacao_Financeira.sql, infra/src/test/java/com/br/infra/persistence/financeiro/FinanceiroPersistenceTest.java, infra/src/test/java/com/br/infra/persistence/financeiro/FinanceiroPostgresIT.java, infra/src/test/java/com/br/infra/persistence/migration/RebanhoPostgresIT.java, scripts/check-postgres.sh, docs/exec-plans/fin-002-2-persistencia.md, usecase/pom.xml, usecase/src/main/java/com/br/usecase/dto/RegistrarLancamentoFinanceiroCommand.java, usecase/src/main/java/com/br/usecase/exception/LancamentoFinanceiroNaoEncontradoException.java, usecase/src/main/java/com/br/usecase/financeiro/RegistrarLancamentoFinanceiroUseCase.java, usecase/src/main/java/com/br/usecase/financeiro/ConsultarLancamentoFinanceiroUseCase.java, usecase/src/test/java/com/br/usecase/financeiro/RegistrarLancamentoFinanceiroUseCaseTest.java, usecase/src/test/java/com/br/usecase/financeiro/ConsultarLancamentoFinanceiroUseCaseTest.java, application/src/main/java/com/br/application/dto/RegistrarLancamentoFinanceiroRequest.java, application/src/main/java/com/br/application/dto/LancamentoFinanceiroResponse.java, application/src/main/java/com/br/application/rest/FinanceiroController.java, infra/src/main/java/com/br/infra/GlobalExceptionHandler.java, infra/src/test/java/com/br/infra/financeiro/FinanceiroHttpTest.java, infra/src/test/java/com/br/infra/financeiro/FinanceiroHttpPostgresIT.java, docs/exec-plans/fin-002-3-api.md
- Notas: Depende de FIN-001. Inicializar em banco sem transações financeiras antigas; preservar portas, compras e indicadores. Acrescentar domínio de lançamento, enums, campos, auditoria, idempotência e confirmação/correção/exclusão. Testes core de invariantes, Mockito de fluxos, MockMvc de contratos, integração de consultas/mappers e inicialização PostgreSQL com campos obrigatórios. Corrigir filtros antes de inserir receitas. Aplicar histórico JSONB confirmado em ASM-701 e observar a resposta de Q-700.

### Etapas incrementais de T-701

| Etapa | Escopo | Rastreabilidade | Estado |
|---|---|---|---|
| FIN-002.1 | Entidade imutável `LancamentoFinanceiro`, enums tipados, invariantes de construção e testes unitários sem infraestrutura. Preservar `Despesa`. | AC-700 e AC-701, somente domínio | Implementada; resultados de validação abaixo |
| FIN-002.2 | Novas migrations e persistência em banco sem transações financeiras antigas; exigir descrição/situação, preservar metadados e corrigir filtros da visão Despesa antes de receitas. | AC-700, AC-701, AC-712, AC-713, AC-714 | Implementada e validada nesta etapa; critérios integrais continuam parciais |
| FIN-002.3 | Casos de uso e API de cadastro/consulta; validar existência dos centros fora da entidade e preservar contratos existentes. | AC-700, AC-701, AC-713, AC-714, AC-715 | Implementada e validada; idempotência permanece FIN-002.5 |
| FIN-002.4 | Confirmar pagamentos e recebimentos integrais com data efetiva e proteger repetições. | AC-703, AC-704 | Pendente |
| FIN-002.5 | Correções, exclusões auditadas, idempotência e regressões de compras, vendas, indicadores e exclusão de animal. | AC-702, AC-704, AC-711, AC-712, AC-713, AC-714, AC-715 | Pendente |

FIN-002.1 não implementa persistência/leitura de AC-700 nem existência real dos centros, correções ou ausência de persistência de AC-701. As tags `@spec:AC-700` e `@spec:AC-701` nos testes comprovam apenas invariantes do domínio. Não marcar esses critérios ou T-701 como integralmente provados/concluídos; a spec original permanece preservada. UUID informado passa pelas mesmas validações; situação nula é rejeitada também na reconstrução básica. FIN-002.2 exige descrição, situação e tipo do centro em todos os registros financeiros. Nenhum método de liquidação ou exclusão é criado nesta etapa.

## T-702 — FIN-003 — Despesas recorrentes mensais [pendente]
- Refs: AC-705, AC-706, AC-707
- Arquivos: .spec/features/financeiro-essencial/spec.md
- Notas: Depende de FIN-002. Criar modelo/porta/adapter de recorrência e migration nova; geração explícita transacional por mês/ano. Testar dois geradores concorrentes, unicidade após exclusão, mês com 28/29/30/31 dias, vigência, reenvio do modelo, edição independente e encerramento. Arquivos de implementação específicos serão mapeados antes da execução.

## T-703 — FIN-004 — Consultas, filtros e resumo financeiro [pendente]
- Refs: AC-708, AC-709, AC-710, AC-712, AC-713
- Arquivos: .spec/features/financeiro-essencial/spec.md
- Notas: Depende de FIN-002 e FIN-003. Implementar consultas por referência/efetivação, pendências globais, vencidos e resumo. Testar mudança de mês/ano, intervalo inclusivo, valores zero, exclusão, paginação e isolamento de receitas nos indicadores. Aplicar calendário America/Bahia confirmado em ASM-700. Mapear arquivos antes de executar.

## T-704 — FIN-005 — Frontend do Financeiro Essencial [pendente]
- Refs: AC-700, AC-703, AC-705, AC-708, AC-709, AC-710, AC-711, AC-712, AC-715
- Arquivos: frontend/src/routes.tsx
- Notas: Depende de FIN-002–FIN-004. Criar feature financeira com tipos estritos e cliente compartilhado; formulários, recorrências, geração antes de leitura, filtros, resumo e histórico. Pago/Recebido e motivo de correção/exclusão. Testar componentes/serviço, reenvio, falha de geração e ausência de edição pecuária. Mapear novos arquivos antes de executar.

## T-705 — FIN-006 — Testes integrados, documentação e fechamento da sprint [pendente]
- Refs: AC-700, AC-701, AC-702, AC-703, AC-704, AC-705, AC-706, AC-707, AC-708, AC-709, AC-710, AC-711, AC-712, AC-713, AC-714, AC-715
- Arquivos: docs/product/financeiro.md, docs/architecture/database.md, .spec/features/financeiro-essencial/spec.md, .spec/features/financeiro-essencial/tasks.md
- Notas: Depende de FIN-002–FIN-005. Exercitar HTTP autenticado e persistência real, inicialização de banco vazio, atomicidade e concorrência, compras e ausência de efeitos na aquisição histórica/venda atual. Executar check-backend, check-frontend, check-all, diff --check, verify e audit --ci. Documentar evidência por critério; só fechar implementação com provas. Não implementar a sprint Vendas.

## Sequência e riscos

FIN-002 e FIN-006 devem incluir regressão de `ExcluirAnimalUseCase`: exclusão permitida de animal com despesa inicial preserva a linha financeira excluída logicamente e auditada, sem deixar a despesa nos indicadores. Validar o centro no cadastro/alteração, preservando referências históricas a animais posteriormente excluídos.

## Entregas e validação atual

- FIN-001 produziu SPEC e documentação de produto. A premissa confirmada pelo usuário é um sistema em desenvolvimento, sem dados financeiros antigos. Q-700 está respondida; AC-712 exige inicialização vazia com campos obrigatórios e situação/data efetiva consistentes.
- FIN-002.1 implementou `LancamentoFinanceiro` imutável, enums tipados, UUID e Builder em `model/builders`, delegando invariantes aos construtores. A interface de categoria fica em `enums/interfaces`. Nenhuma exceção para dados antigos foi criada no domínio.
- FIN-002.2 usa uma única entidade JPA da tabela e uma porta de lançamento com salvar/buscar por UUID. A V12 nova exige descrição, situação e tipo do centro de custo. Não há marcadores, projeções, classificações especiais, backfill ou leitura tolerante de dados financeiros incompletos. V1–V11 permanecem imutáveis.
- Compras operacionais criam despesas PENDENTE com descrição e origem. Salvar registros pelas operações existentes conserva situação, datas financeiras e origem. Esses metadados pertencem às operações normais; não são uma adaptação de dados de produção. Receitas são excluídas das consultas, somas e paginação de despesas; buscas de centros exigem ANIMAL/LOTE conforme a operação.
- Revisão adicional removeu o fallback de tipo de centro nulo em `DespesaMapper` e a consulta não utilizada que filtrava apenas o UUID de centro. Adicionado teste de regressão executado em H2 e PostgreSQL para rejeitar persistência sem tipo do centro. A documentação das etapas futuras foi alinhada à inicialização do sistema.
- Plano e decisões em `docs/exec-plans/fin-002-2-persistencia.md`. Validação da revisão atual: `./scripts/check-all.sh` passou com 281 testes backend (137 core, 87 usecase, 20 application, 37 infra), 27 node:test, 31 Vitest, lint/build e 15 testes PostgreSQL (11 Financeiro e 4 Rebanho), sem skips. O Financeiro executou 10 casos H2 e 11 PostgreSQL, incluindo a regressão de centro obrigatório. `git diff --check` passou. `onp-spec audit --ci` retorna 1 por requisitos financeiros integrais pendentes e pendências globais; não foi atribuída prova integral a critérios parciais. FIN-002 permanece pendente até todas as etapas e critérios completos. Sem casos de uso/endpoints/frontend novos, recorrências, confirmação, auditoria ou idempotência nesta etapa.

## FIN-002.3 — Cadastro e consulta por UUID

- Implementados RegistrarLancamentoFinanceiroUseCase e ConsultarLancamentoFinanceiroUseCase, com command tipado e reutilização de LancamentoFinanceiro/Builder/porta existentes. Construção valida invariantes no domínio; existência de centros usa AnimalRepository, LoteRepository e PastoRepository. GERAL não consulta portas de centro. Falha impede persistência.
- FinanceiroController expõe POST `/api/v1/financeiro/lancamentos` (201 com Location e lançamento criado) e GET `/api/v1/financeiro/lancamentos/{id}` (200 ou 404). DTOs separados, Bean Validation e handler global com exceção de recurso financeiro ausente. UUID gerado e origem operacional não são definidos pelo DTO de entrada.
- JWT e filtros reais permanecem ativos. Payload/UUID inválido ou centro inexistente retorna 400; falta de JWT ou token inválido/expirado retorna 401. Cadastro aceita PENDENTE ou EFETIVADO com data efetiva válida. Não foram alterados DespesaController, compras, vendas, domínio ou persistência/migrations.
- Idempotência e chaveOperacao não implementadas: cada POST válido cria novo lançamento. O contrato final da SPEC permanece para FIN-002.5. Confirmação posterior, edição/exclusão, auditoria, recorrências, listagem/filtros/resumo, integração de vendas e frontend ficam fora desta etapa.
- Acrescentado junit-jupiter-params somente no escopo de teste de usecase; o módulo não possuía a dependência para testes parametrizados. A primeira tentativa de check-backend falhou na compilação dos testes por esse motivo; adicionada a dependência, a suíte passou. Sem dependência nova de produção.
- Testes Mockito verificam cadastro/consulta, invariantes, existência/ausência de centros e ausência de efeitos indevidos. MockMvc em infra exercita controller, casos de uso, JWT, handler global e persistência reais. FinanceiroHttpPostgresIT reutiliza o contrato HTTP com Flyway e ddl-auto=validate; foi incluído explicitamente em check-postgres.sh.
- Branch feature/financeiro-core confirmada. A alteração prévia em scripts/check-all.sh foi preservada. Validação final concluída, detalhada abaixo; T-701/FIN-002 continua pendente até as etapas e critérios completos.

### Resultados de validação FIN-002.3 — 2026-10-16

- `./scripts/check-backend.sh` passou após acrescentar junit-jupiter-params: 344 testes naquele momento (39 HTTP). A versão final, com a proteção explícita de UUID/origem e Location, foi revalidada pelo mesmo script via `./scripts/check-all.sh`: **345 testes** (137 core, 111 usecase, 20 application, 77 infra), zero falhas/erros/skips. Mantidos os 98 casos do domínio e os 10 casos H2 da FIN-002.2.
- Novos casos de uso: **22 casos** de cadastro e **2 casos** de consulta, JUnit/Mockito. Cobrem ambos os tipos e situações, centros existentes/inexistentes, invariantes e nenhuma persistência na falha.
- `FinanceiroHttpTest`: **40 casos** com MockMvc, JWT, casos de uso e persistência H2 reais. Cobrem POST 201/Location, GET 200/404, payloads/UUID/centros inválidos 400, três tipos de centro existentes, valores/datas/categorias inválidos, ausência de JWT e tokens inválidos/expirados 401. Dados persistidos conferidos após flush/clear; UUID/origem enviados adicionalmente pelo cliente não controlam os campos persistidos.
- `./scripts/check-postgres.sh`: o sandbox não acessou Docker e falhou explicitamente sem executar testes. Repetição autorizada fora do sandbox **passou com 55 casos**, sem skips: **40 FinanceiroHttpPostgresIT**, **11 FinanceiroPostgresIT** e **4 RebanhoPostgresIT**. HTTP usa PostgreSQL 17 com Flyway e ddl-auto=validate, sem substituição de casos de uso ou desativação de segurança. Comprova cadastro e consulta por UUID sobre a persistência existente.
- `./scripts/check-frontend.sh`: **27 node:test + 31 Vitest**, lint e build passaram. Nenhum arquivo de frontend foi alterado.
- `./scripts/check-all.sh`: **código 0**, com todas as etapas acima, executado fora do sandbox para acesso ao Docker. A alteração prévia de scripts/check-all.sh permanece intacta.
- `git diff --check` passou. Domínio/Builder, portas existentes, adaptadores JPA, migrations, DespesaController, casos de compra/venda e segurança de produção não foram alterados. Só foi acrescentada a tradução da exceção de lançamento ausente no handler global.
- `onp-spec audit --ci`: **código 1, 189 erros e 94 avisos globais**. Persistem requisitos funcionais das etapas seguintes sem implementação/prova integral e pendências globais anteriores. As novas classes estão rastreadas em T-701. Não executar verify para atribuir prova integral por exit code a critérios ainda parciais (por exemplo correção de centros/exclusão auditada). Sem alteração de SPEC/testes para mascarar pendências.
- FIN-002.3 entrega cadastro e consulta autenticados com centros existentes e persistência PostgreSQL validada. FIN-002/T-701 permanece pendente por FIN-002.4–FIN-002.5 e demais provas integrais. Sem PR ou merge.
