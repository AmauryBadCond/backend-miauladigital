package io.abcdev

import io.abcdev.routes.apiRoutes
import io.abcdev.routes.healthRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        healthRoutes()
        apiRoutes()
    }
}

