package io.abcdev.repository

import io.abcdev.db.TasksTable
import io.abcdev.db.dbQuery
import io.abcdev.models.TaskRequest
import io.abcdev.models.TaskResponse
import io.abcdev.models.toApiString
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.OffsetDateTime
import java.util.UUID

class TaskRepository {
    suspend fun getAll(): List<TaskResponse> = dbQuery {
        TasksTable.selectAll().map(::toResponse)
    }

    suspend fun getById(id: UUID): TaskResponse? = dbQuery {
        TasksTable.selectAll().where { TasksTable.id eq id }.singleOrNull()?.let(::toResponse)
    }

    suspend fun create(request: TaskRequest): TaskResponse = dbQuery {
        val id = TasksTable.insert {
            it[classroomId] = UUID.fromString(request.classroomId)
            it[title] = request.title
            it[description] = request.description
            it[dueDate] = request.dueDate?.let(OffsetDateTime::parse)
        }[TasksTable.id]

        TasksTable.selectAll().where { TasksTable.id eq id }.single().let(::toResponse)
    }

    suspend fun update(id: UUID, request: TaskRequest): TaskResponse? = dbQuery {
        val updatedRows = TasksTable.update({ TasksTable.id eq id }) {
            it[classroomId] = UUID.fromString(request.classroomId)
            it[title] = request.title
            it[description] = request.description
            it[dueDate] = request.dueDate?.let(OffsetDateTime::parse)
        }

        if (updatedRows == 0) return@dbQuery null
        TasksTable.selectAll().where { TasksTable.id eq id }.single().let(::toResponse)
    }

    suspend fun delete(id: UUID): Boolean = dbQuery {
        TasksTable.deleteWhere { TasksTable.id eq id } > 0
    }

    private fun toResponse(row: ResultRow): TaskResponse =
        TaskResponse(
            id = row[TasksTable.id].toString(),
            classroomId = row[TasksTable.classroomId].toString(),
            title = row[TasksTable.title],
            description = row[TasksTable.description],
            dueDate = row[TasksTable.dueDate]?.toInstant()?.toString(),
            createdAt = row[TasksTable.createdAt].toInstant().toApiString()
        )
}

