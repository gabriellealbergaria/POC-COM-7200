kubectl config get-contexts
kubectl config use-context minikube
kubectl config current-context

kubectl exec -it -n monitoring pod/monitoring-grafana-c666667cb-sth92 -- sh
kubectl logs -f -n monitoring pod/monitoring-grafana-c666667cb-sth92

kubectl exec -it -n monitoring pod/elasticsearch-master-0 -- sh
kubectl logs -f -n monitoring pod/elasticsearch-master-0


kubectl cp monitoring/monitoring-grafana-c666667cb-sth92:/etc/grafana/provisioning/datasources/datasource.yaml ./datasource.yaml
kubectl cp monitoring/monitoring-grafana-c666667cb-sth92:/etc/grafana/provisioning/datasources/loki-stack-datasource.yaml ./loki-stack-datasource.yaml


kubectl cp datasource.yaml monitoring/monitoring-grafana-c666667cb-sth92:/etc/grafana/provisioning/datasources/datasource.yaml
kubectl cp loki-stack-datasource.yaml monitoring/monitoring-grafana-c666667cb-sth92:/etc/grafana/provisioning/datasources/loki-stack-datasource.yaml


kubectl port-forward -n monitoring deployment/apm-server 8200:8200


kubectl logs -n monitoring -l k8s-app=filebeat


kubectl rollout restart deployment demo-publisher -n apps

