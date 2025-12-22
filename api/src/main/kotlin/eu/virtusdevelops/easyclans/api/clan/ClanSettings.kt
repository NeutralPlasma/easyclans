package eu.virtusdevelops.easyclans.api.clan

import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.item.ClanItem

interface ClanSettings {

    val banner : ClanItem
    val pvp : Boolean
    val invites : Boolean
    val requests : Boolean


    fun pvp(boolean: Boolean): Result<Success>

    fun toggleInvites(boolean: Boolean): Result<Success>

    fun toggleRequests(boolean: Boolean): Result<Success>

    fun banner(itemStack: ClanItem): Result<Success>
}