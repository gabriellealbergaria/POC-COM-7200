#!/bin/bash

# Fila(s) a monitorar (inclusive DLQs)
QUEUES=("generic-queue-real-time" "generic-queue-lazy-time")

# Endpoint do LocalStack
ENDPOINT="http://localhost:4566"
REGION="us-east-1"

# Arquivo de saída
OUTPUT_FILE="sqs_metrics.csv"

# Cabeçalho do CSV
HEADER="timestamp,queue,ApproximateNumberOfMessages,ApproximateNumberOfMessagesNotVisible,ApproximateNumberOfMessagesDelayed,CreatedTimestamp,LastModifiedTimestamp,VisibilityTimeout,MaximumMessageSize,MessageRetentionPeriod,DelaySeconds,ReceiveMessageWaitTimeSeconds"

# Criar o cabeçalho apenas se o arquivo estiver vazio
if [ ! -s "$OUTPUT_FILE" ]; then
  echo "$HEADER" > "$OUTPUT_FILE"
fi

while true; do
  TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')

  for QUEUE_NAME in "${QUEUES[@]}"; do
    # Obter URL da fila dinamicamente
    QUEUE_URL=$(aws --endpoint-url="$ENDPOINT" --region "$REGION" sqs get-queue-url \
      --queue-name "$QUEUE_NAME" \
      --query 'QueueUrl' \
      --output text 2>/dev/null)

    if [ -z "$QUEUE_URL" ]; then
      echo "Erro ao consultar $QUEUE_NAME (fila não encontrada?)"
      continue
    fi

    # Obter atributos da fila
    ATTRIBUTES_JSON=$(aws --endpoint-url="$ENDPOINT" --region "$REGION" sqs get-queue-attributes \
      --queue-url "$QUEUE_URL" \
      --attribute-name All 2>/dev/null)

    if [ -z "$ATTRIBUTES_JSON" ]; then
      echo "Erro ao obter atributos da fila $QUEUE_NAME"
      continue
    fi

    # Extrair atributos
    VISIBLE=$(echo "$ATTRIBUTES_JSON" | jq -r '.Attributes.ApproximateNumberOfMessages // "0"')
    NOT_VISIBLE=$(echo "$ATTRIBUTES_JSON" | jq -r '.Attributes.ApproximateNumberOfMessagesNotVisible // "0"')
    DELAYED=$(echo "$ATTRIBUTES_JSON" | jq -r '.Attributes.ApproximateNumberOfMessagesDelayed // "0"')
    CREATED=$(echo "$ATTRIBUTES_JSON" | jq -r '.Attributes.CreatedTimestamp // "0"')
    MODIFIED=$(echo "$ATTRIBUTES_JSON" | jq -r '.Attributes.LastModifiedTimestamp // "0"')
    VISIBILITY=$(echo "$ATTRIBUTES_JSON" | jq -r '.Attributes.VisibilityTimeout // "0"')
    MAX_SIZE=$(echo "$ATTRIBUTES_JSON" | jq -r '.Attributes.MaximumMessageSize // "0"')
    RETENTION=$(echo "$ATTRIBUTES_JSON" | jq -r '.Attributes.MessageRetentionPeriod // "0"')
    DELAY=$(echo "$ATTRIBUTES_JSON" | jq -r '.Attributes.DelaySeconds // "0"')
    WAIT_TIME=$(echo "$ATTRIBUTES_JSON" | jq -r '.Attributes.ReceiveMessageWaitTimeSeconds // "0"')

    # Gravar linha CSV
    echo "$TIMESTAMP,$QUEUE_NAME,$VISIBLE,$NOT_VISIBLE,$DELAYED,$CREATED,$MODIFIED,$VISIBILITY,$MAX_SIZE,$RETENTION,$DELAY,$WAIT_TIME" >> "$OUTPUT_FILE"
  done

  sleep 10
done
