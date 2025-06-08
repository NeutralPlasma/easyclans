package eu.virtusdevelops.easyclans.api.clan

import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.economy.Economy
import eu.virtusdevelops.easyclans.api.invite.ClanInvite
import eu.virtusdevelops.easyclans.api.member.ClanMember
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import eu.virtusdevelops.easyclans.api.request.ClanRequest
import org.bukkit.inventory.ItemStack
import java.util.Date
import java.util.concurrent.CompletionStage


interface Clan {

    fun name(): String

    fun tag(): String

    suspend fun ownerAsync(): ClanMember
    fun owner(): CompletionStage<ClanMember>

    suspend fun membersAsync(): Set<ClanMember>
    fun members(): CompletionStage<Set<ClanMember>>

    suspend fun invitesAsync(): Set<ClanInvite>
    fun invites(): CompletionStage<Set<ClanInvite>>

    suspend fun requestsAsync(): Set<ClanRequest>
    fun requests(): CompletionStage<Set<ClanRequest>>

    fun economies(): Set<Economy<Any>>

    fun created(): Date

    suspend fun clanSettingsAsync(): ClanSettings
    fun clanSettings(): CompletionStage<ClanSettings>

    //
    suspend fun kickMember(clanMember: ClanMember): Result<Success>
    fun kickMember(clanMember: ClanMember, bypass: Boolean = false): CompletionStage<Result<Success>>

    suspend fun addMember(clanPlayer: ClanPlayer): ClanMember
    fun addMember(clanPlayer: ClanPlayer, bypass: Boolean = false): CompletionStage<ClanMember>


    fun setTag(tag: String): Result<Success>

    fun setBanner(itemStack: Any): Result<Success>

    fun getBanner(clanMember: ClanMember, bypass: Boolean = false): Result<ItemStack>
}