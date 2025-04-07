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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Component
class Queue2Consumer(
    private val sqsClient: SqsClient,
    private val objectMapper: ObjectMapper,
    @Value("\${aws.sqs.queue-url-2}") private val queueUrl: String,
    @Value("\${aws.sqs.consumer-2-enabled}") private val enabled: Boolean,
    @Value("\${aws.sqs.consumer-2-parallel}") private val parallelProcessing: Boolean,
    @Value("\${aws.sqs.consumer-2-threads}") private val threadCount: Int
) {

    private val pollingExecutor = Executors.newSingleThreadExecutor()
    private val processingExecutor: ExecutorService =
        if (parallelProcessing) Executors.newFixedThreadPool(threadCount)
        else Executors.newSingleThreadExecutor()

    private val log = LoggerFactory.getLogger(Queue2Consumer::class.java)

    @PostConstruct
    fun start() {
        if (!enabled) {
            log.info("Queue2Consumer desativado por variável de ambiente.")
            return
        }

        log.info("Queue2Consumer iniciado. Paralelismo: {}, Threads: {}", parallelProcessing, threadCount)

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

                            // TODO: lógica de negócio aqui

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
