# Comandos úteis Kubernetes com explicações

## 🔄 Contextos do cluster

```bash
kubectl config get-contexts
```
Lista todos os contextos configurados no kubeconfig. Útil quando você trabalha com múltiplos clusters (ex: minikube, EKS, etc).

```bash
kubectl config use-context minikube
```
Define o contexto `minikube` como o contexto atual — ou seja, passa a executar os comandos nele.

```bash
kubectl config current-context
```
Mostra qual contexto está ativo no momento (em qual cluster você está conectado).

---

## 🛠️ Acessar pods com `exec` e visualizar logs

```bash
kubectl exec -it -n monitoring pod/monitoring-grafana-c666667cb-sth92 -- sh
```
Abre um terminal interativo (`sh`) dentro do pod do Grafana no namespace `monitoring`.

```bash
kubectl logs -f -n monitoring pod/monitoring-grafana-c666667cb-sth92
```
Mostra os logs em tempo real (`-f` de "follow") do pod do Grafana.

```bash
kubectl exec -it -n monitoring pod/elasticsearch-master-0 -- sh
```
Abre um shell dentro do pod do Elasticsearch.

```bash
kubectl logs -f -n monitoring pod/elasticsearch-master-0
```
Exibe os logs em tempo real do pod do Elasticsearch.

---

## 📁 Copiar arquivos entre o pod e o host

### ⬇️ Baixar arquivos do pod para sua máquina local:

```bash
kubectl cp monitoring/monitoring-grafana-c666667cb-sth92:/etc/grafana/provisioning/datasources/datasource.yaml ./datasource.yaml
kubectl cp monitoring/monitoring-grafana-c666667cb-sth92:/etc/grafana/provisioning/datasources/loki-stack-datasource.yaml ./loki-stack-datasource.yaml
```

### ⬆️ Enviar arquivos modificados de volta para o pod:

```bash
kubectl cp datasource.yaml monitoring/monitoring-grafana-c666667cb-sth92:/etc/grafana/provisioning/datasources/datasource.yaml
kubectl cp loki-stack-datasource.yaml monitoring/monitoring-grafana-c666667cb-sth92:/etc/grafana/provisioning/datasources/loki-stack-datasource.yaml
```

---

## 🌐 Redirecionar portas de serviços para sua máquina

```bash
kubectl port-forward -n monitoring deployment/apm-server 8200:8200
```
Permite acessar o serviço `apm-server` na porta `8200` localmente.

---

## 📜 Logs do Filebeat

```bash
kubectl logs -n monitoring -l k8s-app=filebeat
```
Mostra os logs de todos os pods com o label `k8s-app=filebeat` no namespace `monitoring`.

---

## 🔁 Reiniciar deploy da aplicação

```bash
kubectl rollout restart deployment demo-publisher -n apps
```
Reinicia os pods do deployment `demo-publisher` no namespace `apps`.

---

## 🧾 Aplicar configurações e reiniciar o Filebeat

```bash
kubectl apply -f filebeat.yaml
kubectl delete pod -n monitoring -l k8s-app=filebeat
```
Aplica o novo `filebeat.yaml` e reinicia os pods do Filebeat para aplicar a configuração atualizada.