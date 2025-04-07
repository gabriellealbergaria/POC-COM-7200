package com.example.demo.publisher.service

import com.example.demo.publisher.model.PublishRequestDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.MessageAttributeValue
import software.amazon.awssdk.services.sns.model.PublishRequest

@Service
class SnsPublisherService(
    private val snsClient: SnsClient,
    private val objectMapper: ObjectMapper,
    @Value("\${aws.sns.topic-arn}") private val topicArn: String
) {
    fun publish(dto: PublishRequestDTO): String {
        val jsonMessage = objectMapper.writeValueAsString(dto)

        val requestBuilder = PublishRequest.builder()
            .message(jsonMessage)
            .topicArn(topicArn)

        dto.type?.let {
            requestBuilder.messageAttributes(
                mapOf(
                    "type" to MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(it)
                        .build()
                )
            )
        }

        val response = snsClient.publish(requestBuilder.build())
        return response.messageId()
    }
}
