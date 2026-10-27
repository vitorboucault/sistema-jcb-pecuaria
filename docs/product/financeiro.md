# Domínio: Financeiro Essencial

## Situação atual e proposta

O domínio e a persistência de receitas e despesas estão implementados em FIN-002.1–FIN-002.2. Compras operacionais com valor geram despesas PENDENTE; indicadores produtivos continuam consultando despesas por ocorrência. FIN-002.3 acrescenta cadastro e consulta por UUID via API autenticada, com validação de centros existentes. FIN-002.4 permite confirmar pagamentos e recebimentos integrais. Recorrências, resumo de caixa e demais entregas permanecem nas etapas seguintes de FIN-002–FIN-006.

A [especificação](../../.spec/features/financeiro-essencial/spec.md) concentra critérios verificáveis, DER, contratos e estratégia de evolução. As [tarefas](../../.spec/features/financeiro-essencial/tasks.md) definem as entregas seguintes.

## Operações do produtor

- Contas fixas: cadastrar salário do vaqueiro, energia, internet ou água como despesa mensal. Gerar os meses ao abrir o período, sem agendador. Cada lançamento é independente; corrigir ou pagar um mês não altera os outros.
- Despesas avulsas: informar descrição, valor positivo, categoria, referência e vencimento opcional para sal, medicamentos, combustível, manutenção e serviços.
- Receitas: registrar manualmente venda de animais, leite, produtos, serviços ou outras receitas enquanto não houver integração.

As categorias são enums fixos; despesas preservam as categorias atuais. Centros GERAL, ANIMAL, LOTE e PASTO continuam disponíveis, sem rateios.

## Pendência e dinheiro movimentado

PENDENTE aparece como Pendente. EFETIVADO aparece como Pago para despesas e Recebido para receitas. Pagamento e recebimento são integrais e exigem data efetiva conhecida. Não há parcelas nem pagamentos parciais.

Referência indica quando a operação ocorreu ou sua competência. Efetivação indica quando o dinheiro foi pago ou recebido. Exemplo: despesa de dezembro de 2026 paga em janeiro de 2027 aparece no histórico de dezembro por referência e nas despesas pagas de janeiro por efetivação.

Resultado do período = receitas recebidas − despesas pagas. Não é saldo bancário. Pendências não alteram esse resultado. Consultar mês ou ano usa a mesma tabela; contas sem vencimento não são vencidas e vencimento hoje ainda não é atraso. A lista global de pendências inclui contas de anos anteriores; pendências do resumo usam a referência no período e a situação atual.

## Correções e proteção contra duplicidade

Correções e exclusões exigem motivo e preservam autor, instante e antes/depois. Exclusão é lógica e retira o lançamento dos totais. Reenvios de cadastro, confirmações repetidas e geração concorrente não duplicam operações. Despesas recorrentes excluídas não reaparecem em uma nova geração.

O histórico ficará na própria linha em JSONB, conforme decisão da FIN-001. O calendário de vencimento seguirá America/Bahia. Ao abrir mês ou ano, a interface solicitará a geração e depois consultará os resultados; falha na geração será exibida para evitar apresentar um período incompleto como definitivo.

O financeiro começa do zero, sem registros antigos. Todos os lançamentos exigem descrição e situação; PENDENTE não tem data efetiva e EFETIVADO exige a data real do pagamento ou recebimento. Não há marcador ou classificação especial de legado financeiro.

## Relação com Rebanho e Vendas

Compra operacional com valor continua criando uma despesa vinculada ao animal; valor de compra não comprova pagamento. Aquisição histórica continua sem efeito financeiro atual. Indicadores existentes mantêm despesas por referência, separados do resultado por efetivação.

Venda de animais é operação pecuária independente. Hoje ela é individual e não gera receita financeira automática. Receita manual não vende animais nem encerra lotes. Em outra sprint, uma negociação poderá conter vários animais com registros individuais, lote de origem e baixa atômica; gerará uma única receita por negociação. Receitas geradas pela negociação terão origem explícita para evitar duplicações; receitas manuais são operações independentes.

## Fora do MVP

Vendas múltiplas e integração automática pertencem à sprint Vendas. Também ficam fora: parcelas, pagamentos parciais, contas e conciliação bancária, rateios complexos, contabilidade formal, custos individuais avançados, relatórios complexos de rentabilidade, Excel e comprovantes.

## API disponível na FIN-002.3

POST `/api/v1/financeiro/lancamentos` cadastra receita/despesa e retorna 201 com Location, UUID gerado e dados normalizados. GET `/api/v1/financeiro/lancamentos/{id}` retorna 200 ou 404; payload/UUID inválido retorna 400. Ambos exigem JWT válido. O cliente informa os atributos básicos do domínio, sem definir UUID ou origem operacional. O cadastro aceita PENDENTE ou EFETIVADO com data efetiva válida; centros ANIMAL, LOTE e PASTO precisam existir e GERAL não recebe referência.

O cadastro ainda não é idempotente: cada POST válido cria um lançamento. `chaveOperacao` e proteção contra reenvios são requisitos da FIN-002.5. A descrição de proteção contra duplicidade acima representa o contrato final, ainda planejado. Nesta etapa não há edição, exclusão, confirmação posterior, listagem, resumo ou recorrência.

## Confirmação disponível na FIN-002.4

POST `/api/v1/financeiro/lancamentos/{id}/pagar` para despesa e `/receber` para receita, com JWT e apenas `dataEfetivacao` obrigatória e não futura. Retorna 200 com situação EFETIVADO e a data real, mantendo integralmente os demais dados. Mesma data repetida retorna 200 sem nova alteração; outra data ou ação incompatível retorna 409. Payload/data/UUID inválido retorna 400; UUID inexistente, 404. Valores parciais e outros campos não pertencem ao payload e são rejeitados.

Confirmações concorrentes usam bloqueio da linha dentro da transação. A proteção de repetição da confirmação não depende de chave de cadastro. O atributo `versao` do contrato final e o controle otimista permanecem reservados para FIN-002.5, assim como auditoria, correções/exclusões e idempotência de cadastro.
