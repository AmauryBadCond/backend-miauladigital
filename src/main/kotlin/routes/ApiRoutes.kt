package io.abcdev.routes

import io.abcdev.models.ClassroomRequest
import io.abcdev.models.StudentParentRequest
import io.abcdev.models.TaskRequest
import io.abcdev.models.UserRequest
import io.abcdev.repository.ClassroomRepository
import io.abcdev.repository.StudentParentRepository
import io.abcdev.repository.TaskRepository
import io.abcdev.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.OffsetDateTime
import java.util.UUID

private val userRepository = UserRepository()
private val classroomRepository = ClassroomRepository()
private val studentParentRepository = StudentParentRepository()
private val taskRepository = TaskRepository()

fun Route.apiRoutes() {
    route("/api") {
        route("/users") {
            get {
                call.respond(userRepository.getAll())
            }

            post {
                val request = call.receive<UserRequest>().validated()
                call.respond(HttpStatusCode.Created, userRepository.create(request))
            }

            get("{id}") {
                val user = userRepository.getById(call.uuidParameter("id"))
                    ?: throw NotFoundException("Usuario no encontrado")
                call.respond(user)
            }

            put("{id}") {
                val updated = userRepository.update(call.uuidParameter("id"), call.receive<UserRequest>().validated())
                    ?: throw NotFoundException("Usuario no encontrado")
                call.respond(updated)
            }

            delete("{id}") {
                if (!userRepository.delete(call.uuidParameter("id"))) {
                    throw NotFoundException("Usuario no encontrado")
                }
                call.respond(HttpStatusCode.NoContent)
            }
        }

        route("/classrooms") {
            get {
                call.respond(classroomRepository.getAll())
            }

            post {
                val request = call.receive<ClassroomRequest>().validated()
                call.respond(HttpStatusCode.Created, classroomRepository.create(request))
            }

            get("{id}") {
                val classroom = classroomRepository.getById(call.uuidParameter("id"))
                    ?: throw NotFoundException("Salon no encontrado")
                call.respond(classroom)
            }

            put("{id}") {
                val updated = classroomRepository.update(call.uuidParameter("id"), call.receive<ClassroomRequest>().validated())
                    ?: throw NotFoundException("Salon no encontrado")
                call.respond(updated)
            }

            delete("{id}") {
                if (!classroomRepository.delete(call.uuidParameter("id"))) {
                    throw NotFoundException("Salon no encontrado")
                }
                call.respond(HttpStatusCode.NoContent)
            }

            get("{id}/tasks") {
                call.respond(classroomRepository.getTasks(call.uuidParameter("id")))
            }
        }

        route("/student-parents") {
            get {
                call.respond(studentParentRepository.getAll())
            }

            post {
                val request = call.receive<StudentParentRequest>().validated()
                call.respond(HttpStatusCode.Created, studentParentRepository.create(request))
            }

            delete {
                val studentId = call.request.queryParameters["studentId"]?.let(UUID::fromString)
                    ?: throw IllegalArgumentException("studentId es requerido")
                val parentId = call.request.queryParameters["parentId"]?.let(UUID::fromString)
                    ?: throw IllegalArgumentException("parentId es requerido")

                if (!studentParentRepository.delete(studentId, parentId)) {
                    throw NotFoundException("Relacion no encontrada")
                }
                call.respond(HttpStatusCode.NoContent)
            }
        }

        get("/students/{studentId}/parents") {
            call.respond(studentParentRepository.getParentsByStudent(call.uuidParameter("studentId")))
        }

        get("/parents/{parentId}/students") {
            call.respond(studentParentRepository.getStudentsByParent(call.uuidParameter("parentId")))
        }

        route("/tasks") {
            get {
                call.respond(taskRepository.getAll())
            }

            post {
                val request = call.receive<TaskRequest>().validated()
                call.respond(HttpStatusCode.Created, taskRepository.create(request))
            }

            get("{id}") {
                val task = taskRepository.getById(call.uuidParameter("id"))
                    ?: throw NotFoundException("Tarea no encontrada")
                call.respond(task)
            }

            put("{id}") {
                val updated = taskRepository.update(call.uuidParameter("id"), call.receive<TaskRequest>().validated())
                    ?: throw NotFoundException("Tarea no encontrada")
                call.respond(updated)
            }

            delete("{id}") {
                if (!taskRepository.delete(call.uuidParameter("id"))) {
                    throw NotFoundException("Tarea no encontrada")
                }
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

private fun ApplicationCall.uuidParameter(name: String): UUID {
    val value = parameters[name] ?: throw IllegalArgumentException("$name es requerido")
    return try {
        UUID.fromString(value)
    } catch (_: IllegalArgumentException) {
        throw IllegalArgumentException("$name no es un UUID valido")
    }
}

private fun UserRequest.validated(): UserRequest {
    require(firstName.isNotBlank()) { "firstName es requerido" }
    require(lastNames.isNotBlank()) { "lastNames es requerido" }
    require(email.isNotBlank()) { "email es requerido" }
    return this
}

private fun ClassroomRequest.validated(): ClassroomRequest {
    require(name.isNotBlank()) { "name es requerido" }
    teacherId?.let(UUID::fromString)
    return this
}

private fun StudentParentRequest.validated(): StudentParentRequest {
    UUID.fromString(studentId)
    UUID.fromString(parentId)
    return this
}

private fun TaskRequest.validated(): TaskRequest {
    require(title.isNotBlank()) { "title es requerido" }
    UUID.fromString(classroomId)
    dueDate?.let(OffsetDateTime::parse)
    return this
}
