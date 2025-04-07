package com.example.demo.publisher.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import java.net.URI

@Configuration
class AwsConfig {

    @Value("\${aws.region}")
    private lateinit var awsRegion: String

    @Value("\${aws.endpoint}")
    private lateinit var awsEndpoint: String

    @Value("\${aws.credentials.access-key}")
    private lateinit var accessKey: String

    @Value("\${aws.credentials.secret-key}")
    private lateinit var secretKey: String

    @Bean
    fun snsClient(): SnsClient = SnsClient.builder()
        .endpointOverride(URI.create(awsEndpoint))
        .region(Region.of(awsRegion))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build()
}
