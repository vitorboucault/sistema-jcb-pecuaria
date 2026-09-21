# Design: Finalização do Rebanho

## Decisões

- O domínio preserva `loteId` em `registrarMorte()` e `reverterMorte()`; não
  haverá campo de lote anterior.
- A porta `AnimalRepository.buscarPorLote` continua com o mesmo contrato e o
  adapter passa a usar uma query derivada Spring Data que também filtra
  `status=ATIVO`.
- Os consumidores de quantidade, peso e cálculos operacionais continuam
  usando a porta existente, recebendo automaticamente apenas animais ativos.
- O frontend mantém o filtro de categoria local e faz nova chamada ao trocar
  o status. A seleção atual permanece após ações de morte/reversão.
- A validação frontend usa o `node:test` já presente, sem dependência nova;
  lint e build continuam sendo a validação principal da UI.

## Componentes afetados

1. `core`: invariantes de morte e reversão.
2. `infra`: método Spring Data derivado e adapter de lote.
3. `usecase/application`: testes dos consumidores existentes e contratos HTTP
   já expostos.
4. `frontend`: service Axios, seleção de status, ações, badges, data local e
   estado vazio.
