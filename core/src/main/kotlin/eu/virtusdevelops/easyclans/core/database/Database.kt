package eu.virtusdevelops.easyclans.core.database

import com.zaxxer.hikari.HikariDataSource
import eu.virtusdevelops.easyclans.api.invite.ClanInvite
import eu.virtusdevelops.easyclans.api.request.ClanRequest
import eu.virtusdevelops.easyclans.core.clan.ClanDao
import eu.virtusdevelops.easyclans.core.clan.ClanDaoMysqlImpl
import eu.virtusdevelops.easyclans.core.clan.settings.ClanSettingsDao
import eu.virtusdevelops.easyclans.core.economy.EconomyDao
import eu.virtusdevelops.easyclans.core.invite.InviteDao
import eu.virtusdevelops.easyclans.core.member.MemberDao
import eu.virtusdevelops.easyclans.core.request.RequestDao
import org.bukkit.plugin.java.JavaPlugin

class Database(
    private val plugin: JavaPlugin
) {

    companion object {
        @JvmStatic
        lateinit var instance: Database

        fun init(instance: Database) {
            this.instance = instance
        }

        fun get(): Database = instance
    }


    private lateinit var clanDao: ClanDao

    private lateinit var dataSource: HikariDataSource


    fun init(){
        // load configuration watever shit
        setupDataSource()
        clanDao = ClanDaoMysqlImpl(dataSource)
        clanDao.init()
        Database.init(this)
    }



    private fun setupDataSource() {

    }

    fun clanDao(): ClanDao = clanDao

    fun clanSettingsDao(): ClanSettingsDao = TODO("Not yet implemented")

    fun memberDao(): MemberDao = TODO("Not yet implemented")

    fun clanInviteDao(): InviteDao = TODO("Not yet implemented")

    fun clanRequestDao(): RequestDao = TODO("Not yet implemented")

    fun economyDao(): EconomyDao = TODO("Not yet implemented")
}