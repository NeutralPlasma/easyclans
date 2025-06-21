package eu.virtusdevelops.easyclans.api.clan

import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import java.util.UUID
import java.util.concurrent.CompletionStage

interface ClansController {

    suspend fun createClanAsync(clanName: String, tag: String, owner: ClanPlayer): Result<Clan>
    fun createClan(clanName: String, tag: String, owner: ClanPlayer): CompletionStage<Result<Clan>>

    suspend fun getClanAsync(id: UUID): Clan?
    fun getClan(id: UUID): CompletionStage<Clan?>

    suspend fun deleteClanAsync(clan: Clan): Result<Success>
    fun deleteClan(clan: Clan): CompletionStage<Result<Success>>

}