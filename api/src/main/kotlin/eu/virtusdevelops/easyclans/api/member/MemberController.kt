package eu.virtusdevelops.easyclans.api.member

import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import java.util.concurrent.CompletionStage

interface MemberController {

    suspend fun getAsync(clanPlayer: ClanPlayer): Result<ClanMember>
    fun get(clanPlayer: ClanPlayer): CompletionStage<ClanMember?>


    suspend fun getClanMembersAsync(clan: Clan): Result<Set<ClanMember>>
    fun getClanMembers(clan: Clan): CompletionStage<Set<ClanMember>>
}