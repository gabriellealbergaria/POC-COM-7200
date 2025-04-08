package com.example.demo.consumer.consumer

import com.example.demo.consumer.model.PublishRequestDTO
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Component
class Queue1Consumer(
    private val sqsClient: SqsClient,
    private val objectMapper: ObjectMapper,
    @Value("\${aws.sqs.queue-url-1}") private val queueUrl: String,
    @Value("\${aws.sqs.consumer-1-enabled}") private val enabled: Boolean,
    @Value("\${aws.sqs.consumer-1-parallel}") private val parallelProcessing: Boolean,
    @Value("\${aws.sqs.consumer-1-threads}") private val threadCount: Int,
    @Value("\${aws.sqs.consumer-1-delay}") private val processingDelay: Duration
) {

    private val pollingExecutor = Executors.newSingleThreadExecutor()
    private val processingExecutor: ExecutorService =
        if (parallelProcessing) Executors.newFixedThreadPool(threadCount)
        else Executors.newSingleThreadExecutor()

    private val log = LoggerFactory.getLogger(Queue1Consumer::class.java)

    @PostConstruct
    fun start() {
        if (!enabled) {
            log.info("Queue1Consumer desativado por variável de ambiente.")
            return
        }

        log.info("Queue1Consumer iniciado. Paralelismo: {}, Threads: {}, Delay: {}", parallelProcessing, threadCount, processingDelay)

        pollingExecutor.submit {
            while (true) {
                val request = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(5)
                    .build()

                val messages = sqsClient.receiveMessage(request).messages()
                for (msg in messages) {
                    processingExecutor.submit {
                        try {
                            val dto: PublishRequestDTO = objectMapper.readValue(msg.body())
                            log.info("Processando mensagem: {}", dto)

                            // ⏱ Simula tempo de processamento
                            Thread.sleep(processingDelay.toMillis())

                            val deleteRequest = DeleteMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .receiptHandle(msg.receiptHandle())
                                .build()
                            sqsClient.deleteMessage(deleteRequest)

                            log.info("Mensagem processada e removida com sucesso.")
                        } catch (e: Exception) {
                            log.error("Erro ao processar mensagem: ${e.message}", e)
                        }
                    }
                }
            }
        }
    }
}
