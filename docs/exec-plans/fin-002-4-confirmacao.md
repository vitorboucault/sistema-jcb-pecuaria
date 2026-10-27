# Confirmar pagamentos e recebimentos integralmente

Documento vivo conforme [.agents/PLANS.md](../../.agents/PLANS.md). Escopo FIN-002.4 de `.spec/features/financeiro-essencial/spec.md`, referências AC-703/AC-704. FIN-002 geral permanece pendente; sistema iniciado do zero, sem tratamento de legado.

## Purpose / Big Picture

Disponibilizar POST `/api/v1/financeiro/lancamentos/{id}/pagar` e `/receber` com apenas dataEfetivacao, retornando 200 com o lançamento integral efetivado. Repetição na mesma data não grava novamente; data diferente ou ação incompatível retorna 409. Data ausente/inválida/futura e campos adicionais são 400; inexistente é 404; JWT continua obrigatório.

## Progress

- [x] (2026-10-18) Revisar domínio, portas, adaptador, JPA, controller, segurança e testes anteriores; branch feature/financeiro-core limpa.
- [x] (2026-10-18) Implementar cópia imutável e idempotência no domínio; testes core e Mockito dos casos de uso.
- [x] (2026-10-18) Acrescentar busca com bloqueio e dois casos de uso transacionais, sem migrations ou versões.
- [x] (2026-10-18) Acrescentar endpoints/DTO, testes MockMvc e cenários concorrentes PostgreSQL.
- [x] (2026-10-23) Executar harness, verificar concorrência real e atualizar evidências/contrato incremental após retomar o trabalho interrompido.

## Surprises & Discoveries

- O DTO de cadastro segue o padrão de ignorar atributos JSON desconhecidos. Para confirmação, isso poderia ignorar um pedido de valor parcial e efetivar o total. O novo DTO rejeita campos adicionais, sem alterar cadastro/consulta.
- As anotações Jackson estavam apenas no classpath de teste de application. Acrescentada dependência explícita de jackson-annotations, gerenciada pelo BOM, para JsonAnySetter no DTO de confirmação. A primeira compilação acusou a ausência; corrigido o classpath sem trocar serializador.

## Decision Log

- (2026-10-18) Reutilizar PESSIMISTIC_WRITE na linha de transacao_financeira. A porta buscarPorIdParaAtualizacao exige transação; o adaptador usa propagação MANDATORY. Os casos de uso usam transação Jakarta até terminar a gravação. Assim uma segunda confirmação lê o estado da primeira depois de aguardar a linha, sem coluna de versão ou subsistema de idempotência.
- (2026-10-18) Métodos confirmarPagamento/confirmarRecebimento no domínio verificam tipo, data e repetição. Uma efetivação nova copia todos os atributos para outra instância; mesma data retorna this. Casos de uso salvam apenas quando a instância muda. IllegalArgumentException e IllegalStateException reutilizam handlers 400/409; recurso ausente reutiliza a exceção 404 existente.
- (2026-10-18) Origem operacional não faz parte do domínio imutável e já é preservada pelo adapter ao salvar. Testes HTTP com snapshot de toda a linha conferem inclusive origem, centro e datas de referência/vencimento.
- (2026-10-18) Concorrência é testada somente em PostgreSQL, com duas requisições autenticadas em threads/transações independentes. Uma terceira conexão segura a linha; pg_stat_activity comprova que ambas as leituras aguardam o bloqueio antes de liberá-lo. Mesma data deve produzir dois 200; datas diferentes, um 200 e um 409, sem alteração parcial.

## Outcomes & Retrospective

Backend passou com 389 testes; frontend passou com 58 testes, lint e build. PostgreSQL passou com 87 testes, incluindo quatro corridas de confirmação verificadas por bloqueios reais. check-all e diff --check passaram. Auditoria SDD retorna 1 (189 erros e 94 avisos globais), com entregas/provas integrais e achados globais pendentes; FIN-002 permanece pendente. Versionamento, auditoria, idempotência do cadastro e demais entregas de FIN-002.5 não foram implementados.

## Context and Orientation

