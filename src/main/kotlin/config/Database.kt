package io.abcdev.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.abcdev.db.ClassroomsTable
import io.abcdev.db.StudentParentsTable
import io.abcdev.db.TasksTable
import io.abcdev.db.UsersTable
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun Application.configureDatabases() {
    val dbConfig = environment.config.config("database")
    val autoCreateSchema = dbConfig.property("autoCreateSchema").getString().toBoolean()
    val databaseSettings = resolveDatabaseSettings(dbConfig)

    val hikariConfig = HikariConfig().apply {
        jdbcUrl = databaseSettings.jdbcUrl
        driverClassName = dbConfig.property("driverClassName").getString()
        username = databaseSettings.username
        password = databaseSettings.password
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

private data class DatabaseSettings(
    val jdbcUrl: String,
    val username: String,
    val password: String
)

private fun Application.resolveDatabaseSettings(dbConfig: ApplicationConfig): DatabaseSettings {
    val env = System.getenv()

    val explicitJdbcUrl = env["DATABASE_URL"]?.takeIf { it.isNotBlank() }
    val explicitUsername = env["DATABASE_USER"]?.takeIf { it.isNotBlank() }
    val explicitPassword = env["DATABASE_PASSWORD"]?.takeIf { it.isNotBlank() }

    if (explicitJdbcUrl != null) {
        return DatabaseSettings(
            jdbcUrl = explicitJdbcUrl,
            username = explicitUsername ?: dbConfig.property("username").getString(),
            password = explicitPassword ?: dbConfig.property("password").getString()
        )
    }

    val pgHost = env["PGHOST"]?.takeIf { it.isNotBlank() }
    val pgPort = env["PGPORT"]?.takeIf { it.isNotBlank() } ?: "5432"
    val pgDatabase = env["PGDATABASE"]?.takeIf { it.isNotBlank() }
    val pgUser = env["PGUSER"]?.takeIf { it.isNotBlank() }
    val pgPassword = env["PGPASSWORD"]?.takeIf { it.isNotBlank() }
    val sslMode = env["PGSSLMODE"]?.takeIf { it.isNotBlank() } ?: dbConfig.property("sslMode").getString()

    if (pgHost != null && pgDatabase != null && pgUser != null && pgPassword != null) {
        val encodedSslMode = URLEncoder.encode(sslMode, StandardCharsets.UTF_8)
        val jdbcUrl = "jdbc:postgresql://$pgHost:$pgPort/$pgDatabase?sslmode=$encodedSslMode"

        log.info("Using PostgreSQL settings from PGHOST/PGPORT/PGDATABASE/PGUSER environment variables")

        return DatabaseSettings(
            jdbcUrl = jdbcUrl,
            username = pgUser,
            password = pgPassword
        )
    }

    return DatabaseSettings(
        jdbcUrl = dbConfig.property("jdbcUrl").getString(),
        username = dbConfig.property("username").getString(),
        password = dbConfig.property("password").getString()
    )
}
