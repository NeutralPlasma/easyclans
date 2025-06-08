package eu.virtusdevelops.easyclans.api.member

import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import java.util.concurrent.CompletionStage

interface MemberController {

    fun get(clanPlayer: ClanPlayer): CompletionStage<ClanMember?>
    suspend fun getAsync(clanPlayer: ClanPlayer): ClanMember?


    suspend fun getClanMembersAsync(clan: Clan): Set<ClanMember>
    fun getClanMembers(clan: Clan): CompletionStage<Set<ClanMember>>
}