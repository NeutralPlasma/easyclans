package eu.virtusdevelops.easyclans.api.invite

import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import java.util.concurrent.CompletionStage

interface InviteController {

    fun invitePlayer(target: ClanPlayer, sender: ClanPlayer, clan: Clan): ClanInvite?


    suspend fun getInvitesBySenderAsync(sender: ClanPlayer, expired: Boolean): Set<ClanInvite>
    fun getInvitesBySender(sender: ClanPlayer, expired: Boolean): CompletionStage<Set<ClanInvite>>

    suspend fun getInvitesByTargetAsync(target: ClanPlayer, expired: Boolean): Set<ClanInvite>
    fun getInvitesByTarget(target: ClanPlayer, expired: Boolean): CompletionStage<Set<ClanInvite>>

    suspend fun getInvitesByClanAsync(clan: Clan, expired: Boolean): Set<ClanInvite>
    fun getInvitesByClan(clan: Clan, expired: Boolean): CompletionStage<Set<ClanInvite>>
}