# 🎩 POC - Ambiente Local com Minikube, Docker, kubectl e K6

Este repositório contém instruções detalhadas para configurar um ambiente local utilizando **Minikube**, **Docker**, **kubectl** e **K6**, visando facilitar a realização de provas de conceito (POC) e testes de carga simulados em Kubernetes.

---

## ✅ Pré-requisitos

É necessário utilizar uma máquina Linux (preferencialmente Ubuntu) e possuir permissões de superusuário.

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

```bash
minikube start
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

## 📊 Monitoramento e Logs com Prometheus, Grafana e Loki

### 📌 Para que servem?

- **Prometheus**: Coleta e armazena métricas de aplicações e infraestrutura.  
- **Grafana**: Visualiza métricas e logs de forma gráfica e interativa.  
- **Loki**: Gerencia e indexa logs, integrando-se ao Grafana para visualização.

---

## 🚀 Passo a Passo para Configuração

### 1. Instalar o Helm

```bash
curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 | bash
```

---

### 2. Adicionar Repositórios do Helm

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update
```

---

### 3. Criar Namespace para Monitoramento

```bash
kubectl create namespace monitoring
```

---

### 4. Instalar o Prometheus e o Grafana

```bash
helm install monitoring prometheus-community/kube-prometheus-stack --namespace monitoring
```

---

### 5. Instalar o Loki e Promtail

```bash
helm install loki grafana/loki-stack --namespace monitoring
```

Essa instalação inclui:
- Loki (coletor e indexador de logs)
- Promtail (agente para envio de logs dos pods)

---

### 6. Acessar a Interface do Grafana

```bash
kubectl --namespace monitoring port-forward svc/monitoring-grafana 3000:80
```

Acesse: [http://localhost:3000](http://localhost:3000)  
Usuário: `admin`  
Senha: `prom-operator`

---

### 7. Visualizar Logs no Grafana

1. No menu lateral do Grafana, vá em **Explore**.
2. Troque a fonte de dados para `Loki`. (http://loki.monitoring.svc.cluster.local:3100
)
3. Execute queries como:
   ```logql
   {app="my-app"} |= "error"
   ```
   para visualizar logs específicos da sua aplicação.

#### Exemplos de queries úteis:

```logql
{job="kubernetes-pods"}
{namespace="default"} |= "Exception"
{app="my-app"} |~ "(?i)warn|error"
```

#### Importar Dashboard de Logs pronto:

1. No Grafana, clique em **Dashboards > Import**.
2. Use o ID `13639` (Loki: Log Panels).
3. Escolha a fonte de dados `Loki` e conclua.

---

## ⚖️ Escalonamento Automático com KEDA

### 📌 Para que serve?

O **KEDA** permite escalonar automaticamente aplicações no Kubernetes com base em eventos externos (como mensagens em uma fila SQS, métricas personalizadas, etc.).

### 1. Instalar o KEDA via Helm

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
kubectl create secret generic aws-secret   --from-literal=AWS_ACCESS_KEY_ID=test   --from-literal=AWS_SECRET_ACCESS_KEY=test
```

### 4. Aplicar os Manifests

```bash
kubectl apply -f scaledobject-sqs.yaml
```

### ✅ Verificar se o Autoscaling Funciona

```bash
kubectl get hpa
kubectl get deployment my-app
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

Agora seu ambiente está pronto para uso!



