# Arquitetura Frontend - Sistema JCB Pecuária

O frontend é uma Single Page Application (SPA) moderna, focada em alto desempenho, tipagem estrita e arquitetura modular orientada a recursos (features).

---

## 1. Stack Tecnológica

- **Framework**: React 19
- **Linguagem**: TypeScript 6
- **Build Tool**: Vite 8
- **Estilização**: Tailwind CSS v4
- **Roteamento**: React Router 7
- **Comunicação HTTP**: Axios
- **Ícones**: Lucide React

---

## 2. Estrutura de Diretórios (`frontend/src`)

```
frontend/src/
├── app/                  # Configurações globais e cliente HTTP (Axios)
├── assets/               # Imagens, fontes e arquivos estáticos
├── features/             # Módulos verticais de negócio (feature-based)
│   ├── auth/             # Login, sessão, autenticação
│   ├── dashboard/        # Painel principal e métricas do rebanho
│   └── rebanho/          # Gestão de animais, lotes, pesagens e baixas
│       ├── api/          # Funções de requisição HTTP (ex: animal.api.ts)
│       ├── components/   # Componentes de UI e modais do domínio
│       ├── hooks/        # Hooks customizados para estado e mutações
│       ├── pages/        # Telas completas roteáveis (ex: RebanhoPage.tsx)
│       └── types/        # Interfaces e types TypeScript do domínio
├── shared/               # Componentes, layouts e utilitários reutilizáveis
├── routes.tsx            # Declaração centralizada de rotas
├── main.tsx              # Ponto de entrada da aplicação
└── index.css             # Diretivas do Tailwind e estilos base
```

---

## 3. Padrões de Implementação

### 3.1. Arquitetura Feature-Based
- Cada funcionalidade de domínio reside dentro de seu próprio subdiretório em `features/`.
- Componentes exclusivos de uma feature não devem ser compartilhados globalmente; caso um componente passe a ser utilizado por duas ou mais features distintas, ele deve ser refatorado para `shared/`.

### 3.2. Camada de API (`api/*.api.ts`)
- Toda comunicação com o backend passa pela instância centralizada do Axios configurada em `app/api.ts`.
- Mapeamento direto com endpoints REST em `/api/v1/`.

### 3.3. Estilização
- Uso das classes utilitárias do Tailwind CSS v4 com paleta escura (dark theme) baseada em tons de `stone` e destaques em tons de `emerald` e `amber`.

---

## 4. Comandos de Validação e Execução

- **Validação Automatizada (Linter + Build)**:
  ```bash
  ./scripts/check-frontend.sh
  ```
- **Ambiente de Desenvolvimento**:
  ```bash
  cd frontend && npm run dev
  ```
