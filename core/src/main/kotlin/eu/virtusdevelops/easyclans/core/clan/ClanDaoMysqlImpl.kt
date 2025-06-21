package eu.virtusdevelops.easyclans.core.clan

import com.sun.net.httpserver.Authenticator
import com.zaxxer.hikari.HikariDataSource
import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.clan.Clan
import java.util.UUID
import java.util.logging.Logger

class ClanDaoMysqlImpl(
    private val dataSource: HikariDataSource,
    private val logger: Logger) : ClanDao {

    override suspend fun init(): Result<Success> {
        return try {
            dataSource.connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.execute("""
                    CREATE TABLE IF NOT EXISTS clan (
                        id UUID NOT NULL PRIMARY KEY,
                        name VARCHAR(128) NOT NULL,
                        tag VARCHAR(16) NOT NULL,
                        owner UUID NOT NULL,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (owner) REFERENCES player(id) ON DELETE CASCADE
                    );
                """.trimIndent())
                }
            }
            logger.info("Clan table initialized successfully.")
            Result.success(Success)
        } catch (e: Exception) {
            logger.severe("Failed to initialize clan table: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getById(id: UUID): Result<Clan> {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(): Result<List<Clan>> {
        TODO("Not yet implemented")
    }

    override suspend fun save(t: Clan): Result<Success> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: UUID): Result<Success> {
        TODO("Not yet implemented")
    }

}