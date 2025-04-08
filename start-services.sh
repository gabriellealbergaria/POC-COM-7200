#!/bin/bash

set -e

# Caminho base do projeto
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "📦 Subindo demo-publisher..."
cd "$BASE_DIR/demo-publisher"
docker compose up -d

echo "📦 Subindo demo-consumer..."
cd "$BASE_DIR/demo-consumer"
docker compose up -d

echo "✅ Todos os serviços foram iniciados com sucesso!"
