package eu.virtusdevelops.easyclans.api.player

import eu.virtusdevelops.easyclans.api.Success
import java.util.UUID
import java.util.concurrent.CompletionStage

interface PlayerController {

    fun create(player: UUID): Result<ClanPlayer>

    suspend fun getAsync(uuid: UUID) : Result<ClanPlayer>
    fun get(uuid: UUID): CompletionStage<Result<ClanPlayer>>

    suspend fun deleteAsync(player: UUID): Result<Success>
    fun delete(player: UUID): CompletionStage<Result<Success>>

    suspend fun saveAsync(clanPlayer: ClanPlayer): Result<Success>
    fun save(clanPlayer: ClanPlayer): CompletionStage<Result<Success>>
}