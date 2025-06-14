package eu.virtusdevelops.easyclans.api.member

import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.permission.ClanPermission
import eu.virtusdevelops.easyclans.api.permission.Permission
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import java.util.Date
import java.util.concurrent.CompletionStage

interface ClanMember {

    suspend fun clanAsync(): Clan
    fun clan(): CompletionStage<Clan>

    suspend fun playerAsync(): ClanPlayer
    fun player(): CompletionStage<ClanPlayer>

    fun permissions(): Set<ClanPermission>

    fun joinDate(): Date

    fun clanChat(): Boolean

    fun setClanChat(clanChat: Boolean)

    fun isActive(): Boolean

    fun interestRate(): Double


    // permissions

    fun has(permission: Permission): Boolean

    suspend fun addAsync(permission: ClanPermission): Result<Success>
    fun add(permission: ClanPermission): CompletionStage<Result<Success>>

    suspend fun removeAsync(permission: Permission): Result<Success>
    fun remove(permission: Permission): CompletionStage<Result<Success>>
}