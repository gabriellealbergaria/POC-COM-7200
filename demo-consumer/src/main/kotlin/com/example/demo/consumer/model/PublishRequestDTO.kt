package com.example.demo.consumer.model

import java.time.Instant
import java.util.UUID

data class PublishRequestDTO(
    val uuid: UUID? = null,
    val message: String,
    val type: String? = null,
    val inputTimestamp: Instant? = null,
    var outputTimestamp: Instant? = null
)
