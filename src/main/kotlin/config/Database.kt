package io.abcdev.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.abcdev.db.ClassroomsTable
import io.abcdev.db.StudentParentsTable
import io.abcdev.db.TasksTable
import io.abcdev.db.UsersTable
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val dbConfig = environment.config.config("database")
    val autoCreateSchema = dbConfig.property("autoCreateSchema").getString().toBoolean()

    val hikariConfig = HikariConfig().apply {
        jdbcUrl = dbConfig.property("jdbcUrl").getString()
        driverClassName = dbConfig.property("driverClassName").getString()
        username = dbConfig.property("username").getString()
        password = dbConfig.property("password").getString()
        maximumPoolSize = dbConfig.property("maximumPoolSize").getString().toInt()
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    Database.connect(HikariDataSource(hikariConfig))

    if (autoCreateSchema) {
        @Suppress("DEPRECATION")
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                UsersTable,
                ClassroomsTable,
                StudentParentsTable,
                TasksTable
            )
        }
    }
}

