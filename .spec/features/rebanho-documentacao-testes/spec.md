# Spec: Documentação e testes do Rebanho existente

> feature: rebanho-documentacao-testes
> status: em-implementacao

## Contexto

Recorte solicitado: apenas pontos 1 (documentação/rastreabilidade) e 5
(testes/validação) do plano de melhorias. As regras novas de lotes, formulário,
pesagem em duas chamadas e paginação visual NÃO fazem parte desta execução.

## Histórias

### US-313 — Confiar na documentação e nas provas do Rebanho

Como mantenedor, quero documentação fiel e testes executáveis dos fluxos
existentes, para evoluir o Rebanho sem confundir cobertura com funcionalidade futura.

#### AC-329 — Documentação representa somente funcionalidades existentes

- **Dado** o ciclo de vida documentado
- **Quando** forem consultadas as entradas, saídas, movimentações e migrations
- **Então** cadastro inicial estará presente, reversão de venda não será anunciada como implementada, histórico individual de lotes será futuro e V9 será descrita

#### AC-330 — Cadastro inicial é comprovado pela aplicação integrada

- **Dado** a aplicação com segurança, handler global e persistência reais
- **Quando** a API receber cadastros iniciais válidos e inválidos
- **Então** persistirá as três origens sem despesa ou pesagem, rejeitará ausência de autenticação, duplicidade e payload inválido sem inserir outro animal; nascimento futuro usará mensagem neutra quanto à categoria

#### AC-331 — Interface existente tem prova comportamental

- **Dado** o componente real de Rebanho e respostas controladas do serviço
- **Quando** o usuário filtrar, editar ou reverter uma morte
- **Então** ações por status, filtros atuais e recarga de matrizes funcionarão; falhas preservarão matrizes e aviso até recuperação, e a consulta de matrizes incluirá páginas adicionais

#### AC-332 — Migrations executam em PostgreSQL isolado

- **Dado** um PostgreSQL descartável vazio ou na V8 com dados legados
- **Quando** as migrations forem executadas até V9
- **Então** a origem legada será DESCONHECIDO, dados anteriores serão preservados e a constraint aceitará somente as três origens não nulas

#### AC-333 — Reversão mantém vínculo histórico mesmo com lote encerrado

- **Dado** um animal cuja morte preservou seu lote e esse lote foi encerrado depois
- **Quando** a morte for revertida
- **Então** o animal ficará ATIVO sem data de morte, mantendo o mesmo lote e sem reabri-lo

## Fora de escopo e cobertura pendente

- Bloquear novos vínculos com lotes encerrados: depende do ponto 2 não autorizado.
- Três operações no formulário, peso opcional e repetição apenas da pesagem: dependem do ponto 3 não autorizado.
- Paginação visual e busca/filtro no backend: dependem do ponto 4 não autorizado.
- Histórico individual de lotes, Financeiro, custos e alteração de migrations existentes.
- Estas pendências não serão representadas por testes pulados nem tarefas concluídas.

## Suposições

Nenhuma. O recorte foi solicitado explicitamente; os testes preservam os contratos atuais.

## Perguntas em aberto

Nenhuma. Execução sequencial confirmada, sem agentes adicionais e sem merge.
