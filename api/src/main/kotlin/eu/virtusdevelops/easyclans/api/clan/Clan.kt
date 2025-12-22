package eu.virtusdevelops.easyclans.api.clan

import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.economy.Economy
import eu.virtusdevelops.easyclans.api.invite.ClanInvite
import eu.virtusdevelops.easyclans.api.item.ClanItem
import eu.virtusdevelops.easyclans.api.member.ClanMember
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import eu.virtusdevelops.easyclans.api.request.ClanRequest
import org.bukkit.inventory.ItemStack
import java.util.Date
import java.util.UUID
import java.util.concurrent.CompletionStage


interface Clan {

    val id: UUID
    val name: String
    val tag: String
    val created: Date

    suspend fun ownerAsync(): Result<ClanMember>
    fun owner(): CompletionStage<Result<ClanMember>>

    suspend fun membersAsync(): Set<ClanMember>
    fun members(): CompletionStage<Set<ClanMember>>

    suspend fun invitesAsync(): Set<ClanInvite>
    fun invites(): CompletionStage<Set<ClanInvite>>

    suspend fun requestsAsync(): Set<ClanRequest>
    fun requests(): CompletionStage<Set<ClanRequest>>

    suspend fun economiesAsync(): Set<Economy<Any>>
    fun economies(): CompletionStage<Set<Economy<Any>>>

    suspend fun clanSettingsAsync(): ClanSettings
    fun clanSettings(): CompletionStage<ClanSettings>

    suspend fun kickMemberAsync(clanMember: ClanMember): Result<Success>
    fun kickMember(clanMember: ClanMember): CompletionStage<Result<Success>>

    suspend fun addMemberAsync(clanPlayer: ClanPlayer): Result<ClanMember>
    fun addMember(clanPlayer: ClanPlayer): CompletionStage<Result<ClanMember>>

    suspend fun setTagAsync(tag: String): Result<Success>

    suspend fun setBannerAsync(item: ClanItem): Result<Success>
    suspend fun getBannerAsync(): Result<ClanItem>
}
