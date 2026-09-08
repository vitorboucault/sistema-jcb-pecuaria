#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "==========================================="
echo "==> Iniciando verificação completa do JCB..."
echo "==========================================="

"${SCRIPT_DIR}/check-backend.sh"
echo ""
"${SCRIPT_DIR}/check-frontend.sh"

echo ""
echo "==========================================="
echo "==> Todas as verificações passaram com sucesso!"
echo "==========================================="
