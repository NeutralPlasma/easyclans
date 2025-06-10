package eu.virtusdevelops.easyclans.core.database

import com.zaxxer.hikari.HikariDataSource
import eu.virtusdevelops.easyclans.core.clan.ClanDao
import eu.virtusdevelops.easyclans.core.clan.ClanDaoMysqlImpl
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


}