# Tasks: Financeiro Essencial

> feature: financeiro-essencial

FIN-001 é documental. FIN-002–FIN-006 são planejamento, sem autorização de execução nesta sessão. As tarefas permanecem pendentes no motor até as provas aplicáveis; o registro documental de FIN-001 não simula PASS funcional. IDs SDD globais são distintos dos códigos das entregas.

## T-700 — FIN-001 — Especificação e modelagem [pendente]
- Refs: US-700, US-701, US-702, US-703, US-704, US-705
- Arquivos: .spec/features/financeiro-essencial/spec.md, .spec/features/financeiro-essencial/tasks.md, docs/product/financeiro.md
- Notas: Entrega documental produzida nesta sessão. Revisar evidências, DER de duas tabelas, limites do legado e contratos. Não representa implementação dos critérios funcionais.

## T-701 — FIN-002 — Receitas e despesas: backend, persistência e API [pendente]
- Refs: AC-700, AC-701, AC-702, AC-703, AC-704, AC-711, AC-712, AC-713, AC-714, AC-715
- Arquivos: core/src/main/java/com/br/core/domain/model/Despesa.java, core/src/main/java/com/br/core/domain/repository/DespesaRepository.java, usecase/src/main/java/com/br/usecase/financeiro/RegistrarDespesaUseCase.java, infra/src/main/java/com/br/infra/persistence/entity/DespesaEntity.java, infra/src/main/java/com/br/infra/persistence/repository/SpringDataDespesaRepository.java, application/src/main/java/com/br/application/rest/DespesaController.java
- Notas: Depende de FIN-001. Inventariar dados antes de novas migrations; preservar portas, compras e indicadores. Acrescentar domínio de lançamento, enums, campos, auditoria, idempotência e confirmação/correção/exclusão. Testes core de invariantes, Mockito de fluxos, MockMvc de contratos, integração de consultas/mappers e upgrade PostgreSQL com legado desconhecido. Corrigir filtros antes de inserir receitas. Aplicar histórico JSONB confirmado em ASM-701 e inventariar as fontes de Q-700.

## T-702 — FIN-003 — Despesas recorrentes mensais [pendente]
- Refs: AC-705, AC-706, AC-707
- Arquivos: .spec/features/financeiro-essencial/spec.md
- Notas: Depende de FIN-002. Criar modelo/porta/adapter de recorrência e migration nova; geração explícita transacional por mês/ano. Testar dois geradores concorrentes, unicidade após exclusão, mês com 28/29/30/31 dias, vigência, reenvio do modelo, edição independente e encerramento. Arquivos de implementação específicos serão mapeados antes da execução.

## T-703 — FIN-004 — Consultas, filtros e resumo financeiro [pendente]
- Refs: AC-708, AC-709, AC-710, AC-712, AC-713
- Arquivos: .spec/features/financeiro-essencial/spec.md
- Notas: Depende de FIN-002 e FIN-003. Implementar consultas por referência/efetivação, pendências globais, vencidos e resumo. Testar mudança de mês/ano, intervalo inclusivo, valores zero, exclusão, legado, paginação e isolamento de receitas nos indicadores. Aplicar calendário America/Bahia confirmado em ASM-700. Mapear arquivos antes de executar.

## T-704 — FIN-005 — Frontend do Financeiro Essencial [pendente]
- Refs: AC-700, AC-703, AC-705, AC-708, AC-709, AC-710, AC-711, AC-712, AC-715
- Arquivos: frontend/src/routes.tsx
- Notas: Depende de FIN-002–FIN-004. Criar feature financeira com tipos estritos e cliente compartilhado; formulários, recorrências, geração antes de leitura, filtros, resumo e histórico. Pago/Recebido, revisão de legado e motivo de correção/exclusão. Testar componentes/serviço, reenvio, falha de geração e ausência de edição pecuária. Mapear novos arquivos antes de executar.

## T-705 — FIN-006 — Testes integrados, documentação e fechamento da sprint [pendente]
- Refs: AC-700, AC-701, AC-702, AC-703, AC-704, AC-705, AC-706, AC-707, AC-708, AC-709, AC-710, AC-711, AC-712, AC-713, AC-714, AC-715
- Arquivos: docs/product/financeiro.md, docs/architecture/database.md, .spec/features/financeiro-essencial/spec.md, .spec/features/financeiro-essencial/tasks.md
- Notas: Depende de FIN-002–FIN-005. Exercitar HTTP autenticado e persistência real, upgrade com legado, atomicidade e concorrência, compras e ausência de efeitos na aquisição histórica/venda atual. Executar check-backend, check-frontend, check-all, diff --check, verify e audit --ci. Documentar evidência por critério; só fechar implementação com provas. Não implementar a sprint Vendas.

## Sequência e riscos

FIN-002 e FIN-006 devem incluir regressão de `ExcluirAnimalUseCase`: exclusão permitida de animal com despesa inicial preserva a linha financeira excluída logicamente e auditada, sem deixar a despesa nos indicadores. Validar o centro no cadastro/alteração, preservando referências históricas a animais posteriormente excluídos.

## Validação documental FIN-001 — 2026-10-12

- Branch `feature/financeiro-core` confirmada no mesmo commit da `origin/main` após `git fetch origin main`: `b9e049d`.
- Revisados domínio, arquitetura, migrations V1–V11, persistência financeira, compras, vendas, exclusão de animal e testes existentes. Nenhum arquivo Java, frontend ou migration foi alterado por esta entrega; alteração Java prévia permanece preservada.
- `git diff --check` passou; arquivos documentais novos também foram verificados com `git diff --no-index --check`, sem mensagens de whitespace.
- `./scripts/check-all.sh`: backend Maven passou; frontend passou em testes (27 node:test e 31 Vitest), lint e build. Etapa PostgreSQL falhou por Docker indisponível. Repetição de `bash scripts/check-postgres.sh` fora do sandbox também falhou pelo mesmo motivo; testes PostgreSQL não foram executados nesta sessão.
- `onp-spec audit --ci`: código 1. Os 16 critérios AC-700–AC-715 não têm testes de implementação, conforme escopo documental. A auditoria global também informa provas desatualizadas e arquivos sem rastreabilidade em outras áreas do projeto. Nenhuma prova funcional foi fabricada e nenhum teste foi criado para contornar o resultado.
- FIN-001 entrega documentos revisados; FIN-002–FIN-006 permanecem pendentes. A marcação SDD de T-700 permanece pendente porque suas referências são histórias funcionais futuras, ainda sem provas; não representa ausência dos documentos. Inventário de evidências do legado (Q-700) fica para FIN-002.

Executar na ordem FIN-002 → FIN-003 → FIN-004 → FIN-005 → FIN-006. Não gerar executor nem delegar implementações nesta FIN-001. Antes da execução, detalhar arquivos novos e plano de migrations. Riscos principais: mapper de receita como despesa, somas contaminadas, perda de metadados pelo adaptador legado, recriação após exclusão, pagamento histórico presumido e duplicação futura de receitas manuais de venda. Cada risco está vinculado aos critérios acima.
