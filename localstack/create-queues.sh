#!/bin/bash

set -euo pipefail

echo "📬 Criando filas principais sem DLQs..."

# Criar fila real-time sem DLQ
awslocal sqs create-queue \
  --queue-name generic-queue-real-time \
  --attributes '{
    "VisibilityTimeout": "10"
  }'

# Criar fila lazy-time sem DLQ
awslocal sqs create-queue \
  --queue-name generic-queue-lazy-time \
  --attributes '{
    "VisibilityTimeout": "10"
  }'

echo "✅ Filas principais criadas com sucesso."

echo "📄 Filas SQS criadas:"
awslocal sqs list-queues
