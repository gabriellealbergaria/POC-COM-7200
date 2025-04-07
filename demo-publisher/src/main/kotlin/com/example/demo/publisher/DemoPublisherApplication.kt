package com.example.demo.publisher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoPublisherApplication

fun main(args: Array<String>) {
    runApplication<DemoPublisherApplication>(*args)
}
