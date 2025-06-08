package eu.virtusdevelops.easyclans.api.player

import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.CompletionStage

interface PlayerController {

    fun create(player: Player): ClanPlayer?

    suspend fun getAsync(uuid: UUID) : ClanPlayer?
    fun get(uuid: UUID): CompletionStage<ClanPlayer?>

    suspend fun getAsync(player: Player): ClanPlayer?
    fun get(player: Player): CompletionStage<ClanPlayer?>

    suspend fun deleteAsync(player: Player): Boolean
    fun delete(player: Player): CompletionStage<Boolean>



    suspend fun saveAsync(clanPlayer: ClanPlayer): Boolean
    fun save(clanPlayer: ClanPlayer): CompletionStage<Boolean>
}