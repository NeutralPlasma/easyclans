package eu.virtusdevelops.easyclans.core.database

import com.zaxxer.hikari.HikariDataSource
import eu.virtusdevelops.easyclans.core.clan.ClanDao
import eu.virtusdevelops.easyclans.core.clan.ClanDaoMysqlImpl
import org.bukkit.plugin.java.JavaPlugin

class Database(
    private val plugin: JavaPlugin
) {

    private lateinit var clanDao: ClanDao

    private lateinit var dataSource: HikariDataSource


    fun init(){
        // load configuration watever shit
        setupDataSource()

        clanDao = ClanDaoMysqlImpl(dataSource)


        clanDao.init()
    }



    private fun setupDataSource() {

    }


}