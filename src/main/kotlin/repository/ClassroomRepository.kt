package io.abcdev.repository

import io.abcdev.db.ClassroomsTable
import io.abcdev.db.TasksTable
import io.abcdev.db.dbQuery
import io.abcdev.models.ClassroomRequest
import io.abcdev.models.ClassroomResponse
import io.abcdev.models.TaskResponse
import io.abcdev.models.toApiString
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.UUID

class ClassroomRepository {
    suspend fun getAll(): List<ClassroomResponse> = dbQuery {
        ClassroomsTable.selectAll().map(::toResponse)
    }

    suspend fun getById(id: UUID): ClassroomResponse? = dbQuery {
        ClassroomsTable.selectAll().where { ClassroomsTable.id eq id }.singleOrNull()?.let(::toResponse)
    }

    suspend fun create(request: ClassroomRequest): ClassroomResponse = dbQuery {
        val id = ClassroomsTable.insert {
            it[name] = request.name
            it[teacherId] = request.teacherId?.let(UUID::fromString)
            it[schedules] = request.schedules
        }[ClassroomsTable.id]

        ClassroomsTable.selectAll().where { ClassroomsTable.id eq id }.single().let(::toResponse)
    }

    suspend fun update(id: UUID, request: ClassroomRequest): ClassroomResponse? = dbQuery {
        val updatedRows = ClassroomsTable.update({ ClassroomsTable.id eq id }) {
            it[name] = request.name
            it[teacherId] = request.teacherId?.let(UUID::fromString)
            it[schedules] = request.schedules
        }

        if (updatedRows == 0) return@dbQuery null
        ClassroomsTable.selectAll().where { ClassroomsTable.id eq id }.single().let(::toResponse)
    }

    suspend fun delete(id: UUID): Boolean = dbQuery {
        ClassroomsTable.deleteWhere { ClassroomsTable.id eq id } > 0
    }

    suspend fun getTasks(id: UUID): List<TaskResponse> = dbQuery {
        TasksTable.selectAll().where { TasksTable.classroomId eq id }.map(::taskToResponse)
    }

    private fun toResponse(row: ResultRow): ClassroomResponse =
        ClassroomResponse(
            id = row[ClassroomsTable.id].toString(),
            name = row[ClassroomsTable.name],
            teacherId = row[ClassroomsTable.teacherId]?.toString(),
            schedules = row[ClassroomsTable.schedules],
            createdAt = row[ClassroomsTable.createdAt].toInstant().toApiString()
        )

    private fun taskToResponse(row: ResultRow): TaskResponse =
        TaskResponse(
            id = row[TasksTable.id].toString(),
            classroomId = row[TasksTable.classroomId].toString(),
            title = row[TasksTable.title],
            description = row[TasksTable.description],
            dueDate = row[TasksTable.dueDate]?.toInstant()?.toString(),
            createdAt = row[TasksTable.createdAt].toInstant().toApiString()
        )
}

