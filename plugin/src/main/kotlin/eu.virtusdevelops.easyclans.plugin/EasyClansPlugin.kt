package eu.virtusdevelops.easyclans.plugin

import eu.virtusdevelops.easyclans.api.EasyClansAPI
import eu.virtusdevelops.easyclans.core.EasyClansAPIImpl
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

class EasyClansPlugin : JavaPlugin() {

    private lateinit var api: EasyClansAPIImpl

    override fun onLoad() {
        api = EasyClansAPIImpl(this)

        server.servicesManager.register(
            EasyClansAPI::class.java,
            api,
            this,
            ServicePriority.Highest
        )


        logger.info("EasyClansPlugin loaded")
    }




    override fun onEnable() {
        // enable the listeners and run the controllers
    }

    override fun onDisable() {

    }



}