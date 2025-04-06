FROM localstack/localstack:latest

# Copia o script para o diretório de inicialização do LocalStack
COPY create-queues.sh /etc/localstack/init/ready.d/create-queues.sh

# Dá permissão de execução
RUN chmod +x /etc/localstack/init/ready.d/create-queues.sh
