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
├── features/             # Módulos verticais de negócio (feature-based)
│   ├── auth/             # Autenticação, login e tipos de sessão
│   ├── dashboard/        # Painel principal e métricas do rebanho
│   └── rebanho/          # Gestão de animais, lotes, pesagens e baixas
│       ├── api/          # Chamadas HTTP do domínio (ex: rebanhoService.ts)
│       ├── components/   # Componentes e modais específicos do domínio
│       ├── pages/        # Telas completas roteáveis (ex: RebanhoPage.tsx)
│       └── types/        # Interfaces e types TypeScript do domínio
├── shared/               # Recursos e utilitários compartilhados
│   ├── api/              # Instância central do Axios e interceptors
│   │   └── client.ts     # Cliente HTTP configurado com baseURL e auth token
│   ├── components/       # Componentes visuais comuns (layout, botões, modais)
│   └── types/            # Tipos compartilhados entre módulos
├── routes.tsx            # Declaração centralizada de rotas da aplicação
├── main.tsx              # Ponto de entrada da aplicação React
└── index.css             # Diretivas do Tailwind e estilos base
```

---

## 3. Padrões de Implementação

### 3.1. Arquitetura Feature-Based
- Cada funcionalidade de domínio reside dentro de seu próprio subdiretório em `features/`.
- Componentes e tipos exclusivos de uma feature ficam restritos ao seu diretório; caso um recurso passe a ser utilizado por duas ou mais features, deve ser promovido para `shared/`.

### 3.2. Camada de API e Cliente HTTP
- Toda comunicação com o backend passa pela instância compartilhada do Axios exportada em `shared/api/client.ts`.
- O cliente injeta automaticamente o token JWT armazenado em `@jcb:user` via interceptor de requisição.

### 3.3. Estilização
- Uso das classes utilitárias do Tailwind CSS v4 com paleta escura (dark theme) baseada em tons de `stone` e destaques em tons de `emerald` e `amber`.

---

## 4. Comandos de Validação e Execução

- **Validação Automatizada (CI / Lockfile + Linter + Build)**:
  ```bash
  ./scripts/check-frontend.sh
  ```
- **Ambiente de Desenvolvimento**:
  ```bash
  cd frontend && npm run dev
  ```
