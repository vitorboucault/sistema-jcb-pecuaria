# Planos de Execução (ExecPlans)

Este diretório armazena os planos de execução (`ExecPlans`) para recursos complexos, migrações estruturais e refatorações de grande porte no Sistema JCB.

## Como utilizar

1. **Quando criar**: Sempre que for iniciar uma tarefa complexa, crie um novo plano baseado no template [.agents/templates/exec-plan-template.md](../../.agents/templates/exec-plan-template.md).
2. **Nomenclatura**: Nomeie o arquivo com a data e um resumo em kebab-case:
   `YYYY-MM-DD-nome-do-plano.md` (exemplo: `2026-09-08-harness-engineering.md`).
3. **Documento Vivo**: Durante o desenvolvimento, o agente e os desenvolvedores DEVEM manter o arquivo atualizado com progresso em tempo real, decisões no log e descobertas.
4. **Referência Normativa**: Consulte [.agents/PLANS.md](../../.agents/PLANS.md) para detalhes completos sobre o padrão de ExecPlans.
