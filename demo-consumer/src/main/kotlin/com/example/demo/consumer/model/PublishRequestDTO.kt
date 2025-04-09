package com.example.demo.consumer.model

import java.time.Duration
import java.time.Instant
import java.util.*

data class PublishRequestDTO(
    val uuid: UUID? = null,
    val cenario: String? = null,
    val contador: Long? = null,
    val type: String? = null,
    val inputTimestamp: Instant? = null,
    var outputTimestamp: Instant? = null
) {
    val durationInQueueSeconds: Long?
        get() = if (inputTimestamp != null && outputTimestamp != null) {
            Duration.between(inputTimestamp, outputTimestamp).toSeconds()
        } else {
            null
        }
}
