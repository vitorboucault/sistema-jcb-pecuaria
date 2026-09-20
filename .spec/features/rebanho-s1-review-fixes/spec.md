# Spec: Correções da revisão do Rebanho S1

> feature: rebanho-s1-review-fixes
> status: auditada

<!--
  Como ler este arquivo (o formato é verificado por `onp-spec audit`):
  - US-xxx = história de usuário · AC-xxx = critério de aceite
    ASM-xxx = suposição · Q-xxx = pergunta em aberto
    São códigos de rastreio: ligam a especificação às tarefas e aos testes.
  - Toda história de usuário precisa de pelo menos um critério de aceite.
  - Todo critério de aceite precisa de Dado/Quando/Então completos.
  - Os códigos são únicos no projeto inteiro (nunca reutilize um número).
  - Suposições e Perguntas em aberto são OBRIGATÓRIAS: se não há nenhuma,
    escreva "Nenhuma." — mas desconfie: quase toda feature esconde uma.
-->

## Contexto

A revisão da Sprint 1 encontrou três riscos no fluxo de nascimentos: a lista
de matrizes pode ficar desatualizada após mutações, a consulta está limitada a
100 animais e o caso de uso não garante que a mãe seja uma matriz elegível.
Esta feature corrige esses riscos reutilizando o endpoint paginado existente,
sem criar uma nova API ou abstração de domínio.

## Histórias

### US-305 — Vincular somente matrizes elegíveis

Como produtor, quero que o nascimento aceite somente uma matriz válida, para
manter a genealogia e a composição do rebanho coerentes.

#### AC-310 — Matriz ativa e elegível pode ser vinculada

- **Dado** um animal ativo, do sexo FEMEA e da categoria VACA ou NOVILHA
- **Quando** ele for informado como mãe no registro de nascimento
- **Então** o nascimento é salvo com o vínculo para essa mãe

#### AC-311 — Mãe inelegível é rejeitada

- **Dado** um animal que seja macho, inativo ou não pertença à categoria VACA
  ou NOVILHA
- **Quando** ele for informado como mãe no registro de nascimento
- **Então** o sistema rejeita o registro e não salva o bezerro

### US-306 — Manter a seleção de matrizes atualizada

Como produtor, quero que a seleção de matriz reflita o estado atual do
rebanho, para não escolher uma matriz que acabou de morrer, ser vendida ou ser
alterada.

#### AC-312 — Mutação do rebanho atualiza a seleção de matriz

- **Dado** a seleção de matriz aberta após uma alteração de cadastro, edição,
  morte ou reversão de morte
- **Quando** a alteração for concluída com sucesso
- **Então** a seleção contém somente as matrizes atualmente ativas, FEMEA e de
  categoria VACA ou NOVILHA

### US-307 — Carregar todas as matrizes elegíveis

Como produtor, quero consultar todas as matrizes elegíveis, para que a seleção
de mãe não dependa de um limite arbitrário da primeira página.

#### AC-313 — Matrizes além da primeira página ficam disponíveis

- **Dado** mais matrizes elegíveis do que o tamanho de uma página do endpoint
- **Quando** o cadastro de nascimento for aberto
- **Então** todas as matrizes elegíveis retornadas pela paginação ficam
  disponíveis para seleção

### US-309 — Evitar recargas redundantes após cadastro

Como produtor, quero que o cadastro de animal atualize os dados uma única vez,
para evitar recargas redundantes após uma operação concluída com sucesso.

#### AC-314 — Cadastro recarrega os dados uma única vez

- **Dado** um cadastro de animal concluído com sucesso
- **Quando** `handleCadastrarAnimal` finalizar a chamada de cadastro
- **Então** ele executa `recarregarDados()` exatamente uma vez, sem callback adicional de sucesso no componente do formulário

### US-308 — Preservar matrizes quando a atualização falhar

Como produtor, quero manter a última lista conhecida de matrizes quando uma
atualização falhar, para continuar vendo opções utilizáveis e ser avisado da
defasagem sem apagar os dados já carregados.

#### AC-315 — Falha de matrizes preserva a lista e o aviso é recuperável

- **Dado** uma lista de matrizes já carregada
- **Quando** uma nova carga de matrizes falhar e depois outra carga for concluída com sucesso
- **Então** a lista conhecida não é substituída por uma lista vazia, um aviso é exibido durante a falha e esse aviso é limpo após a carga bem-sucedida

## Fora de escopo

- Nova API ou porta específica de matrizes.
- Alterações no contrato de autenticação, vendas ou reversão de vendas.
- Mudança no tamanho ou na paginação visual da tabela principal.
- Melhoria geral do reporter do motor `onp-spec`; os avisos de prova fraca
  permanecem como melhoria posterior do harness.

## Suposições

<!-- O que estamos ASSUMINDO sem confirmação. Status: aberta | confirmada | invalidada -->

| ID | Suposição | Status | Resolução |
|---|---|---|---|
| ASM-401 | A elegibilidade de uma matriz é exatamente status ATIVO, sexo FEMEA e categoria VACA ou NOVILHA. | confirmada | Regra já expressa na spec `rebanho-s1-close` e na seleção atual do frontend. |
| ASM-402 | A correção da lista completa pode reutilizar o endpoint paginado existente sem alterar a paginação da tabela. | confirmada | A nova consulta será isolada no fluxo de matrizes; a tabela continuará usando seu comportamento atual. |

## Perguntas em aberto

<!-- O que ainda não sabemos. Status: aberta | respondida -->

| ID | Pergunta | Status | Resposta |
|---|---|---|---|
| Q-401 | A seleção de matrizes deve carregar todas as páginas mesmo quando houver mais de 100 animais ativos? | respondida | Sim. A seleção deve carregar todas as páginas para não perder matrizes elegíveis por limite técnico. |
