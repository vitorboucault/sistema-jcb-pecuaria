# <Título Curto e Orientado à Ação>

Este ExecPlan é um documento vivo. As seções `Progress`, `Surprises & Discoveries`, `Decision Log` e `Outcomes & Retrospective` DEVEM ser mantidas atualizadas conforme o trabalho avança.

Referência de diretrizes: [.agents/PLANS.md](../../.agents/PLANS.md)

## Purpose / Big Picture

Explique em poucas frases o que é ganho com esta alteração, qual comportamento visível ao usuário será habilitado e como verificar visual ou programaticamente que está funcionando.

## Progress

Use listas com caixas de seleção (`- [x]` ou `- [ ]`) com timestamps (UTC/local) para registrar cada etapa granular.

- [ ] (YYYY-MM-DD HH:MM) Descrição da etapa inicial
- [ ] Descrição da próxima etapa

## Surprises & Discoveries

Registre comportamentos inesperados, armadilhas encontradas, trade-offs de desempenho ou particularidades descobertas durante a implementação.

- **Observação**: ...
  **Evidência**: ...

## Decision Log

Registre toda decisão arquitetural ou de design tomada durante o plano:

- **Decisão**: ...
  **Justificativa**: ...
  **Data/Autor**: YYYY-MM-DD - <autor>

## Outcomes & Retrospective

Ao concluir marcos importantes ou o plano completo, resuma o que foi alcançado, eventuais pendências e aprendizados em relação ao objetivo inicial.

## Context and Orientation

Descreva o estado atual relevante assumindo que o leitor não conhece o histórico prévio.
- Módulos e arquivos afetados por caminho completo.
- Conceitos de domínio e termos não triviais.

## Plan of Work

Descreva a sequência de edições e adições em prosa técnica:
- Para cada modificação: arquivo, classe, método/função e a alteração exata.
- Mantenha aditivo primeiro e depois remova código obsoleto.

## Concrete Steps

Comandos exatos a serem executados e o diretório de trabalho:

```bash
# Diretório de execução
./scripts/check-backend.sh
```

## Validation and Acceptance

Critérios de aceitação comportamentais:
- Como testar manualmente ou via testes automatizados.
- Quais testes unitários/integrados devem passar.

## Idempotence and Recovery

- Se as etapas podem ser reexecutadas com segurança.
- Plano de rollback em caso de falha.

## Interfaces and Dependencies

Tipos, assinaturas de métodos, DTOs e contratos de API esperados ao término da execução:

```java
// Exemplo de contrato ou assinatura
```
