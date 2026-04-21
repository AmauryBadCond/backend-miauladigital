package io.abcdev.repository

import io.abcdev.db.StudentParentsTable
import io.abcdev.db.dbQuery
import io.abcdev.models.StudentParentRequest
import io.abcdev.models.StudentParentResponse
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

class StudentParentRepository {
    suspend fun getAll(): List<StudentParentResponse> = dbQuery {
        StudentParentsTable.selectAll().map(::toResponse)
    }

    suspend fun create(request: StudentParentRequest): StudentParentResponse = dbQuery {
        StudentParentsTable.insert {
            it[studentId] = UUID.fromString(request.studentId)
            it[parentId] = UUID.fromString(request.parentId)
        }
        StudentParentResponse(request.studentId, request.parentId)
    }

    suspend fun getParentsByStudent(studentId: UUID): List<StudentParentResponse> = dbQuery {
        StudentParentsTable.selectAll().where { StudentParentsTable.studentId eq studentId }.map(::toResponse)
    }

    suspend fun getStudentsByParent(parentId: UUID): List<StudentParentResponse> = dbQuery {
        StudentParentsTable.selectAll().where { StudentParentsTable.parentId eq parentId }.map(::toResponse)
    }

    suspend fun delete(studentId: UUID, parentId: UUID): Boolean = dbQuery {
        StudentParentsTable.deleteWhere {
            (StudentParentsTable.studentId eq studentId) and (StudentParentsTable.parentId eq parentId)
        } > 0
    }

    private fun toResponse(row: ResultRow): StudentParentResponse =
        StudentParentResponse(
            studentId = row[StudentParentsTable.studentId].toString(),
            parentId = row[StudentParentsTable.parentId].toString()
        )
}

