package com.example.banking.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = ["com.example.banking"],
    exclude = [UserDetailsServiceAutoConfiguration::class],
)
class Main

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}
