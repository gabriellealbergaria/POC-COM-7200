import http from 'k6/http'
import { check } from 'k6'
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js'
import { Counter } from 'k6/metrics'

// Contadores customizados
const realTimeCounter = new Counter('real_time_msgs')
const lazyTimeCounter = new Counter('lazy_time_msgs')


export let options = {
  vus: 20, // número de VUs
  iterations: 11000, // total de mensagens enviadas
  thresholds: {
    //http_req_duration: ['p(95)<800'],
    http_req_failed: ['rate<0.01'],
    real_time_msgs: ['count>=5490', 'count<=5510'],
    lazy_time_msgs: ['count>=5490', 'count<=5510'],    
  },
}

export default function () {
  const type = __ITER % 2 === 0 ? 'real-time' : 'lazy-time'
  const now = new Date().toISOString()

  // Contador global único por mensagem
  const globalCounter = `VU${__VU}-ITER${__ITER}`

  const messageBody = JSON.stringify({
    uuid: uuidv4(),
    cenario: "cenario-1-11000-mensagens",
    mensagem: globalCounter,
    type: type,
    inputTimestamp: now,
  })

  const sqsUrlLazyTime = 'http://localhost:4566/000000000000/generic-queue-lazy-time'
  const sqsUrlRealTime = 'http://localhost:4566/000000000000/generic-queue-real-time'
  const sqsUrl = type === 'real-time' ? sqsUrlRealTime : sqsUrlLazyTime
  const body = `Action=SendMessage&MessageBody=${encodeURIComponent(messageBody)}&Version=2012-11-05`

  const res = http.post(sqsUrl, body, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  })

  check(res, {
    'status is 200': (r) => r.status === 200,
  })

  // Incrementa o contador específico
  if (type === 'real-time') {
    realTimeCounter.add(1)
  } else {
    lazyTimeCounter.add(1)
  }

  // (Opcional) Log para validação
  console.log(`Enviado por VU${__VU} | Tipo: ${type} | Contador: ${globalCounter}`)
}
