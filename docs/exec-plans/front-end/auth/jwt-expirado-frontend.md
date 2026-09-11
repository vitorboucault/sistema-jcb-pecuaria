# Corrigir tratamento de JWT expirado no frontend

Este ExecPlan é um documento vivo e segue as diretrizes de [.agents/PLANS.md](../../../../.agents/PLANS.md). A implementação deverá usar o agent `onp-spec-driven` e somente começará depois que este plano estiver revisado.

## Purpose / Big Picture

Quando o JWT do produtor expirar, o frontend deve reconhecer a resposta não autorizada, limpar a sessão inválida e levar o usuário ao login. Isso evita que a aplicação continue exibindo uma sessão aparentemente autenticada ou repita chamadas com um token vencido.

O comportamento será centralizado no cliente Axios, de modo que todas as chamadas autenticadas compartilhem a mesma correção. O backend, o formato do token e as regras de autorização permanecerão inalterados.

## Progress

- [x] (2026-09-08) Inspecionar o cliente Axios, o `AuthProvider`, as rotas e o filtro JWT do backend.
- [x] (2026-09-08) Confirmar o comportamento desejado: limpar a sessão e redirecionar para `/login`.
- [x] (2026-09-08) Registrar este ExecPlan antes da implementação.
- [x] (2026-09-08) Criar a especificação rastreável da feature no agent `onp-spec-driven`.
- [x] (2026-09-08) Implementar o interceptor de resposta do Axios.
- [x] (2026-09-08) Criar e executar os testes do tratamento de JWT expirado.
- [x] (2026-09-08) Executar lint, build, harness e auditoria final.

## Surprises & Discoveries

- O cliente em `frontend/src/shared/api/client.ts` já injeta o Bearer token, mas não possui interceptor de resposta.
  Evidência: só existe `api.interceptors.request.use(...)`.
- O `AuthProvider` considera a presença de `@jcb:user` suficiente para autenticação local.
  Evidência: `isAuthenticated` é calculado como `!!user`.
- O backend deixa o token inválido sem autenticação e a proteção das rotas deve resultar em `401`; o filtro não cria uma sessão alternativa.
  Evidência: `infra/src/main/java/com/br/infra/security/SecurityFilter.java` só popula o contexto quando `validarToken` retorna login válido.

## Decision Log

- **Decisão**: tratar somente HTTP `401` de requisições que possuíam Bearer token.
  **Justificativa**: `401` representa credencial ausente ou inválida; `403` é uma proibição de acesso e não deve apagar a sessão automaticamente.
  **Data/Autor**: 2026-09-08 / Codex.
- **Decisão**: remover `@jcb:user` e redirecionar com `window.location.replace('/login')`.
  **Justificativa**: limpa o estado persistido, reinicia o `AuthProvider` e evita que o histórico permita retornar diretamente à tela protegida.
  **Data/Autor**: 2026-09-08 / Codex.
- **Decisão**: não redirecionar quando a rota atual já for `/login`.
  **Justificativa**: evita loop durante falhas de autenticação no próprio endpoint de login.
  **Data/Autor**: 2026-09-08 / Codex.
- **Decisão**: propagar erros diferentes de JWT expirado.
  **Justificativa**: falhas `403`, `404`, `500`, rede e validação devem continuar sendo tratadas pela tela que iniciou a operação.
  **Data/Autor**: 2026-09-08 / Codex.

## Outcomes & Retrospective

A implementação foi concluída sem alterações no backend. O interceptor centralizado trata somente `401` de requisições autenticadas, limpa `@jcb:user`, redireciona para `/login` e preserva login inválido, `403` e demais erros. Os cinco critérios passaram no `verify` e a auditoria CI ficou limpa.

## Context and Orientation

O frontend React usa um cliente Axios compartilhado em `frontend/src/shared/api/client.ts`. O interceptor de requisição lê a sessão JSON armazenada em `localStorage` sob `@jcb:user` e adiciona `Authorization: Bearer <token>`.

O estado de autenticação é mantido em `frontend/src/features/auth/hooks/useAuth.tsx`. O componente `ProtectedRoute`, em `frontend/src/routes.tsx`, decide se renderiza as rotas protegidas com base em `isAuthenticated`.

O backend valida o JWT em `infra/src/main/java/com/br/infra/security/SecurityFilter.java`. Não será necessário alterar backend, banco, migrations ou contratos públicos.

## Plan of Work

1. Criar a especificação auditável da feature com uma história de usuário e critérios para `401` autenticado, sessão limpa, redirecionamento, ausência de loop no login e propagação dos demais erros.
2. Adicionar um interceptor de resposta em `client.ts`. Ele deverá verificar se o erro é Axios, se o status é `401`, se a requisição tinha `Authorization` e se a página atual não é `/login`. Nessa situação, removerá `@jcb:user` e usará `window.location.replace('/login')`; depois, rejeitará o erro para encerrar a cadeia da requisição.
3. Criar testes do cliente HTTP sem subir o backend. Os testes deverão controlar `localStorage`, a URL atual e uma resposta simulada do Axios, verificando o efeito observável e a propagação de erros.
4. Executar `./scripts/check-frontend.sh` e o fluxo do agent `onp-spec-driven` (`verify` e `audit --ci`).

## Concrete Steps

Executar todos os comandos a partir da raiz do repositório:

    node .agents/skills/onp-spec-driven/scripts/onp-spec.mjs licoes list
    node .agents/skills/onp-spec-driven/scripts/onp-spec.mjs new jwt-expirado-frontend
    ./scripts/check-frontend.sh
    node .agents/skills/onp-spec-driven/scripts/onp-spec.mjs verify jwt-expirado-frontend
    node .agents/skills/onp-spec-driven/scripts/onp-spec.mjs audit --ci

O `audit --ci` deverá retornar código `0`. Nenhum teste poderá ser marcado como skip ou todo.

## Validation and Acceptance

- Uma resposta `401` de endpoint protegido, enviada com Bearer token, remove `@jcb:user` e redireciona para `/login`.
- A chamada de login que falhar com `401` não entra em loop de redirecionamento.
- Uma resposta `403` não remove a sessão automaticamente.
- Respostas `404`, `500` e erros de rede continuam sendo rejeitadas pelo Axios para o chamador tratar.
- Uma requisição que não tinha Bearer token não dispara o fluxo de sessão expirada.
- `./scripts/check-frontend.sh` passa com lint e build Vite.
- Todos os critérios da especificação possuem testes anotados com `@spec:AC-xxx` e `onp-spec audit --ci` retorna `0`.

## Idempotence and Recovery

O interceptor deverá ser registrado uma única vez no módulo compartilhado. Os testes deverão limpar mocks e `localStorage` entre cenários. Se a implementação falhar, a recuperação consiste em restaurar somente `frontend/src/shared/api/client.ts` e os testes criados pela feature; nenhum dado persistente ou migration será afetado.

## Artifacts and Notes

Arquivos previstos:

- `docs/exec-plans/jwt-expirado-frontend.md` — este plano.
- `.spec/features/jwt-expirado-frontend/spec.md` — especificação auditável.
- `.spec/features/jwt-expirado-frontend/tasks.md` — tarefas e rastreabilidade.
- `frontend/src/shared/api/client.ts` — interceptor de resposta.
- `frontend/tests/client.test.js` — testes do cliente Axios.

## Interfaces and Dependencies

Não haverá nova API pública de backend.

O cliente Axios continuará exportando:

    export const api: AxiosInstance;

O novo interceptor deverá preservar o contrato de rejeição das Promises Axios para que as telas existentes continuem recebendo erros não relacionados à expiração.
