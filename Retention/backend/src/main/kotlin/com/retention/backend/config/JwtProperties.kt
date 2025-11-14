package com.retention.backend.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtProperties {
    lateinit var secret : String
    var expirationMs : Long = 8600000
}