# T-506 — Registrar aquisição histórica do animal

Este ExecPlan é um documento vivo. As seções `Progress`, `Surprises & Discoveries`, `Decision Log` e `Outcomes & Retrospective` serão mantidas durante a implementação.

Referência de diretrizes: [.agents/PLANS.md](../../.agents/PLANS.md)

## Purpose / Big Picture

Permitir que o cadastro inicial preserve data e/ou valor conhecidos da compra de um animal existente sem criar `Despesa` ou qualquer lançamento financeiro atual. O comportamento será observável no endpoint `POST /api/v1/animais/cadastrar` e no banco: `AquisicaoAnimal` será opcional, única por animal e removida em cascata quando o animal for excluído.

## Progress

- [x] (2026-09-29) Ler `AGENTS.md`, a SPEC atual, o domínio de Rebanho, as implementações e os testes relacionados.
- [x] (2026-09-29) Confirmar que US-316, AC-349…AC-356 e T-506 estão livres.
- [x] (2026-09-29) Corrigir a redação de peso de nascimento na SPEC; confirmar que o cenário simétrico de AC-344 já existe no HEAD.
- [x] Criar o modelo de domínio, porta, command, request e caso de uso.
- [x] Criar adapter JPA, migration V10 e testes da persistência/cascata.
- [x] Adicionar provas HTTP de aquisição, financeiro e atomicidade.
- [x] Atualizar documentação de produto e banco.
- [!] (2026-09-29) `check-backend.sh` passou; `check-all.sh` passou
  backend/frontend, mas não concluiu a etapa PostgreSQL porque Docker está
  indisponível; `onp-spec verify` passou.
- [!] `onp-spec audit --ci` executado, mas bloqueado por 188 erros globais
  preexistentes de código órfão e provas obsoletas em outras features; os
  AC-349…AC-356 ficaram com prova PASS.

## Surprises & Discoveries

- O teste simétrico de AC-344 já estava no commit `64c830c` da branch, embora a revisão textual o apontasse como ausente. Não será duplicado.
- Os testes Spring Boot de infra usam H2 com `ddl-auto=create-drop` e desativam Flyway; a prova real das constraints da V10 precisa continuar no `RebanhoPostgresIT` com Testcontainers.

## Decision Log

- **Decisão**: representar aquisição histórica em `AquisicaoAnimal`, não em `Animal` nem em `Despesa`.
  **Justificativa**: a aquisição tem semântica econômica do animal e ciclo próprio, mas não representa caixa atual.
  **Data/Autor**: 2026-09-29 - Codex, conforme requisito T-506.
- **Decisão**: criar registro somente quando data ou valor estiver conhecido.
  **Justificativa**: evita registros vazios e preserva ausência de informação como `null`.
  **Data/Autor**: 2026-09-29 - Codex, conforme requisito T-506.
- **Decisão**: usar FK `ON DELETE CASCADE` na V10 e testar no PostgreSQL.
  **Justificativa**: a aquisição não deve sobreviver ao animal corrigido/excluído.
  **Data/Autor**: 2026-09-29 - Codex, conforme requisito T-506.

## Outcomes & Retrospective

- `./scripts/check-backend.sh` passou com todos os módulos e testes.
- `./scripts/check-all.sh` passou backend e frontend; terminou com exit 1 porque
  a etapa PostgreSQL não foi executada: Docker está indisponível no ambiente.
- `onp-spec verify rebanho-s2-t1-origem-animal` provou 36/36 critérios,
  incluindo AC-349…AC-356.
- `onp-spec audit --ci` registrou 79/79 critérios provados, mas saiu com exit 1
  por sinais globais fora do escopo desta tarefa (188 erros e 79 avisos),
  incluindo código órfão e provas obsoletas de features anteriores.

## Context and Orientation

O domínio fica em `core`, a orquestração do cadastro em `usecase`, o contrato HTTP em `application` e os adapters/migration em `infra`. `RegistrarAnimalInicialUseCase` já salva `Animal` e, opcionalmente, `Pesagem` na mesma transação; a T-506 acrescenta a aquisição histórica nessa mesma operação. `RegistrarCompraAnimalUseCase` permanece operacional e não será modificado.

## Plan of Work

1. Acrescentar `AquisicaoAnimal` e `AquisicaoAnimalRepository` no `core`, cobrindo valor positivo, data não futura, data não anterior ao nascimento e rejeição de registro vazio.
2. Estender command/request e o caso de uso para validar origem `COMPRA`, construir a aquisição somente quando houver dado e salvá-la após animal/pesagem, sem qualquer dependência financeira.
3. Implementar entidade, mapper, repository Spring Data e adapter de infra.
4. Criar V10 com constraints de valor/dado conhecido, unicidade, FK e cascata; atualizar os testes PostgreSQL.
5. Provar por HTTP os cenários de dados opcionais, origem incompatível, valores inválidos, ausência de efeito financeiro, atomicidade e exclusão.
6. Atualizar a documentação e executar todos os gates obrigatórios.

## Concrete Steps

Executar na raiz `/Users/vbcripto/repositorios/sistemajcb`:

```bash
./mvnw --batch-mode --no-transfer-progress test
./scripts/check-backend.sh
./scripts/check-all.sh
node .agents/skills/onp-spec-driven/scripts/onp-spec.mjs verify rebanho-s2-t1-origem-animal
node .agents/skills/onp-spec-driven/scripts/onp-spec.mjs audit --ci
```

O teste PostgreSQL exige Docker disponível; se Docker estiver indisponível, `check-all.sh` deve ser reportado como não concluído, sem afirmar a validação completa.

## Validation and Acceptance

- Testes unitários comprovam os invariantes do modelo e as decisões do caso de uso.
- Testes HTTP comprovam o contrato do cadastro inicial, a persistência correta e que `transacao_financeira` não aumenta.
- Teste de atomicidade provoca falha no adapter de aquisição e verifica ausência de animal/pesagem persistidos.
- Teste PostgreSQL executa as migrations até V10, valida as constraints e verifica a remoção em cascata.
- `verify` deve produzir prova PASS para AC-349…AC-356. A auditoria global
  permanece bloqueada até o saneamento dos sinais preexistentes registrados em
  Outcomes.

## Idempotence and Recovery

As alterações são aditivas: V1–V9 não serão tocadas. Em caso de falha, corrigir código/testes e repetir o teste específico; não apagar nem reescrever migration versionada.

## Interfaces and Dependencies

Ao final, o contrato de domínio incluirá:

```java
void salvar(AquisicaoAnimal aquisicao);
Optional<AquisicaoAnimal> buscarPorAnimalId(UUID animalId);
```

O request/command receberá `dataCompraHistorica` e `valorCompraHistorico`, ambos opcionais e independentes. O frontend não será alterado nesta tarefa.
