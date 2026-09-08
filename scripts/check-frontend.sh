#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

echo "==> [Frontend] Instalando dependências e validando qualidade..."
cd "${PROJECT_ROOT}/frontend"

echo "==> [Frontend] Executando npm ci..."
npm ci

echo "==> [Frontend] Executando linter..."
npm run lint

echo "==> [Frontend] Executando build de produção..."
npm run build

echo "==> [Frontend] Validação concluída com sucesso!"
