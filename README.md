# Sistema JCB Pecuária

> Sistema de Gestão de Pecuária de Ciclo Completo

![status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
![java](https://img.shields.io/badge/Java-21-orange)
![spring](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen)

> ⚠️ **Projeto em desenvolvimento — ainda não está pronto para uso.**
> Funcionalidades, estrutura de módulos e instruções de execução podem mudar sem aviso. Não recomendado para uso em produção ou por terceiros neste momento.

## Sobre o projeto

O **Sistema JCB Pecuária** tem como objetivo apoiar a gestão de pecuária de ciclo completo (cria, recria e engorda), centralizando o controle de rebanho, manejo e demais processos da atividade pecuária em uma única aplicação.

## Status do projeto

- [ ] Definição completa dos requisitos e regras de negócio
- [ ] Implementação das funcionalidades principais
- [ ] Testes automatizados cobrindo os módulos
- [ ] Documentação da API
- [ ] Pipeline de CI/CD
- [ ] Primeira versão utilizável (release)

## Arquitetura

O backend é organizado em módulos Maven, seguindo uma separação inspirada em Arquitetura Limpa/Hexagonal:

| Módulo | Responsabilidade |
|---|---|
| `core` | Regras de negócio e entidades de domínio |
| `usecase` | Casos de uso da aplicação |
| `application` | Ponto de entrada da aplicação (ex.: API REST) |
| `infra` | Integrações com infraestrutura (banco de dados, serviços externos etc.) |
| `coverage` | Agregação de cobertura de testes (JaCoCo) |

O `frontend/` contém a aplicação cliente, servida separadamente do backend.

## Tecnologias

**Backend**
- Java 21
- Spring Boot 4.1.1
- Maven (multi-módulo)
- JaCoCo (cobertura de testes)

**Frontend**
- Node.js / Vite *(detalhes a confirmar)*

## Como executar (provisório)

> As instruções abaixo são um ponto de partida e ainda precisam ser validadas/ajustadas conforme o projeto evolui.

### Backend

```bash
./mvnw clean install
./mvnw spring-boot:run -pl application
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

### Pré-requisitos

- JDK 21+
- Maven (ou usar o wrapper `mvnw` incluído)
- Node.js (versão a definir)
- Banco de dados: *a definir*

## Estrutura do repositório

```
sistema-jcb-pecuaria/
├── core/
├── usecase/
├── application/
├── infra/
├── coverage/
├── frontend/
└── pom.xml
```

## Roadmap

- [ ] Escrever documentação de arquitetura mais detalhada
- [ ] Adicionar testes automatizados
- [ ] Configurar CI (build + testes a cada push/PR)
- [ ] Definir e documentar variáveis de ambiente/configuração
- [ ] Publicar primeira versão (release)

## Contribuindo

O projeto ainda não está aberto para contribuições externas de forma estruturada. Issues e sugestões podem ser abertas, mas fluxos de contribuição (guia, padrões de commit, etc.) ainda serão definidos.

## Licença

Este é um software **proprietário**. Todos os direitos são reservados.

Copyright © 2026 Vitor Boucault. Nenhuma parte deste repositório (código-fonte, documentação ou demais arquivos) pode ser copiada, modificada, distribuída, sublicenciada ou usada para fins comerciais sem autorização prévia e por escrito do autor.

Consulte o arquivo [`LICENSE`](./LICENSE) para os termos completos.