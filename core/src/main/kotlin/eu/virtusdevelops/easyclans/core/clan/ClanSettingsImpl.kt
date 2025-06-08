package eu.virtusdevelops.easyclans.core.clan

import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.clan.ClanSettings
import org.bukkit.inventory.ItemStack

class ClanSettingsImpl : ClanSettings {


    override fun pvpEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun invitesEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun requestsEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun banner(): ItemStack {
        TODO("Not yet implemented")
    }

    override fun pvp(boolean: Boolean): Result<Success> {
        TODO("Not yet implemented")
    }

    override fun toggleInvites(boolean: Boolean): Result<Success> {
        TODO("Not yet implemented")
    }

    override fun toggleRequests(boolean: Boolean): Result<Success> {
        TODO("Not yet implemented")
    }

}