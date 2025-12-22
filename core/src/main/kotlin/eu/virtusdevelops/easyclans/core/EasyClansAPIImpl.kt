package eu.virtusdevelops.easyclans.core

import eu.virtusdevelops.easyclans.api.EasyClansAPI
import eu.virtusdevelops.easyclans.api.clan.ClansController
import eu.virtusdevelops.easyclans.api.economy.EconomyController
import eu.virtusdevelops.easyclans.api.invite.InviteController
import eu.virtusdevelops.easyclans.api.member.MemberController
import eu.virtusdevelops.easyclans.api.player.PlayerController
import eu.virtusdevelops.easyclans.api.request.RequestController
import org.bukkit.plugin.java.JavaPlugin

class EasyClansAPIImpl(private val plugin: JavaPlugin) : EasyClansAPI {

    init {
        EasyClansAPI.load(this)
    }

    fun plugin(): JavaPlugin {
        return plugin
    }


    override fun clanController(): ClansController {
        TODO("Not yet implemented")
    }

    override fun economyController(): EconomyController {
        TODO("Not yet implemented")
    }

    override fun inviteController(): InviteController {
        TODO("Not yet implemented")
    }

    override fun membersController(): MemberController {
        TODO("Not yet implemented")
    }

    override fun playerController(): PlayerController {
        TODO("Not yet implemented")
    }

    override fun requestController(): RequestController {
        TODO("Not yet implemented")
    }
}