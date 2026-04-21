package io.abcdev.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import java.time.Instant

@Serializable
data class ApiError(val message: String)

@Serializable
enum class UserRole {
    @SerialName("STUDENT")
    STUDENT,

    @SerialName("TEACHER")
    TEACHER,

    @SerialName("PARENT")
    PARENT
}

@Serializable
data class UserRequest(
    val firstName: String,
    val lastNames: String,
    val email: String,
    val phoneNumber: String? = null,
    val gender: String? = null,
    val avatarUrl: String? = null,
    val role: UserRole
)

@Serializable
data class UserResponse(
    val id: String,
    val firstName: String,
    val lastNames: String,
    val email: String,
    val phoneNumber: String? = null,
    val gender: String? = null,
    val avatarUrl: String? = null,
    val role: UserRole,
    val createdAt: String
)

@Serializable
data class ClassroomRequest(
    val name: String,
    val teacherId: String? = null,
    val schedules: JsonObject? = null
)

@Serializable
data class ClassroomResponse(
    val id: String,
    val name: String,
    val teacherId: String? = null,
    val schedules: JsonObject? = null,
    val createdAt: String
)

@Serializable
data class StudentParentRequest(
    val studentId: String,
    val parentId: String
)

@Serializable
data class StudentParentResponse(
    val studentId: String,
    val parentId: String
)

@Serializable
data class TaskRequest(
    val classroomId: String,
    val title: String,
    val description: String? = null,
    val dueDate: String? = null
)

@Serializable
data class TaskResponse(
    val id: String,
    val classroomId: String,
    val title: String,
    val description: String? = null,
    val dueDate: String? = null,
    val createdAt: String
)

fun Instant.toApiString(): String = toString()

