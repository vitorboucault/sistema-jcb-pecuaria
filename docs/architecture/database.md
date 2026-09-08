# Arquitetura de Banco de Dados - Sistema JCB Pecuária

O Sistema JCB utiliza o **PostgreSQL** como banco de dados relacional principal em ambientes de desenvolvimento e produção, e **H2** (em modo de compatibilidade PostgreSQL) durante a execução de testes automatizados.

---

## 1. Controle de Versão com Flyway

O versionamento do esquema é gerenciado exclusivamente pelo **Flyway**.

- **Localização dos scripts**: `infra/src/main/resources/db/migration/`
- **Padrão de nomenclatura**: `V<Versão>__<Descricao_Em_Snake_Case>.sql` (exemplo: `V5__Create_Tabela_Venda_Animal.sql`).

### Regra Inviolável de Imutabilidade
> [!IMPORTANT]
> **Migrations do Flyway são estritamente imutáveis.**
> Uma vez que um arquivo `V<N>__*.sql` tenha sido commitado ou aplicado em qualquer ambiente, ele **NUNCA** deve ser alterado ou renomeado. Qualquer modificação, correção de tipo, acréscimo de coluna, exclusão ou criação de índice deve ser feita através de uma **nova migration** sequencial (`V<N+1>__*.sql`).

---

## 2. Histórico Atual de Migrações

1. `V1__Create_Table.sql`: Tabelas base (`animal`, `lote`, `pesagem`, `usuario`).
2. `V2__Create_Tabelas_Reproducao.sql`: Tabelas de controle reprodutivo e inseminação.
3. `V3__Create_Tabelas_Parto_Desmame.sql`: Eventos de parto e desmame.
4. `V4__Create_Tabela_Movimentacao_Lote.sql`: Histórico de movimentações de animais entre lotes.
5. `V5__Create_Tabela_Venda_Animal.sql`: Registro de vendas e comercialização de animais.
6. `V6__Create_Tabela_Fornecimento_Racao.sql`: Registro de alimentação e suplementação por lote.
7. `V7__Add_Data_Morte_Animal.sql`: Inclusão de `data_morte` na tabela de animais para suporte a baixas e reversões.
8. `V8__Add_Origem_Pesagem.sql`: Inclusão da coluna `origem` em `pesagem` (`CADASTRO_INICIAL`, `ROTINA`).

---

## 3. Convenções de Modelagem e Nomenclatura

- **Identificadores (Chaves Primárias)**: Utilizar `UUID` como chave primária (`id UUID PRIMARY KEY`).
- **Nomenclatura**: Tabelas e colunas em `snake_case` (ex: `data_nascimento`, `peso_venda`).
- **Chaves Estrangeiras**: Criar restrições com prefixo explícito `fk_<tabela_origem>_<tabela_destino>` e índices nas colunas de relacionamento.
- **Enums**: Mapeados como `VARCHAR(30)` ou `VARCHAR(50)`, correspondendo fielmente aos nomes dos enums em Java no `core`.
