package io.abcdev

import io.abcdev.config.configureDatabases
import io.abcdev.config.configureMonitoring
import io.abcdev.config.configureSerialization
import io.abcdev.config.configureStatusPages
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureStatusPages()
    configureDatabases()
    configureRouting()
}

