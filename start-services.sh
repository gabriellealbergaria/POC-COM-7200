#!/bin/bash

set -e

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "📦 [1/3] Ativando Docker no contexto do Minikube..."
eval $(minikube docker-env)

echo "🐋 [2/3] Buildando imagens do demo-publisher e demo-consumer..."
docker build -t demo-publisher "$BASE_DIR/demo-publisher"
docker build -t demo-consumer "$BASE_DIR/demo-consumer"

echo "🚀 [3/3] Subindo os containers localmente com Docker Compose..."

echo "📦 Subindo demo-publisher..."
cd "$BASE_DIR/demo-publisher"
docker compose up -d

echo "📦 Subindo demo-consumer..."
cd "$BASE_DIR/demo-consumer"
docker compose up -d

echo "✅ Ambiente local iniciado com sucesso!"
