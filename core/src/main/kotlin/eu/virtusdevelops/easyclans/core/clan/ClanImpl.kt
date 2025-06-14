package eu.virtusdevelops.easyclans.core.clan

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.scope
import eu.virtusdevelops.easyclans.api.EasyClansAPI
import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.clan.ClanSettings
import eu.virtusdevelops.easyclans.api.economy.Economy
import eu.virtusdevelops.easyclans.api.invite.ClanInvite
import eu.virtusdevelops.easyclans.api.member.ClanMember
import eu.virtusdevelops.easyclans.api.member.MemberNotFound
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import eu.virtusdevelops.easyclans.api.request.ClanRequest
import eu.virtusdevelops.easyclans.core.ExpiringCache
import eu.virtusdevelops.easyclans.core.database.Database
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import org.bukkit.inventory.ItemStack
import java.util.Date
import java.util.UUID
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit

class ClanImpl(
    private val id: UUID,
    private var name: String,
    private var tag: String,
    private var owner: UUID,
    private var createDate: Date,
    private var clanSettings: ExpiringCache<ClanSettingsImpl>,

    private var members: ExpiringCache<Set<ClanMember>>,
    private var invites: ExpiringCache<Set<ClanInvite>>,
    private var requests: ExpiringCache<Set<ClanRequest>>,
    private var economies: ExpiringCache<Set<Economy<Any>>>
) : Clan {

    override fun id(): UUID {
        return id
    }

    private val api by lazy {
        EasyClansAPI.get()
    }

    private val database by lazy {
        Database.get()
    }

    override fun name(): String {
        return name
    }

    override fun tag(): String {
        return tag
    }

    override suspend fun ownerAsync(): Result<ClanMember> = withContext(api.plugin().asyncDispatcher) {
        val player = api.playerController().getAsync(owner)
        if(player.isFailure)
            return@withContext Result.failure(player.exceptionOrNull()!!)

        api.membersController().getAsync(player.getOrNull()!!)

    }

    override fun owner(): CompletionStage<Result<ClanMember>> {
        return api.plugin().scope.future {
            ownerAsync()
        }
    }

    override suspend fun membersAsync(): Set<ClanMember> {
        val cached = members.get()
        if(cached != null){
            return cached
        }else{
            val newMembers = withContext(api.plugin().asyncDispatcher) {
                api.membersController().getClanMembersAsync(this@ClanImpl)
            }
            if(newMembers.isFailure) {
                api.plugin().logger.severe("Could not retrieve members from database! ${this@ClanImpl}")
                api.plugin().logger.severe("Errors: ${newMembers.exceptionOrNull()}")
                return emptySet()
            }
            members.put(newMembers.getOrNull()!!)
            return members.get()!!
        }
    }

    override fun members(): CompletionStage<Set<ClanMember>> {
        return api.plugin().scope.future {
            membersAsync()
        }
    }

    override suspend fun invitesAsync(): Set<ClanInvite> {
        val cached = invites.get()
        if(cached != null){
            return cached
        }

        val status = withContext(api.plugin().asyncDispatcher) {
            database.clanInviteDao().getByClan(this@ClanImpl)
        }

        if(status.isFailure) {
            api.plugin().logger.severe("Could not retrieve invites from database! ${this@ClanImpl}")
            api.plugin().logger.severe("Errors: ${status.exceptionOrNull()}")


            return emptySet()
        }

        invites.put(status.getOrNull()!!)
        return status.getOrNull()!!
    }

    override fun invites(): CompletionStage<Set<ClanInvite>> {
        return api.plugin().scope.future {
            invitesAsync()
        }
    }

    override suspend fun requestsAsync(): Set<ClanRequest> {
        val cached = requests.get()
        if(cached != null){
            return cached
        }

        val status = withContext(api.plugin().asyncDispatcher) {
            database.clanRequestDao().getByClan(this@ClanImpl)
        }

        if(status.isFailure) {
            api.plugin().logger.severe("Could not retrieve requests from database! ${this@ClanImpl}")
            api.plugin().logger.severe("Errors: ${status.exceptionOrNull()}")
            return emptySet()
        }

        requests.put(status.getOrNull()!!)
        return status.getOrNull()!!
    }

    override fun requests(): CompletionStage<Set<ClanRequest>> {
        return api.plugin().scope.future {
            requestsAsync()
        }
    }

    override suspend fun economiesAsync(): Set<Economy<Any>> {
        val cached = economies.get()
        if(cached != null)
            return cached

        val status = withContext(api.plugin().asyncDispatcher){
            database.economyDao().getByClan(this@ClanImpl)
        }

        if(status.isFailure) {
            api.plugin().logger.severe("Could not retrieve economies from database! ${this@ClanImpl}")
            api.plugin().logger.severe("Errors: ${status.exceptionOrNull()}")
            return emptySet()
        }
        economies.put(status.getOrNull()!!)
        return status.getOrNull()!!
    }

    override fun economies(): CompletionStage<Set<Economy<Any>>>{
        return api.plugin().scope.future {
            economiesAsync()
        }
    }

    override fun created(): Date {
        return createDate
    }

    override suspend fun clanSettingsAsync(): ClanSettings {
        val cached = clanSettings.get()
        if(cached != null)
            return cached

        val status = withContext(api.plugin().asyncDispatcher){
            database.clanSettingsDao().getById(id())
        }
        if(status.isFailure) {
            api.plugin().logger.severe("Could not retrieve clan settings from database! ${this@ClanImpl}")
            api.plugin().logger.severe("Errors: ${status.exceptionOrNull()}")
            return ClanSettingsImpl()
        }
        clanSettings.put(status.getOrNull()!!)
        return clanSettings.get()!!
    }

    override fun clanSettings(): CompletionStage<ClanSettings> {
        return api.plugin().scope.future {
            clanSettingsAsync()
        }
    }

    override suspend fun kickMemberAsync(clanMember: ClanMember): Result<Success> {
        val membersCached = membersAsync()
        if(membersCached.contains(clanMember).not())
            return Result.failure(MemberNotFound("Member not found in clan!"))

        val status = withContext(api.plugin().asyncDispatcher) {
            val player = clanMember.playerAsync()

            database.memberDao().deleteById(player.uuid())
        }

        if(status.isFailure) {
            api.plugin().logger.severe("Could not delete member from database! $clanMember")
            api.plugin().logger.severe("Errors: ${status.exceptionOrNull()}")
            return Result.failure(status.exceptionOrNull()!!)
        }

        members.put(membersCached.minus(clanMember))

        return Result.success(Success)
    }

    override fun kickMember(clanMember: ClanMember): CompletionStage<Result<Success>> {
        return api.plugin().scope.future {
            kickMemberAsync(clanMember)
        }
    }

    override suspend fun addMemberAsync(clanPlayer: ClanPlayer): Result<ClanMember> {
        val membersCached = membersAsync()

        if(clanPlayer.isInClan())
            return Result.failure(MemberNotFound("Player is already in a clan!"))

        TODO("Create clan member and add player to the clan")

    }

    override fun addMember(clanPlayer: ClanPlayer): CompletionStage<Result<ClanMember>> {
        return api.plugin().scope.future {
            addMemberAsync(clanPlayer)
        }
    }

    override suspend fun setTagAsync(tag: String): Result<Success> {

        this.tag = tag
            // save clan
        val status = withContext(api.plugin().asyncDispatcher) {
            database.clanDao().save(this@ClanImpl)
        }

        if(status.isFailure) {
            api.plugin().logger.severe("Could not save clan to database! ${this@ClanImpl}")
            api.plugin().logger.severe("Errors: ${status.exceptionOrNull()}")
            return status
        }

        return Result.success(Success)
    }

    override suspend fun setBannerAsync(itemStack: ItemStack): Result<Success> {
        val cache = clanSettings.get()
        if(cache == null){
            // get new settings
            val status = withContext(api.plugin().asyncDispatcher) {
                database.clanSettingsDao().getById(this@ClanImpl.id())
            }

            status.onFailure { e ->
                return Result.failure(e)
            }

            val newSettings = status.getOrThrow()
            newSettings.banner(itemStack)

            val status2 = withContext(api.plugin().asyncDispatcher) {
                database.clanSettingsDao().save(newSettings)
            }

            status2.onSuccess { _ ->
                clanSettings.put(newSettings)
            }
            return status2
        }else{
            val old = cache.banner()
            cache.banner(itemStack)
            val status = withContext(api.plugin().asyncDispatcher) {
                database.clanDao().save(this@ClanImpl)
            }
            status.onSuccess {
                clanSettings.put(cache)
            }
            status.onFailure { e ->
                cache.banner(old)
                return Result.failure(e)
            }
            return Result.success(Success)
        }
    }



    override suspend fun getBannerAsync(): Result<ItemStack> {
        //
        val cache = clanSettings.get()
        if(cache != null){
            return Result.success(cache.banner())
        }

        val newSettings = clanSettingsAsync()
        clanSettings.put(newSettings as ClanSettingsImpl)
        return Result.success(clanSettings.get()!!.banner())
    }


}