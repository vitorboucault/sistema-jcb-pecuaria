# Tasks: Financeiro Essencial

> feature: financeiro-essencial

Premissa atual confirmada pelo usuário: o financeiro começa do zero, sem registros antigos.

FIN-001 é documental. FIN-002 será executada incrementalmente; FIN-002.1 (domínio) e FIN-002.2 (persistência) estão autorizadas nas entregas incrementais. FIN-002.3–FIN-002.5 e FIN-003–FIN-006 permanecem planejamento. As tarefas permanecem pendentes no motor até as provas integrais aplicáveis; o registro documental de FIN-001 não simula PASS funcional. IDs SDD globais são distintos dos códigos das entregas.

## T-700 — FIN-001 — Especificação e modelagem [pendente]
- Refs: US-700, US-701, US-702, US-703, US-704, US-705
- Arquivos: .spec/features/financeiro-essencial/spec.md, .spec/features/financeiro-essencial/tasks.md, docs/product/financeiro.md
- Notas: Entrega documental produzida nesta sessão. Revisar evidências, DER de duas tabelas, inicialização do sistema e contratos. Não representa implementação dos critérios funcionais.

## T-701 — FIN-002 — Receitas e despesas: backend, persistência e API [pendente]
- Refs: AC-700, AC-701, AC-702, AC-703, AC-704, AC-711, AC-712, AC-713, AC-714, AC-715
- Arquivos: core/src/main/java/com/br/core/domain/model/LancamentoFinanceiro.java, core/src/main/java/com/br/core/domain/model/builders/LancamentoFinanceiroBuilder.java, core/src/main/java/com/br/core/domain/enums/interfaces/CategoriaFinanceira.java, core/src/main/java/com/br/core/domain/enums/TipoLancamentoFinanceiro.java, core/src/main/java/com/br/core/domain/enums/SituacaoFinanceira.java, core/src/main/java/com/br/core/domain/enums/CategoriaReceita.java, core/src/main/java/com/br/core/domain/enums/CategoriaDespesa.java, core/src/test/java/com/br/core/domain/model/LancamentoFinanceiroTest.java, core/src/main/java/com/br/core/domain/repository/DespesaRepository.java, usecase/src/main/java/com/br/usecase/financeiro/RegistrarDespesaUseCase.java, infra/src/main/java/com/br/infra/persistence/entity/DespesaEntity.java, infra/src/main/java/com/br/infra/persistence/repository/SpringDataDespesaRepository.java, application/src/main/java/com/br/application/rest/DespesaController.java, core/src/main/java/com/br/core/domain/repository/LancamentoFinanceiroRepository.java, infra/src/main/java/com/br/infra/persistence/mapper/LancamentoFinanceiroMapper.java, infra/src/main/java/com/br/infra/persistence/mapper/DespesaMapper.java, infra/src/main/java/com/br/infra/persistence/adapter/LancamentoFinanceiroRepositoryImpl.java, infra/src/main/java/com/br/infra/persistence/adapter/DespesaRepositoryImpl.java, infra/src/main/resources/db/migration/V12__Evolve_Transacao_Financeira.sql, infra/src/test/java/com/br/infra/persistence/financeiro/FinanceiroPersistenceTest.java, infra/src/test/java/com/br/infra/persistence/financeiro/FinanceiroPostgresIT.java, infra/src/test/java/com/br/infra/persistence/migration/RebanhoPostgresIT.java, scripts/check-postgres.sh, docs/exec-plans/fin-002-2-persistencia.md
- Notas: Depende de FIN-001. Inicializar em banco sem transações financeiras antigas; preservar portas, compras e indicadores. Acrescentar domínio de lançamento, enums, campos, auditoria, idempotência e confirmação/correção/exclusão. Testes core de invariantes, Mockito de fluxos, MockMvc de contratos, integração de consultas/mappers e inicialização PostgreSQL com campos obrigatórios. Corrigir filtros antes de inserir receitas. Aplicar histórico JSONB confirmado em ASM-701 e observar a resposta de Q-700.

### Etapas incrementais de T-701

| Etapa | Escopo | Rastreabilidade | Estado |
|---|---|---|---|
| FIN-002.1 | Entidade imutável `LancamentoFinanceiro`, enums tipados, invariantes de construção e testes unitários sem infraestrutura. Preservar `Despesa`. | AC-700 e AC-701, somente domínio | Implementada; resultados de validação abaixo |
| FIN-002.2 | Novas migrations e persistência em banco sem transações financeiras antigas; exigir descrição/situação, preservar metadados e corrigir filtros da visão Despesa antes de receitas. | AC-700, AC-701, AC-712, AC-713, AC-714 | Implementada e validada nesta etapa; critérios integrais continuam parciais |
| FIN-002.3 | Casos de uso e API de cadastro/consulta; validar existência dos centros fora da entidade e preservar contratos existentes. | AC-700, AC-701, AC-713, AC-714, AC-715 | Pendente |
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
