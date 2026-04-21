package io.abcdev.repository

import io.abcdev.db.UsersTable
import io.abcdev.db.dbQuery
import io.abcdev.models.UserRequest
import io.abcdev.models.UserResponse
import io.abcdev.models.UserRole
import io.abcdev.models.toApiString
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.UUID

class UserRepository {
    suspend fun getAll(): List<UserResponse> = dbQuery {
        UsersTable.selectAll().map(::toResponse)
    }

    suspend fun getById(id: UUID): UserResponse? = dbQuery {
        UsersTable.selectAll().where { UsersTable.id eq id }.singleOrNull()?.let(::toResponse)
    }

    suspend fun create(request: UserRequest): UserResponse = dbQuery {
        val id = UsersTable.insert {
            it[firstName] = request.firstName
            it[lastNames] = request.lastNames
            it[email] = request.email
            it[phoneNumber] = request.phoneNumber
            it[gender] = request.gender
            it[avatarUrl] = request.avatarUrl
            it[role] = request.role.name
        }[UsersTable.id]

        UsersTable.selectAll().where { UsersTable.id eq id }.single().let(::toResponse)
    }

    suspend fun update(id: UUID, request: UserRequest): UserResponse? = dbQuery {
        val updatedRows = UsersTable.update({ UsersTable.id eq id }) {
            it[firstName] = request.firstName
            it[lastNames] = request.lastNames
            it[email] = request.email
            it[phoneNumber] = request.phoneNumber
            it[gender] = request.gender
            it[avatarUrl] = request.avatarUrl
            it[role] = request.role.name
        }

        if (updatedRows == 0) return@dbQuery null
        UsersTable.selectAll().where { UsersTable.id eq id }.single().let(::toResponse)
    }

    suspend fun delete(id: UUID): Boolean = dbQuery {
        UsersTable.deleteWhere { UsersTable.id eq id } > 0
    }

    private fun toResponse(row: ResultRow): UserResponse =
        UserResponse(
            id = row[UsersTable.id].toString(),
            firstName = row[UsersTable.firstName],
            lastNames = row[UsersTable.lastNames],
            email = row[UsersTable.email],
            phoneNumber = row[UsersTable.phoneNumber],
            gender = row[UsersTable.gender],
            avatarUrl = row[UsersTable.avatarUrl],
            role = UserRole.valueOf(row[UsersTable.role]),
            createdAt = row[UsersTable.createdAt].toInstant().toApiString()
        )
}

