# Domínio: Financeiro Essencial

## Situação atual e proposta

Atualmente há `Despesa` persistida em `transacao_financeira`, compras operacionais que geram despesas quando há valor e indicadores produtivos por ocorrência. Não há controle implementado de pagamento, receitas financeiras, recorrências ou resumo de caixa. Esta documentação descreve a proposta FIN-001; a implementação está planejada em FIN-002–FIN-006.

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

## Correções, legado e proteção contra duplicidade

Correções e exclusões exigem motivo e preservam autor, instante e antes/depois. Exclusão é lógica e retira o lançamento dos totais. Reenvios de cadastro, confirmações repetidas e geração concorrente não duplicam operações. Despesas recorrentes excluídas não reaparecem em uma nova geração.

O histórico ficará na própria linha em JSONB, conforme decisão da FIN-001. O calendário de vencimento seguirá America/Bahia. Ao abrir mês ou ano, a interface solicitará a geração e depois consultará os resultados; falha na geração será exibida para evitar apresentar um período incompleto como definitivo.

Registros antigos sem evidência de pagamento mantêm situação e data efetiva ausentes. São exibidos para revisão e não entram como pagos, recebidos, pendentes ou vencidos até classificação explícita. Não presumir pagamento na data de ocorrência nem inventar descrições ou autores históricos.

## Relação com Rebanho e Vendas

Compra operacional com valor continua criando uma despesa vinculada ao animal; valor de compra não comprova pagamento. Aquisição histórica continua sem efeito financeiro atual. Indicadores existentes mantêm despesas por referência, separados do resultado por efetivação.

Venda de animais é operação pecuária independente. Hoje ela é individual e não gera receita financeira automática. Receita manual não vende animais nem encerra lotes. Em outra sprint, uma negociação poderá conter vários animais com registros individuais, lote de origem e baixa atômica; gerará uma única receita por negociação. A transição deverá reconciliar receitas manuais, sem duplicá-las nem inventar vínculos históricos.

## Fora do MVP

Vendas múltiplas e integração automática pertencem à sprint Vendas. Também ficam fora: parcelas, pagamentos parciais, contas e conciliação bancária, rateios complexos, contabilidade formal, custos individuais avançados, relatórios complexos de rentabilidade, Excel e comprovantes.
