# 🧹 Limpar ambiente Docker

docker stop $(docker ps -q) && \
docker rm $(docker ps -aq) && \
docker rmi -f $(docker images -q) && \
docker volume rm $(docker volume ls -q)

# 🚀 Iniciar Minikube com recursos customizados

minikube start \
  --memory=24576 \
  --cpus=6 \
  --driver=docker

# 🚀 Habilitar metrics to HPA

minikube addons enable metrics-server

# 📥 Instalar KEDA com Helm

helm repo add kedacore https://kedacore.github.io/charts
helm repo update
helm install keda kedacore/keda \
  --namespace keda \
  --create-namespace

# 📂 Criar Namespaces e Ajustar Configurações

kubectl create namespace monitoring
kubectl create namespace apps

# Ajustar configuração do kernel para Elasticsearch
minikube ssh -- "sudo sysctl -w vm.max_map_count=262144"

# 📑 Deploy do Elasticsearch

kubectl apply -n monitoring -f elastic/simple/ --recursive

# 🔑 Gerar token Elasticsearch-Kibana

kubectl exec -it elasticsearch-0 -n monitoring -- bash -c "elasticsearch-service-tokens create elastic/kibana kibana-token"

# 🐋 Configurar Docker para usar a VM do Minikube

eval $(minikube docker-env)

# 📦 Construir Imagens Docker Customizadas

docker build -t localstack-custom localstack/ && \
docker build -t demo-consumer demo-consumer/

# ▶️ Aplicar Cenários de Teste

kubectl apply -n apps -f localstack/localstack.yaml

## Scenario 1
kubectl apply -n apps -f demo-consumer/k8s/scenario1/ --recursive

## Scenario 2
kubectl apply -n apps -f demo-consumer/k8s/scenario2/ --recursive

## Port-forward para publicar mensagens diretamente nas filas

kubectl port-forward svc/localstack 4566:4566 -n apps
