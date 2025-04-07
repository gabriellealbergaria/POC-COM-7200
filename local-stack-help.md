## 📬 Testes com SQS/SNS (via LocalStack)

### 📌 Executar o Local Stack

```bash
chmod +x create-queues.sh
docker-compose up -d
```

### 📌 Listar as filas criadas

```bash
aws --endpoint-url=http://localhost:4566 --region us-east-1 sqs list-queues
```

### 📌 Verificar se os tópicos foram criados

```bash
aws --endpoint-url=http://localhost:4566 --region us-east-1 sns list-topics
```

### 📌 Verificar as subscriptions

```bash
aws --endpoint-url=http://localhost:4566 --region us-east-1   sns list-subscriptions-by-topic --topic-arn arn:aws:sns:us-east-1:000000000000:generic-topic 
```

### 📌 Publicar mensagem no tópico

```bash
aws --endpoint-url=http://localhost:4566 --region us-east-1   sns publish   --topic-arn arn:aws:sns:us-east-1:000000000000:generic-topic   --message "Mensagem para fila generic"   --message-attributes '{"type": {"DataType": "String", "StringValue": "generic"}}'
```

### 📌 Publicar mensagem na fila

```bash
aws --endpoint-url=http://localhost:4566 --region us-east-1   sqs send-message   --queue-url http://localhost:4566/000000000000/generic-queue   --message-body "Mensagem enviada direto para a fila generic"
```

### 📌 Verificar se a mensagem chegou

```bash
aws --endpoint-url=http://localhost:4566 --region us-east-1   sqs receive-message   --queue-url http://localhost:4566/000000000000/generic-queue   --wait-time-seconds 5   --visibility-timeout 0   --message-attribute-names All   --max-number-of-messages 10
```

### 📌 Limpar a fila

```bash
aws --endpoint-url=http://localhost:4566 --region us-east-1   sqs purge-queue   --queue-url http://localhost:4566/000000000000/generic-queue
```