`core/src/main/java/com/br/core/domain/model/LancamentoFinanceiro.java` é imutável e valida situação/data. A porta `core/src/main/java/com/br/core/domain/repository/LancamentoFinanceiroRepository.java` salva e consulta. JPA e adapter ficam em `infra/src/main/java/com/br/infra/persistence/`. Os novos casos de uso ficam em `usecase/src/main/java/com/br/usecase/financeiro/`; FinanceiroController e DTOs ficam em `application/src/main/java/com/br/application/`. GlobalExceptionHandler já traduz os três erros necessários e não precisa ser alterado.

## Plan of Work

Adicionar métodos de domínio e teste de cópia/repetição/falha. Acrescentar operação de leitura com bloqueio na porta/JPA/adapter, exigindo uma transação externa. Implementar ConfirmarPagamentoUseCase e ConfirmarRecebimentoUseCase sem regras duplicadas. Adicionar DTO estrito de data e endpoints usando a resposta existente. Ampliar FinanceiroHttpTest para persistência/contratos, mantendo seus testes anteriores, e FinanceiroHttpPostgresIT para quatro corridas: pagamento/recebimento e datas iguais/diferentes. A suíte já está incluída em scripts/check-postgres.sh.

## Concrete Steps

Na raiz executar `./scripts/check-backend.sh`, `./scripts/check-frontend.sh`, `./scripts/check-postgres.sh`, `./scripts/check-all.sh`, `git diff --check` e `node .agents/skills/onp-spec-driven/scripts/onp-spec.mjs audit --ci`. Docker é obrigatório e não pode ser pulado. Usar acesso fora do sandbox se necessário. O banco dos testes é descartável.

## Validation and Acceptance

Confirmar mantém UUID/valor/categoria/descrição/centro/origem e altera somente situação/data efetiva. Repetir a mesma data não chama salvar no Mockito; no HTTP retorna o mesmo conteúdo. Conflitos, datas inválidas, campos parciais e falhas de autenticação não alteram nenhum campo do snapshot da linha. Consultar por GET após flush/clear retorna a situação/data persistidas. Concorrência PostgreSQL aguarda ambas as leituras bloqueadas, usa timeouts e libera conexões/threads mesmo na falha; não depende de atrasos para definir qual operação vence.

## Idempotence and Recovery

Idempotência da confirmação é por situação/data na própria linha. Não há chave de cadastro ou versão. Nenhuma migration é alterada nem tabela criada. Testes comuns usam rollback; os concorrentes desativam a transação do teste para obter transações independentes e limpam somente seus UUIDs depois de encerrar threads/liberar locks. Em falha repetir o check afetado, sem enfraquecer testes.

## Interfaces and Dependencies

Porta: Optional<LancamentoFinanceiro> buscarPorIdParaAtualizacao(UUID). Domínio: LancamentoFinanceiro confirmarPagamento(LocalDate) e confirmarRecebimento(LocalDate). Casos de uso: executar(UUID, LocalDate) retorna LancamentoFinanceiro. DTO de confirmação aceita somente dataEfetivacao obrigatória; valor e versao não são aceitos. jackson-annotations é a única dependência declarada adicional; não há dependência de Spring no core/usecase.

Atualização inicial: execução local sequencial da etapa explicitamente autorizada, sem delegação ou executor headless.

## Artifacts and Notes

Resultados reexecutados em 2026-10-23 após conferir o estado salvo na retomada do servidor:

    ConfirmacaoLancamentoFinanceiroTest: Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
    ConfirmarLancamentoFinanceiroUseCaseTest: Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
    FinanceiroHttpTest: Tests run: 68, Failures: 0, Errors: 0, Skipped: 0
    FinanceiroHttpPostgresIT: Tests run: 72, Failures: 0, Errors: 0, Skipped: 0
    check-postgres.sh: Tests run: 87, Failures: 0, Errors: 0, Skipped: 0
    check-all.sh: Todas as verificações passaram com sucesso! (exit 0)
    onp-spec audit --ci: 189 erro(s), 94 aviso(s) (exit 1)

Atualização final: contrato incremental e tasks registram campos adicionais rejeitados, versao reservada para FIN-002.5 e idempotência de confirmação implementada sem chave de cadastro. Nenhuma migration, versão ou tabela foi criada. O teste concorrente comprovou que ambas as leituras aguardam o lock antes de permitir o commit, preservando todos os metadados. Os testes usam bancos descartáveis; Docker exigiu execução fora do sandbox. FIN-002.4 está validada; FIN-002 não está integralmente concluída.
