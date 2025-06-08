package eu.virtusdevelops.easyclans.api

import eu.virtusdevelops.easyclans.api.clan.ClansController
import eu.virtusdevelops.easyclans.api.economy.EconomyController
import eu.virtusdevelops.easyclans.api.invite.InviteController
import eu.virtusdevelops.easyclans.api.member.MemberController
import eu.virtusdevelops.easyclans.api.player.PlayerController
import eu.virtusdevelops.easyclans.api.request.RequestController
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.ApiStatus

interface EasyClansAPI {

    companion object {
        private var implementation: EasyClansAPI? = null
        private var enabled = false


        fun get(): EasyClansAPI {
            if(!enabled || implementation == null) {
                throw IllegalStateException("EasyClansAPI is not enabled")
            }
            return implementation!!
        }


        @ApiStatus.Internal
        fun load(api: EasyClansAPI) {
            if(enabled) {
                throw IllegalStateException("EasyClansAPI is already enabled")
            }
            implementation = api
            enabled = true
        }

        @ApiStatus.Internal
        fun unload() {
            if(!enabled) {
                throw IllegalStateException("EasyClansAPI is not enabled")
            }
            enabled = false
            implementation = null
        }

    }


    fun plugin(): JavaPlugin

    fun clanController(): ClansController

    fun economyController(): EconomyController

    fun inviteController(): InviteController

    fun membersController(): MemberController

    fun playerController(): PlayerController

    fun requestController(): RequestController

}