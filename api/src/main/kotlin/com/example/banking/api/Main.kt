package com.example.banking.api
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
@SpringBootApplication(scanBasePackages = ["com.example.banking"])
class Main
fun main(args: Array<String>) { runApplication<Main>(*args) }
