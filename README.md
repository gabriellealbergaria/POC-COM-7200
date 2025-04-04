# 🎩 POC - Ambiente Local com Minikube, Docker, kubectl e K6

Este repositório contém instruções detalhadas para configurar um ambiente local utilizando **Minikube**, **Docker**, **kubectl** e **K6**, visando facilitar a realização de provas de conceito (POC) e testes de carga simulados em Kubernetes.

---

## ✅ Pré-requisitos

É necessário utilizar uma máquina Linux (preferencialmente Ubuntu) e possuir permissões de superusuário.

Além disso, você precisará instalar os seguintes componentes antes de começar:

- [x] Docker e Docker Compose  
- [x] kubectl  
- [x] Minikube  
- [x] **Helm** (gerenciador de pacotes do Kubernetes)  
- [x] K6

---

## 🐳 Docker & Docker Compose

### 📌 Para que serve?

Docker permite executar containers localmente, sendo utilizado pelo Minikube como driver padrão, evitando o uso de máquinas virtuais adicionais.

### 📌 Instalação

```bash
sudo apt update
sudo apt install docker.io docker-compose -y
```

### 📌 Configuração opcional (Docker sem `sudo`)

```bash
sudo groupadd docker
sudo gpasswd -a $USER docker
newgrp docker
docker run hello-world
docker context use default
```

---

## ⚙️ kubectl

### 📌 Para que serve?

kubectl é a ferramenta oficial para gerenciar e interagir com clusters Kubernetes. É essencial para aplicar manifests, monitorar recursos e visualizar logs.

### 📌 Instalação

```bash
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl
sudo mv kubectl /usr/local/bin/
```

### 📌 Verificar instalação

```bash
kubectl version --client
kubectl get nodes
```

---

## ☘️ Minikube

### 📌 Para que serve?

Minikube permite criar rapidamente um cluster Kubernetes local para desenvolvimento e testes, simulando o ambiente de produção.

### 📌 Instalação

```bash
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
chmod +x minikube-linux-amd64
sudo mv minikube-linux-amd64 /usr/local/bin/minikube
```

### 📌 Configuração do driver Docker

```bash
minikube config set driver docker
```

### 📌 Inicializar o cluster

✅ Exemplo recomendado para 16 GB de RAM: (Ideal 8GB, mas vou usar 10GB)

```bash
minikube start --memory=10240 --cpus=4 --driver=docker
```

---

## 📦 Helm

### 📌 Para que serve?

Helm é o gerenciador de pacotes do Kubernetes, usado para instalar aplicações de forma simples e reutilizável.

### 📌 Instalação

```bash
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
```

### 📌 Verificar instalação

```bash
helm version
```

---

## 📈 K6

### 📌 Para que serve?

K6 é uma ferramenta poderosa para testes de carga, permitindo simular múltiplos usuários e medir a performance das aplicações em Kubernetes.

### 📌 Instalação

```bash
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt update
sudo apt install k6
```

### 📌 Verificar instalação

```bash
k6 version
```
---

## ⚖️ Escalonamento Automático com KEDA

### 📌 Para que serve?

O **KEDA** permite escalonar automaticamente aplicações no Kubernetes com base em eventos externos (como mensagens em uma fila SQS, métricas personalizadas, etc).

### 1. Instalar o KEDA usando YAML files

```bash
helm repo add kedacore https://kedacore.github.io/charts
helm repo update
helm install keda kedacore/keda --namespace keda --create-namespace
```

### 2. Exemplo de ScaledObject com SQS

```yaml
apiVersion: keda.sh/v1alpha1
kind: TriggerAuthentication
metadata:
  name: aws-trigger-auth
  namespace: default
spec:
  secretTargetRef:
    - parameter: awsAccessKeyID
      name: aws-secret
      key: AWS_ACCESS_KEY_ID
    - parameter: awsSecretAccessKey
      name: aws-secret
      key: AWS_SECRET_ACCESS_KEY
---
apiVersion: keda.sh/v1alpha1
kind: ScaledObject
metadata:
  name: my-app-sqs-scaler
  namespace: default
spec:
  scaleTargetRef:
    name: my-app
  pollingInterval: 30
  cooldownPeriod:  60
  minReplicaCount: 1
  maxReplicaCount: 10
  triggers:
    - type: aws-sqs-queue
      metadata:
        queueURL: http://localstack:4566/000000000000/minha-fila
        queueLength: "5"
        awsRegion: us-east-1
      authenticationRef:
        name: aws-trigger-auth
```

### 3. Criar os Segredos da AWS (mesmo para LocalStack)

```bash
kubectl create secret generic aws-secret --from-literal=AWS_ACCESS_KEY_ID=test --from-literal=AWS_SECRET_ACCESS_KEY=test
```

### 4. Aplicar os Manifests

```bash
kubectl apply -f scaledobject-sqs.yaml
```

---

## 📊 Monitoramento com Elasticsearch, Kibana e APM

### 📌 Para que serve?

O stack do Elasticsearch permite observabilidade completa em seu cluster Kubernetes, com:

- **Elasticsearch** para armazenamento e busca de logs e métricas;
- **Kibana** como interface gráfica para explorar dados e criar dashboards;
- **APM Server** para coletar dados de performance de aplicações.

### 📌 Ajustar parâmetro do kernel

```bash
minikube ssh -- "sudo sysctl -w vm.max_map_count=262144"
```

### 📌 Instalar componentes (fornecidos no diretório `elastic/`)

```bash
kubectl create -f elastic/elasticsearch.yaml
kubectl create -f elastic/kibana.yaml
kubectl create -f elastic/apm-server.yaml
```

### 📌 Acessar Kibana

```bash
minikube service kibana
```

---

## 🚀 Inicializando o Ambiente

### 📌 Executar o Local Stack

```bash
chmod +x create-queues.sh
docker-compose up -d
```

### 📌 Listar as filas criadas

```bash
aws --endpoint-url=http://localhost:4566 --region us-east-1 sqs list-queues
```

Agora seu ambiente está pronto para uso! O diretório `elastic/` já contém os manifests `elasticsearch.yaml`, `kibana.yaml` e `apm-server.yaml` prontos para aplicar no cluster.