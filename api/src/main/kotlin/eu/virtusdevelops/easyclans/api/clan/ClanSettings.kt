package eu.virtusdevelops.easyclans.api.clan

import eu.virtusdevelops.easyclans.api.Success
import org.bukkit.inventory.ItemStack

interface ClanSettings {

    fun pvpEnabled(): Boolean

    fun invitesEnabled(): Boolean

    fun requestsEnabled(): Boolean

    fun banner(): ItemStack


    

    fun pvp(boolean: Boolean): Result<Success>

    fun toggleInvites(boolean: Boolean): Result<Success>

    fun toggleRequests(boolean: Boolean): Result<Success>

    fun banner(itemStack: ItemStack): Result<Success>
}