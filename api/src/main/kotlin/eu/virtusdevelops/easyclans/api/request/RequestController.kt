package eu.virtusdevelops.easyclans.api.request

import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import java.util.concurrent.CompletionStage

interface RequestController {

    fun requestJoin(target: Clan, sender: ClanPlayer): ClanRequest?


    suspend fun getRequestsByTargetAsync(target: ClanPlayer, expired: Boolean): Set<ClanRequest>
    fun getRequestsBySender(sender: ClanPlayer, expired: Boolean): CompletionStage<Set<ClanRequest>>

    fun getRequestsBySenderAsync(sender: ClanPlayer, expired: Boolean): Set<ClanRequest>
    fun getRequestsByClan(clan: Clan, expired: Boolean): CompletionStage<Set<ClanRequest>>

}