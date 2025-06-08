package eu.virtusdevelops.easyclans.api.clan

import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import java.util.UUID
import java.util.concurrent.CompletionStage

interface ClansController {

    fun createClan(clanName: String, tag: String, owner: ClanPlayer): Clan


    suspend fun getClanAsync(id: UUID): Clan?
    fun getClan(id: UUID): CompletionStage<Clan?>


    suspend fun deleteClanAsync(clan: Clan): Result<Success>
    fun deleteClan(clan: Clan): CompletionStage<Result<Success>>

}