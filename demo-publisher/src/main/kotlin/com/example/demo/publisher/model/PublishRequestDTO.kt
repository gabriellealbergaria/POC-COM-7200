package com.example.demo.publisher.model

data class PublishRequestDTO(
    val message: String,
    val type: String? = null // usado como message attribute para filtros SNS
)
