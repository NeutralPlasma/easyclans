package eu.virtusdevelops.easyclans.api.request

import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import java.util.Date

interface ClanRequest {

    fun clan(): Clan

    fun sender(): ClanPlayer

    fun sentDate(): Date

    fun expireDate(): Date

    fun isExpired(): Boolean

    fun isCancelled(): Boolean


    fun accept(): Result<Success>

    fun decline()
}