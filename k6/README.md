# 📈 POC-K6-COM-7200

Este projeto contém um script de **teste de carga com K6** que simula o envio de mensagens para diferentes tipos de fila: `real-time` e `lazy-time`, de forma aleatória. O objetivo é validar a performance e escalabilidade do endpoint `/sns/publish` no contexto da POC COM-7199, que testa o uso de **KEDA** para escalonamento automático baseado em eventos.

---

## ✅ Pré-requisitos

- [x] [`k6`](https://k6.io/docs/getting-started/installation) instalado
- [x] Aplicação acessível via variável `TARGET_URL` (por ex.: Minikube, localhost etc.)

---

## 📁 Estrutura do projeto

```bash
.
├── script.js       # Script principal de carga
└── README.md       # Instruções
```

---

## 🚀 Executando o teste

O teste já está configurado para sortear entre os tipos `real-time` e `lazy-time` automaticamente a cada requisição:

### ✅ Teste local com endpoint exposto

```bash
TARGET_URL=http://localhost:3000 k6 run script.js
```

### ✅ Teste com Minikube

```bash
TARGET_URL=$(minikube service demo-publisher -n apps --url) k6 run script.js
```

---

## 📦 O que o teste faz

- Simula **10.000 usuários simultâneos**
- Executa por **2 minutos**
- Realiza requisições POST para `/sns/publish`
- Envia payload com dados simulados
- Alterna aleatoriamente entre `real-time` e `lazy-time` para o campo `type`

---

## 🧪 Exemplo de payload

```json
{
  "uuid": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "message": "Teste de carga via K6 para tipo real-time",
  "type": "real-time",
  "inputTimestamp": "2025-04-08T17:14:23.631Z",
  "outputTimestamp": "2025-04-08T17:14:23.631Z"
}
```

---

## 📊 Métricas monitoradas

| Métrica              | Objetivo                         |
|----------------------|----------------------------------|
| `http_req_duration`  | 95% das requisições < 500ms      |
| `http_req_failed`    | Taxa de erro < 1%                |

---

## 🛠️ Ajustes no volume de carga

Você pode editar o início do `script.js` para mudar os parâmetros:

```js
export let options = {
  vus: 5000,
  duration: '60s',
}
```

---

## 🔍 Contexto da POC

Este script faz parte da POC de escalabilidade da plataforma **Iris**, que compara o modelo atual de filas do **Hermes** com a proposta baseada em **KEDA**, separando filas de alta prioridade (`real-time`) e baixa prioridade (`lazy-time`) de forma inteligente.

Mais detalhes técnicos em:  
🔗 [DD-028 - Volumetria de envio de mensagens de WhatsApp](https://creditas.atlassian.net/wiki/spaces/CUD/pages/4503732272)