
# 🎩 POC - Ambiente Local com Minikube, Docker, kubectl e K6

Este repositório fornece um guia passo a passo para configurar um ambiente local com **Minikube**, **Docker**, **kubectl** e **K6**, ideal para provas de conceito (POCs), testes de carga e experimentação em Kubernetes.

---

## ✅ Pré-requisitos

Recomenda-se o uso de uma máquina **Linux (Ubuntu preferencialmente)** com permissões de superusuário.

Você precisará instalar os seguintes componentes antes de iniciar:

- [x] Docker e Docker Compose  
- [x] kubectl  
- [x] Minikube  
- [x] Helm  
- [x] K6

---

## 🐳 Docker & Docker Compose

### 📌 O que é?

O Docker permite executar containers localmente e é utilizado como driver padrão do Minikube, dispensando o uso de máquinas virtuais.

### ⚙️ Instalação

```bash
sudo apt update
sudo apt install docker.io docker-compose -y
```

### 🔓 Executar Docker sem `sudo` (opcional)

```bash
sudo groupadd docker
sudo gpasswd -a $USER docker
newgrp docker
docker run hello-world
docker context use default
```

---

## ⚙️ kubectl

### 📌 O que é?

`kubectl` é a ferramenta de linha de comando oficial do Kubernetes, essencial para aplicar manifests, inspecionar recursos e visualizar logs.

### ⚙️ Instalação

```bash
curl -LO "https://dl.k8s.io/release/$(curl -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl
sudo mv kubectl /usr/local/bin/
```

### 🔍 Verificação

```bash
kubectl version --client
kubectl get nodes
```

---

## ☘️ Minikube

### 📌 O que é?

O Minikube permite criar um cluster Kubernetes local de forma rápida, ideal para desenvolvimento e testes em um ambiente que simula produção.

### ⚙️ Instalação

```bash
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
chmod +x minikube-linux-amd64
sudo mv minikube-linux-amd64 /usr/local/bin/minikube
```

### 🔧 Configurar driver Docker

```bash
minikube config set driver docker
```

---

## 🚀 Inicializando o Cluster (Desenvolvimento Local)

Para máquinas com **32 GB de RAM**, aloque metade da memória para o cluster:

```bash
minikube start --memory=24576 --cpus=6 --driver=docker
```

| Memória da Máquina | Memória para Minikube |
|--------------------|------------------------|
| 8 GB               | 4 GB                   |
| 16 GB              | 8–10 GB                |
| **32 GB**          | **16 GB**              |
| 64 GB              | 24–32 GB               |

> 💡 **Dica:** Alocar até 50% da RAM do host para o cluster evita travamentos no sistema e mantém o ambiente fluido.

---

## ➕ Addons Úteis

```bash
minikube addons enable dashboard
```

### 📊 Acessar o Dashboard

```bash
minikube dashboard
```

---

## 📦 Helm

### 📌 O que é?

Helm é o gerenciador de pacotes do Kubernetes, facilitando a instalação e atualização de aplicações no cluster.

### ⚙️ Instalação

```bash
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
```

### 🔍 Verificação

```bash
helm version
```

---

## 📈 K6

### 📌 O que é?

K6 é uma ferramenta moderna para testes de carga, ideal para medir a performance de aplicações em Kubernetes.

### ⚙️ Instalação

```bash
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt update
sudo apt install k6
```

### 🔍 Verificação

```bash
k6 version
```

---

## ⚖️ Escalonamento com KEDA

### 📌 O que é?

O **KEDA** permite escalonar pods com base em eventos externos (como filas SQS, Kafka, métricas, etc).

### ⚙️ Instalação com Helm

```bash
helm repo add kedacore https://kedacore.github.io/charts
helm repo update
helm install keda kedacore/keda --namespace keda --create-namespace
```

---

## 📊 Observabilidade com Elasticsearch, Kibana

### 🧪 Preparação

```bash
kubectl create namespace monitoring
kubectl create namespace apps
minikube ssh -- "sudo sysctl -w vm.max_map_count=262144"
```

# 🚀 Iniciar Minikube com recursos customizados

```bash
minikube start \
  --memory=24576 \
  --cpus=6 \
  --driver=docker
```

# 🚀 Habilitar metrics to HPA

```bash
minikube addons enable metrics-server
```
# 📥 Instalar KEDA com Helm

```bash
helm repo add kedacore https://kedacore.github.io/charts
helm repo update
helm install keda kedacore/keda \
  --namespace keda \
  --create-namespace
```

# 📂 Criar Namespaces e Ajustar Configurações

```bash
kubectl create namespace monitoring
kubectl create namespace apps
```

# Ajustar configuração do kernel para Elasticsearch

```bash
minikube ssh -- "sudo sysctl -w vm.max_map_count=262144"
```

# 📑 Deploy do Elasticsearch

```bash
kubectl apply -n monitoring -f elastic/simple/ --recursive
```

# 🔑 Gerar token Elasticsearch-Kibana

```bash
kubectl exec -it elasticsearch-0 -n monitoring -- bash -c "elasticsearch-service-tokens create elastic/kibana kibana-token"
```

# 🐋 Configurar Docker para usar a VM do Minikube

```bash
eval $(minikube docker-env)
```

# 📦 Construir Imagens Docker Customizadas


```bash
docker build -t localstack-custom localstack/ && \
docker build -t demo-consumer demo-consumer/
```

# ▶️ Aplicar Cenários de Teste

```bash
kubectl apply -n apps -f localstack/localstack.yaml
```

## Scenario 1

```bash
kubectl apply -n apps -f demo-consumer/k8s/scenario1/ --recursive
```

## Scenario 2

```bash
kubectl apply -n apps -f demo-consumer/k8s/scenario2/ --recursive
```

## Port-forward para publicar mensagens diretamente nas filas

```bash
kubectl port-forward svc/localstack 4566:4566 -n apps
```

