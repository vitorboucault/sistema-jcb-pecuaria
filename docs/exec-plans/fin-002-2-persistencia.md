# Persistir lançamentos financeiros a partir de banco vazio

Documento vivo conforme [.agents/PLANS.md](../../.agents/PLANS.md).

## Purpose / Big Picture

O usuário confirmou que não existem transações financeiras antigas. FIN-002.2 permite salvar e recuperar receitas/despesas pelo UUID, preservando os dados da FIN-002.1 e a porta DespesaRepository. Não entrega API, confirmação, auditoria, recorrência ou idempotência. A SPEC em `.spec/features/financeiro-essencial/spec.md` permanece o contrato; T-701 permanece pendente.

## Progress

- [x] (2026-10-13) Revisar SPEC, domínio, adaptadores e V1–V11; confirmar branch feature/financeiro-core limpa.
- [x] (2026-10-13) Criar V12 aditiva e evoluir a única entidade JPA.
- [x] (2026-10-13) Implementar porta, mapper e adaptador e integrar as operações Despesa.
- [x] (2026-10-13) Testar persistência, compras e inicialização PostgreSQL; executar harness e auditoria.

## Surprises & Discoveries

- Somatório e paginação atuais não filtram tipo DESPESA. Busca por lote filtra UUID, mas não tipo LOTE.
- O domínio exige tipo de centro de custo em todos os lançamentos; a revisão alinhou mapper, JPA e V12 a essa obrigatoriedade.

## Decision Log

- Decisão atual: remover o marcador e todo tratamento de legado financeiro, exigindo descrição e situação de todos os registros desde a V12. Justificativa: usuário confirmou que o financeiro começa do zero; decisões anteriores sobre legado estão substituídas. Data/Autor: 2026-10-13, usuário/Codex.

- Decisão: manter DespesaEntity como único mapeamento de transacao_financeira; adicionar mapper/porta específicos de LancamentoFinanceiro. Justificativa: evitar entidades conflitantes e preservar consumidores. Data/Autor: 2026-10-13, Codex.
- Decisão: a porta de domínio salva e busca LancamentoFinanceiro pelo UUID, sem filtros ou métodos de legado. Justificativa: todos os registros financeiros têm os campos obrigatórios completos desde a inicialização. Data/Autor: 2026-10-13, Codex.
- Decisão: V12 adiciona descrição e situação NOT NULL, vencimento, efetivação e origem operacional, mantendo data_transacao. Justificativa: a tabela não tem transações antigas; todos os novos registros seguem as invariantes financeiras. Data/Autor: 2026-10-13, Codex.
- Decisão: ao salvar pela visão Despesa existente, atualizar somente os campos dessa visão e preservar metadados financeiros. Novas despesas começam PENDENTE. Exclusão/auditoria continuam na FIN-002.5. Data/Autor: 2026-10-13, Codex.

## Outcomes & Retrospective

Persistência entregue com o tratamento de legado removido por solicitação do usuário. check-all passou com 281 testes backend, 58 frontend e 15 PostgreSQL, incluindo 10 casos H2 e 11 casos financeiros PostgreSQL. Q-700 foi respondida: não existem dados financeiros antigos. FIN-002 permanece pendente pelos requisitos de API, centros existentes e exclusão auditada e pelas demais etapas. Evidências atuais em `.spec/features/financeiro-essencial/tasks.md`.

## Context and Orientation

`core/.../model/LancamentoFinanceiro.java` valida novos registros. `infra/.../entity/DespesaEntity.java` representa transacao_financeira; seu mapper/adaptador existentes atendem compras e indicadores. V1 define NUMERIC(12,2); V11 é a última versão. `scripts/check-postgres.sh` seleciona testes reais explicitamente.

## Plan of Work

Adicionar V12 em infra/src/main/resources/db/migration. Evoluir DespesaEntity e DespesaMapper, mantendo construtor existente. Adicionar LancamentoFinanceiroRepository em core e mapper/adaptador em infra. Corrigir somatório, paginação e filtro de lote. Criar integração H2 para round-trip e compra real; criar FinanceiroPostgresIT para inicialização de banco vazio e constraints; incluir a classe no harness. Atualizar contagens esperadas de RebanhoPostgresIT para a versão mais recente.

## Concrete Steps

Na raiz, executar `./scripts/check-backend.sh`, `./scripts/check-frontend.sh`, `./scripts/check-postgres.sh`, `./scripts/check-all.sh`, `git diff --check` e `node .agents/skills/onp-spec-driven/scripts/onp-spec.mjs audit --ci`. Docker exige acesso fora do sandbox neste ambiente.

## Validation and Acceptance

Round-trip deve preservar UUID, categoria específica, valor, centro, descrição e todas as datas/situação. Receitas não devem aparecer em nenhuma consulta de despesas. Salvar a visão Despesa não deve apagar metadados. Compra real deve persistir uma despesa PENDENTE. Inicialização deve manter a tabela financeira vazia; novo registro incompleto deve ser rejeitado pelas constraints. PostgreSQL não pode ser pulado.

## Idempotence and Recovery

Flyway aplica V12 uma vez e valida checksums; não editar V1–V11. Testes usam bancos descartáveis com rollback ou containers fechados. Em falha, corrigir somente V12 ainda não versionada e implementação, repetir a validação afetada. Não há execução em produção nesta tarefa.

## Interfaces and Dependencies

Porta pura core com salvar(LancamentoFinanceiro) e buscarPorId(UUID), sem filtro especial de classificação. JPA/Spring permanecem em infra. Sem dependências novas.

Atualização inicial: plano limitado à etapa autorizada FIN-002.2; execução local sequencial, sem executor/delegação.

## Artifacts and Notes

O usuário confirmou que não existem transações financeiras antigas. A V12 é nova, não versionada, e foi ajustada sem alterar V1–V11. O contrato de AC-712, a SPEC e a documentação do produto foram atualizados por essa decisão explícita; os testes de migração de registros financeiros antigos saíram do escopo. Continuam os testes de dados completos, categorias, filtros, metadados, compras, inicialização vazia e constraints.


Atualização final de escopo: removidos marcador, getter, filtros e tratamento de legado financeiro. Banco e JPA exigem descrição/situação. Após check-all passar, acrescentada verificação explícita de tabela financeira vazia no teste PostgreSQL de inicialização; revalidação PostgreSQL passou com 14 testes, sem skips. git diff --check passou; auditoria SDD continua com código 1 (190 erros e 94 avisos globais), sem conclusão integral de FIN-002.

Revisão do sistema criado do zero: tipo do centro obrigatório em JPA e banco, sem fallback nulo no mapper. A consulta de centro sem filtro de tipo não tinha consumidores e foi removida. Documentação corrente consolidada sem tarefas de migração ou inventário de transações financeiras antigas. Teste de centro obrigatório roda em H2 e PostgreSQL; validação concluída: check-all e diff --check passaram. Auditoria SDD retorna 1 pelas pendências integrais e globais; FIN-002 permanece pendente.
