package com.example.demo.publisher.model

import java.time.Instant
import java.util.*

data class PublishRequestDTO(
    val uuid: UUID? = null,
    val message: String,
    val type: String? = null, // usado como message attribute para filtros SNS
    val inputTimestamp: Instant = Instant.now(), // timestamp de entrada no tópico
    val outputTimestamp: Instant? = null         // preenchido após processamento
)
