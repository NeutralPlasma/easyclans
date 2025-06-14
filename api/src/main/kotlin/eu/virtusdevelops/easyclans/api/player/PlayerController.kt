package eu.virtusdevelops.easyclans.api.player

import eu.virtusdevelops.easyclans.api.Success
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.CompletionStage

interface PlayerController {

    fun create(player: Player): Result<ClanPlayer>

    suspend fun getAsync(uuid: UUID) : Result<ClanPlayer>
    fun get(uuid: UUID): CompletionStage<Result<ClanPlayer>>

    suspend fun getAsync(player: Player): Result<ClanPlayer>
    fun get(player: Player): CompletionStage<Result<ClanPlayer>>

    suspend fun deleteAsync(player: Player): Result<Success>
    fun delete(player: Player): CompletionStage<Result<Success>>



    suspend fun saveAsync(clanPlayer: ClanPlayer): Result<Success>
    fun save(clanPlayer: ClanPlayer): CompletionStage<Result<Success>>
}