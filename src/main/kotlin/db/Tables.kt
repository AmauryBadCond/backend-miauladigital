package io.abcdev.db

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObject.Companion.serializer
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone
import org.jetbrains.exposed.sql.json.jsonb
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

object UsersTable : Table("users") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val firstName = varchar("first_name", 50)
    val lastNames = varchar("last_names", 100)
    val email = varchar("email", 100).uniqueIndex()
    val phoneNumber = varchar("phone_number", 20).nullable()
    val gender = varchar("gender", 20).nullable()
    val avatarUrl = text("avatar_url").nullable()
    val role = varchar("role", 20)
    val createdAt = timestampWithTimeZone("created_at").clientDefault { OffsetDateTime.now(ZoneOffset.UTC) }

    override val primaryKey = PrimaryKey(id)
}

object ClassroomsTable : Table("classrooms") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val name = varchar("name", 100)
    val teacherId = uuid("teacher_id").references(UsersTable.id).nullable()
    val schedules = jsonb("schedules", Json, serializer()).nullable()
    val createdAt = timestampWithTimeZone("created_at").clientDefault { OffsetDateTime.now(ZoneOffset.UTC) }

    override val primaryKey = PrimaryKey(id)
}

object StudentParentsTable : Table("student_parents") {
    val studentId = uuid("student_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val parentId = uuid("parent_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(studentId, parentId)
}

object TasksTable : Table("tasks") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val classroomId = uuid("classroom_id").references(ClassroomsTable.id, onDelete = ReferenceOption.CASCADE)
    val title = text("title")
    val description = text("description").nullable()
    val dueDate = timestampWithTimeZone("due_date").nullable()
    val createdAt = timestampWithTimeZone("created_at").clientDefault { OffsetDateTime.now(ZoneOffset.UTC) }

    override val primaryKey = PrimaryKey(id)
}

