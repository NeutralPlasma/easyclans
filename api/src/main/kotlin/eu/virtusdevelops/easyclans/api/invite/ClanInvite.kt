package eu.virtusdevelops.easyclans.api.invite

import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import java.util.Date

interface ClanInvite {

    fun clan(): Clan

    fun sender(): ClanPlayer

    fun target(): ClanPlayer

    fun sentDate(): Date

    fun expireDate(): Date

    fun isExpired(): Boolean

    fun isCancelled(): Boolean

}