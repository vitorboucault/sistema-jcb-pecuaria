#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}/.."

echo "==> [PostgreSQL] Verificando Docker para testes reais de migrations..."
if ! docker info >/dev/null 2>&1; then
    echo "ERRO: Docker indisponível. Testes PostgreSQL NÃO foram executados." >&2
    exit 1
fi
./mvnw --batch-mode --no-transfer-progress -pl infra -am \
    -Dtest=RebanhoPostgresIT -Dsurefire.failIfNoSpecifiedTests=false test
