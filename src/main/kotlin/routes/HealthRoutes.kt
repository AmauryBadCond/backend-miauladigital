package io.abcdev.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.healthRoutes() {
    get("/") {
        call.respond(
            HttpStatusCode.OK,
            mapOf(
                "service" to "MiAulaDigital API",
                "status" to "running"
            )
        )
    }

    get("/health") {
        call.respond(HttpStatusCode.OK, mapOf("status" to "ok"))
    }
}

