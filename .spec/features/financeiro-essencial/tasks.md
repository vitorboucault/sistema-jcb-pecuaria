# Tasks: Financeiro Essencial

> feature: financeiro-essencial

FIN-001 é documental. FIN-002 será executada incrementalmente; somente FIN-002.1 (domínio) está autorizada nesta entrega. FIN-002.2–FIN-002.5 e FIN-003–FIN-006 permanecem planejamento. As tarefas permanecem pendentes no motor até as provas integrais aplicáveis; o registro documental de FIN-001 não simula PASS funcional. IDs SDD globais são distintos dos códigos das entregas.

## T-700 — FIN-001 — Especificação e modelagem [pendente]
- Refs: US-700, US-701, US-702, US-703, US-704, US-705
- Arquivos: .spec/features/financeiro-essencial/spec.md, .spec/features/financeiro-essencial/tasks.md, docs/product/financeiro.md
- Notas: Entrega documental produzida nesta sessão. Revisar evidências, DER de duas tabelas, limites do legado e contratos. Não representa implementação dos critérios funcionais.

## T-701 — FIN-002 — Receitas e despesas: backend, persistência e API [pendente]
- Refs: AC-700, AC-701, AC-702, AC-703, AC-704, AC-711, AC-712, AC-713, AC-714, AC-715
- Arquivos: core/src/main/java/com/br/core/domain/model/LancamentoFinanceiro.java, core/src/main/java/com/br/core/domain/model/builders/LancamentoFinanceiroBuilder.java, core/src/main/java/com/br/core/domain/enums/CategoriaFinanceira.java, core/src/main/java/com/br/core/domain/enums/TipoLancamentoFinanceiro.java, core/src/main/java/com/br/core/domain/enums/SituacaoFinanceira.java, core/src/main/java/com/br/core/domain/enums/CategoriaReceita.java, core/src/main/java/com/br/core/domain/enums/CategoriaDespesa.java, core/src/test/java/com/br/core/domain/model/LancamentoFinanceiroTest.java, core/src/main/java/com/br/core/domain/repository/DespesaRepository.java, usecase/src/main/java/com/br/usecase/financeiro/RegistrarDespesaUseCase.java, infra/src/main/java/com/br/infra/persistence/entity/DespesaEntity.java, infra/src/main/java/com/br/infra/persistence/repository/SpringDataDespesaRepository.java, application/src/main/java/com/br/application/rest/DespesaController.java
- Notas: Depende de FIN-001. Inventariar dados antes de novas migrations; preservar portas, compras e indicadores. Acrescentar domínio de lançamento, enums, campos, auditoria, idempotência e confirmação/correção/exclusão. Testes core de invariantes, Mockito de fluxos, MockMvc de contratos, integração de consultas/mappers e upgrade PostgreSQL com legado desconhecido. Corrigir filtros antes de inserir receitas. Aplicar histórico JSONB confirmado em ASM-701 e inventariar as fontes de Q-700.

### Etapas incrementais de T-701

| Etapa | Escopo | Rastreabilidade | Estado |
|---|---|---|---|
| FIN-002.1 | Entidade imutável `LancamentoFinanceiro`, enums tipados, invariantes de construção e testes unitários sem infraestrutura. Preservar `Despesa`. | AC-700 e AC-701, somente domínio | Implementada; resultados de validação abaixo |
| FIN-002.2 | Novas migrations e persistência; descrever reconstrução de legado sem situação conhecida, preservar metadados e corrigir filtros da visão Despesa antes de receitas. | AC-700, AC-701, AC-712, AC-713, AC-714 | Pendente |
| FIN-002.3 | Casos de uso e API de cadastro/consulta; validar existência dos centros fora da entidade e preservar contratos existentes. | AC-700, AC-701, AC-713, AC-714, AC-715 | Pendente |
| FIN-002.4 | Confirmar pagamentos e recebimentos integrais com data efetiva e proteger repetições. | AC-703, AC-704 | Pendente |
| FIN-002.5 | Correções, exclusões auditadas, idempotência e regressões de compras, vendas, indicadores e exclusão de animal. | AC-702, AC-704, AC-711, AC-712, AC-713, AC-714, AC-715 | Pendente |

FIN-002.1 não implementa persistência/leitura de AC-700 nem existência real dos centros, correções ou ausência de persistência de AC-701. As tags `@spec:AC-700` e `@spec:AC-701` nos testes comprovam apenas invariantes do domínio. Não marcar esses critérios ou T-701 como integralmente provados/concluídos; a spec original permanece preservada. UUID informado passa pelas mesmas validações; situação nula é rejeitada também na reconstrução básica. O tratamento distinto de legado será definido em FIN-002.2. Nenhum método de liquidação ou exclusão é criado nesta etapa.

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

## Validação incremental FIN-002.1 — 2026-10-12

