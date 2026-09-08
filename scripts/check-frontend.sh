#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

echo "==> [Frontend] Validando com ESLint e build TypeScript..."
cd "${PROJECT_ROOT}/frontend"

if [ ! -d "node_modules" ]; then
    echo "==> [Frontend] node_modules não encontrado. Executando npm ci..."
    npm ci
fi

echo "==> [Frontend] Executando linter..."
npm run lint

echo "==> [Frontend] Executando build de produção..."
npm run build

echo "==> [Frontend] Validação concluída com sucesso!"
