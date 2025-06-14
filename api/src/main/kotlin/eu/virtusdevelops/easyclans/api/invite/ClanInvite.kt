package eu.virtusdevelops.easyclans.api.invite

import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import java.util.Date
import java.util.concurrent.CompletionStage

interface ClanInvite {

    suspend fun clanAsync(): Clan
    fun clan(): CompletionStage<Clan>

    suspend fun senderAsync(): ClanPlayer
    fun sender(): CompletionStage<ClanPlayer>

    suspend fun targetAsync(): ClanPlayer
    fun target(): CompletionStage<ClanPlayer>


    fun sentDate(): Date

    fun expireDate(): Date

    fun isExpired(): Boolean

    fun isCancelled(): Boolean



    suspend fun declineAsync()
    fun decline(): CompletionStage<Void>

    suspend fun acceptAsync(): Result<Success>
    fun accept(): CompletionStage<Result<Success>>
}