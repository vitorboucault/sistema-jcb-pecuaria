# Spec: Jwt expirado frontend

> feature: jwt-expirado-frontend
> status: pronta

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

O cliente HTTP mantém um JWT expirado no navegador e atualmente não reage quando uma API protegida responde `401`. Esta feature faz o frontend limpar a sessão inválida e retornar o produtor ao login.

## Histórias

<!-- História de usuário: quem precisa, o que precisa e por quê. -->

### US-001 — Recuperar sessão expirada

Como produtor autenticado, quero ser levado ao login quando minha sessão expirar, para continuar usando o sistema sem permanecer em uma sessão inválida.

#### AC-001 — Sessão expirada encerra a sessão local

- **Dado** uma sessão `@jcb:user` armazenada e uma requisição com Bearer token
- **Quando** a API protegida responde HTTP `401`
- **Então** a sessão é removida do armazenamento local e o navegador é redirecionado para `/login`

#### AC-002 — Falha no login não entra em loop

- **Dado** que o usuário está na rota `/login` e envia credenciais inválidas
- **Quando** o endpoint de login responde HTTP `401`
- **Então** a falha é propagada para a tela de login sem executar redirecionamento adicional

#### AC-003 — Qualquer 401 fora do login encerra a sessão

- **Dado** uma requisição fora da rota de login, com ou sem Bearer token
- **Quando** ela responde HTTP `401`
- **Então** o interceptor remove `@jcb:user` e redireciona para `/login`

#### AC-004 — Erros de autorização diferentes de expiração são preservados

- **Dado** uma requisição autenticada
- **Quando** a API responde HTTP `403`
- **Então** o erro é propagado e a sessão permanece armazenada

#### AC-005 — Erros não relacionados ao JWT são preservados

- **Dado** uma requisição autenticada
- **Quando** a API responde HTTP `404`, `500` ou ocorre erro de rede
- **Então** o erro é propagado e a sessão permanece armazenada

## Fora de escopo

- Renovação automática ou refresh token.
- Alterações no backend, no contrato de autenticação ou no tempo de expiração do JWT.
- Tratamento automático de HTTP `403` como sessão expirada.

## Suposições

<!-- O que estamos ASSUMINDO sem confirmação. Status: aberta | confirmada | invalidada -->

| ID | Suposição | Status | Resolução |
|---|---|---|---|
| ASM-001 | HTTP `401` é o sinal de credencial ausente ou inválida para as rotas protegidas. | confirmada | Confirmado pelo filtro e pela configuração de segurança do backend. |
| ASM-002 | O fluxo de logout deve remover a chave `@jcb:user`, usada pelo `AuthProvider`. | confirmada | Confirmado pela implementação atual do `useAuth`. |

## Perguntas em aberto

<!-- O que ainda não sabemos. Status: aberta | respondida -->

| ID | Pergunta | Status | Resposta |
|---|---|---|---|
| Q-001 | Nenhuma pergunta em aberto. | respondida | O comportamento aprovado é limpar sessão e redirecionar para `/login`. |
