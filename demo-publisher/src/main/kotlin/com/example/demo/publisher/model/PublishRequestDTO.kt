package com.example.demo.publisher.model

import java.time.Instant
import java.util.*

data class PublishRequestDTO(
    val uuid: UUID? = null,
    val cenario: String? = null,
    val contador: Long? = null,
    val type: String? = null,
    val inputTimestamp: Instant = Instant.now(), // timestamp de entrada no tópico
    val outputTimestamp: Instant? = null         // preenchido após processamento
)
