package com.example.demo.consumer.model

data class PublishRequestDTO(
    val message: String,
    val type: String? = null
)
