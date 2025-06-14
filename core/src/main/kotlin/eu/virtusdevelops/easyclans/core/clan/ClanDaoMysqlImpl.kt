package eu.virtusdevelops.easyclans.core.clan

import com.zaxxer.hikari.HikariDataSource
import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.clan.Clan
import java.util.UUID

class ClanDaoMysqlImpl(dataSource: HikariDataSource) : ClanDao {

    override fun init(): Result<Success> {
        TODO("Not yet implemented")
    }

    override fun getById(id: UUID): Result<Clan> {
        TODO("Not yet implemented")
    }

    override fun getAll(): Result<List<Clan>> {
        TODO("Not yet implemented")
    }

    override fun save(t: Clan): Result<Success> {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: UUID): Result<Success> {
        TODO("Not yet implemented")
    }

    override fun getClanSettings(clan: Clan): Result<ClanSettingsImpl> {
        TODO("Not yet implemented")
    }
}