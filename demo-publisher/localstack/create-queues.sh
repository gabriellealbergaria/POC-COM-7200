#!/bin/bash

set -euo pipefail

# Criação das filas SQS com VisibilityTimeout de 5s
awslocal sqs create-queue --queue-name generic-queue --attributes VisibilityTimeout=5
awslocal sqs create-queue --queue-name generic-queue-fifo.fifo --attributes FifoQueue=true,VisibilityTimeout=5
awslocal sqs create-queue --queue-name generic-queue-real-time --attributes VisibilityTimeout=5
awslocal sqs create-queue --queue-name generic-queue-lazy-time --attributes VisibilityTimeout=5

# Criação do tópico SNS
TOPIC_ARN=$(awslocal sns create-topic --name generic-topic | grep TopicArn | cut -d'"' -f4)

# Função para obter o ARN da fila
get_queue_arn() {
  local QUEUE_NAME=$1
  awslocal sqs get-queue-attributes \
    --queue-url "http://localhost:4566/000000000000/${QUEUE_NAME}" \
    --attribute-name QueueArn \
    | grep QueueArn \
    | cut -d'"' -f4
}

# Aplica a policy SNS -> SQS via arquivo JSON temporário
apply_policy() {
  local QUEUE_NAME=$1
  local QUEUE_ARN=$2

  POLICY_FILE="/tmp/policy-${QUEUE_NAME}.json"

  cat > "$POLICY_FILE" <<EOF
{
  "Policy": "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":\"*\",\"Action\":\"sqs:SendMessage\",\"Resource\":\"${QUEUE_ARN}\",\"Condition\":{\"ArnEquals\":{\"aws:SourceArn\":\"${TOPIC_ARN}\"}}}]}"
}
EOF

  awslocal sqs set-queue-attributes \
    --queue-url "http://localhost:4566/000000000000/${QUEUE_NAME}" \
    --attributes file://"$POLICY_FILE"
}

# Inscreve a fila no tópico com filtro SNS e entrega raw
subscribe_queue() {
  local QUEUE_ARN=$1
  local FILTER=$2

  awslocal sns subscribe \
    --topic-arn "$TOPIC_ARN" \
    --protocol sqs \
    --notification-endpoint "$QUEUE_ARN" \
    --attributes "{
      \"FilterPolicy\": \"{\\\"type\\\": [\\\"$FILTER\\\"]}\",
      \"RawMessageDelivery\": \"true\"
    }"
}

# Mapeamento: nome da fila -> filtro
declare -A QUEUES=(
  ["generic-queue"]="generic"
#  ["generic-queue-fifo.fifo"]="fifo"
  ["generic-queue-real-time"]="real-time"
  ["generic-queue-lazy-time"]="lazy-time"
)

# Loop principal
for QUEUE_NAME in "${!QUEUES[@]}"; do
  FILTER="${QUEUES[$QUEUE_NAME]}"
  QUEUE_ARN=$(get_queue_arn "$QUEUE_NAME")

  apply_policy "$QUEUE_NAME" "$QUEUE_ARN"
  subscribe_queue "$QUEUE_ARN" "$FILTER"
done

echo "✅ Tópico e filas configurados e inscritos com sucesso com RawMessageDelivery!"
