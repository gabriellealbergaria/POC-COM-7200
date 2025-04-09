package com.example.demo.consumer.config

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ElasticsearchConfig {

    @Bean
    fun elasticsearchClient(
        objectMapper: ObjectMapper,
        @Value("\${elasticsearch.host}") host: String,
        @Value("\${elasticsearch.port}") port: Int,
        @Value("\${elasticsearch.scheme}") scheme: String
    ): ElasticsearchClient {
        val restClient = RestClient.builder(HttpHost(host, port, scheme)).build()
        val transport = RestClientTransport(restClient, JacksonJsonpMapper(objectMapper))
        return ElasticsearchClient(transport)
    }
}
