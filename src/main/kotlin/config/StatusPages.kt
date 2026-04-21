package io.abcdev.config

import io.abcdev.models.ApiError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    val logger = environment.log

    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ApiError(cause.message ?: "Solicitud invalida"))
        }
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ApiError(cause.message ?: "Recurso no encontrado"))
        }
        exception<Throwable> { call, cause ->
            logger.error("Unhandled error", cause)
            call.respond(HttpStatusCode.InternalServerError, ApiError("Error interno del servidor"))
        }
    }
}