- Entregue `LancamentoFinanceiro` imutável, com construtores para novo UUID ou UUID existente. `CategoriaFinanceira` conecta os dois enums sem categorias livres; `CategoriaDespesa` preserva os valores existentes e acrescenta as cinco categorias previstas. `Despesa` e todos os seus consumidores permanecem inalterados.
- Validada construção com descrição normalizada, valor positivo até NUMERIC(12,2), sem arredondamento de fração de centavo, categoria compatível, referência obrigatória, situação/data efetiva consistentes e centros de custo estruturais. Normalização monetária usa escala 2; representações equivalentes com zeros adicionais são aceitas. Datas de efetivação usam America/Bahia; um construtor restrito ao pacote aceita `Clock` para testes determinísticos, sem dependências de infraestrutura.
- `./mvnw --batch-mode --no-transfer-progress -pl core test`: 124 testes passaram, incluindo 85 casos da entidade (JUnit parametrizado); sem falhas, erros ou testes pulados.
- `./scripts/check-backend.sh`: passou em todos os módulos. `./scripts/check-frontend.sh`: passaram 27 testes node:test, 31 Vitest, lint e build, sem alterar frontend.
- `bash scripts/check-postgres.sh`: Docker inacessível no sandbox; repetição autorizada fora do sandbox passou com 4 testes PostgreSQL. Os três componentes do harness foram executados separadamente; `check-all.sh` não foi repetido nesta etapa.
- `git diff --check`: passou. Nenhum arquivo de persistência, caso de uso, controller, migration ou frontend foi alterado.
- `onp-spec audit --ci`: código 1; AC-700 e AC-701 agora possuem testes de domínio, mas não prova integral no motor. Os outros 14 critérios financeiros ainda não possuem testes. Permanecem também achados globais anteriores. Não executar `verify financeiro-essencial` para atribuir prova integral a critérios cobertos apenas parcialmente: persistência/leitura e existência dos centros continuam pendentes.
- FIN-002.2 deve definir migrations aditivas, persistência e reconstrução do legado desconhecido, sem relaxar a obrigatoriedade de situação para novos lançamentos. T-701 continua pendente até concluir todas as etapas e comprovar seus critérios completos.

## Refatoração de construção FIN-002.1 — Builder e validações privadas

- Adicionado `LancamentoFinanceiro.builder()` com Builder interno fluente para todos os atributos. `build()` delega ao construtor, sem duplicar validações. Situação e tipo de custo continuam explícitos e obrigatórios. ID omitido gera UUID por construção; ID explícito é preservado e `id(null)` é rejeitado no build. Reutilizar o Builder não modifica entidades anteriores.
- Extraídos métodos privados estáticos para identificação, normalização da descrição, valor, categoria, datas/situação e centro de custo. Preservados os três construtores, ordem de validação, mensagens, normalização monetária, calendário e imutabilidade. Nenhum enum, consumidor de Despesa ou componente de infraestrutura foi alterado nesta refatoração.
- Preservados os 85 casos anteriores e adicionados 13 casos do Builder: construção e normalização, campos opcionais, UUID, campos obrigatórios omitidos, combinações inválidas e independência/reutilização. A suíte do core passou com 137 testes, incluindo 98 da entidade, sem falhas, erros ou skips.
- Executado `./scripts/check-all.sh`: backend, frontend (27 node:test, 31 Vitest), lint e build passaram. Etapa PostgreSQL não acessou Docker no sandbox; repetição autorizada de `bash scripts/check-postgres.sh` fora do sandbox passou com 4 testes em banco real. O comando agregado retornou 1 pela restrição inicial; seus componentes foram todos validados.
- `git diff --check` passou. Auditoria SDD continua sem prova integral de AC-700/AC-701 e com requisitos futuros e achados globais pendentes; T-701 continua pendente. A refatoração não implementa FIN-002.2 ou etapas posteriores.

## Extração do Builder para model/builders — FIN-002.1

- Extraído o Builder para `core/src/main/java/com/br/core/domain/model/builders/LancamentoFinanceiroBuilder.java`. `LancamentoFinanceiro.builder()` retorna o novo tipo e mantém a chamada fluente. O construtor do Builder é público porque o subpacote `model.builders` não compartilha acesso de pacote com `model`; `build()` continua delegando ao construtor validado da entidade.
- Atualizadas as referências de tipo nos testes e o mapeamento de arquivos de T-701. Nenhuma regra, enum ou consumidor de Despesa foi modificado nesta extração. Não criado Builder genérico ou validações duplicadas.
- Os 98 testes da entidade e os 137 do core passaram após a mudança de pacote. `./scripts/check-backend.sh` passou na versão final; testes, lint e build do frontend passaram via `./scripts/check-all.sh`. O agregado parou no acesso ao Docker dentro do sandbox; repetição autorizada de `bash scripts/check-postgres.sh` fora do sandbox passou com 4 testes PostgreSQL.
- `git diff --check` passou. Auditoria SDD continua com código 1 pelas provas financeiras parciais e pendências globais, sem conclusão integral de T-701.
