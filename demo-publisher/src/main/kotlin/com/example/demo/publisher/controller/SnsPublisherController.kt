package com.example.demo.publisher.controller

import com.example.demo.publisher.model.PublishRequestDTO
import com.example.demo.publisher.service.SnsPublisherService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sns")
@Tag(name = "SNS Publisher")
class SnsPublisherController(
    private val publisherService: SnsPublisherService
) {
    @PostMapping("/publish")
    @Operation(summary = "Publica uma mensagem no tópico SNS")
    fun publishMessage(@RequestBody dto: PublishRequestDTO): ResponseEntity<String> {
        val messageId = publisherService.publish(dto)
        return ResponseEntity.ok("Mensagem enviada com ID: $messageId")
    }
}