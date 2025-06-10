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
    private var name: String,
    private var tag: String,
    private var owner: UUID,
    private var createDate: Date,
    private var clanSettings: ExpiringCache<ClanSettingsImpl>,

    private var members: ExpiringCache<Set<ClanMember>>,
    private var invites: ExpiringCache<Set<ClanInvite>>,
    private var requests: ExpiringCache<Set<ClanRequest>>
) : Clan {



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

    override suspend fun ownerAsync(): ClanMember = withContext(api.plugin().asyncDispatcher) {
        val player = api.playerController().getAsync(owner)
        if(player == null) throw IllegalStateException()
        val member = api.membersController().getAsync(player)
        if(member == null) throw IllegalStateException()
        member
    }

    override fun owner(): CompletionStage<ClanMember> {
        return api.plugin().scope.future {
            ownerAsync()
        }
    }

    override suspend fun membersAsync(): Set<ClanMember> = withContext(api.plugin().asyncDispatcher) {
        val cached = members.get()
        if(cached != null){
            cached
        }else{
            val newMembers = api.membersController().getClanMembersAsync(this@ClanImpl)

            members = ExpiringCache(1, TimeUnit.HOURS)
            members.put(newMembers)

            members.get()!!
        }
    }

    override fun members(): CompletionStage<Set<ClanMember>> {
        return api.plugin().scope.future {
            membersAsync()
        }
    }

    override suspend fun invitesAsync(): Set<ClanInvite> {
        TODO("Not yet implemented")
    }

    override fun invites(): CompletionStage<Set<ClanInvite>> {
        return api.plugin().scope.future {
            invitesAsync()
        }
    }

    override suspend fun requestsAsync(): Set<ClanRequest> {
        TODO("Not yet implemented")
    }

    override fun requests(): CompletionStage<Set<ClanRequest>> {
        return api.plugin().scope.future {
            requestsAsync()
        }
    }

    override fun economies(): Set<Economy<Any>> {
        TODO("Not yet implemented")
    }

    override fun created(): Date {
        TODO("Not yet implemented")
    }

    override suspend fun clanSettingsAsync(): ClanSettings {
        TODO("Not yet implemented")
    }

    override fun clanSettings(): CompletionStage<ClanSettings> {
        TODO("Not yet implemented")
    }

    override suspend fun kickMember(clanMember: ClanMember): Result<Success> {
        TODO("Not yet implemented")
    }

    override fun kickMember(
        clanMember: ClanMember,
        bypass: Boolean
    ): CompletionStage<Result<Success>> {
        TODO("Not yet implemented")
    }

    override suspend fun addMember(clanPlayer: ClanPlayer): ClanMember {
        TODO("Not yet implemented")
    }

    override fun addMember(
        clanPlayer: ClanPlayer,
        bypass: Boolean
    ): CompletionStage<ClanMember> {
        TODO("Not yet implemented")
    }

    override suspend fun setTagAsync(tag: String): Result<Success>
    = withContext(api.plugin().asyncDispatcher) {

        this@ClanImpl.tag = tag
            // save clan
        val status = database.clanDao().save(this@ClanImpl)
        if(status.isFailure) {
            api.plugin().logger.severe("Could not save clan to database! ${this@ClanImpl}")
            api.plugin().logger.severe("Errors: ${status.exceptionOrNull()}")


            return@withContext status
        }


        return@withContext Result.success(Success)
    }

    override suspend fun setBannerAsync(itemStack: ItemStack): Result<Success>
    = withContext(api.plugin().asyncDispatcher) {
        val cache = clanSettings.get()
        if(cache == null){
            // get new settings
            val status = database.clanDao().getClanSettings(this@ClanImpl)

            status.onFailure { e ->
                return@withContext Result.failure(e)
            }


            TODO("Not yet implemented")

            return@withContext Result.success(Success)

        }else{
            cache.banner(itemStack)
            clanSettings.put(cache)
            val status = database.clanDao().update(this@ClanImpl)
            status.onFailure { e ->
                return@withContext Result.failure(e)
            }
            return@withContext Result.success(Success)
        }
    }



    override suspend fun getBannerAsync(
        clanMember: ClanMember,
        bypass: Boolean
    ): Result<ItemStack> = withContext(api.plugin().asyncDispatcher) {
        //
        val cache = clanSettings.get()
        if(cache != null){
            return@withContext Result.success(cache.banner())
        }

        val status = database.clanDao().getClanSettings(this@ClanImpl)
        status.onFailure { e ->
            return@withContext Result.failure(e)
        }

        clanSettings = ExpiringCache<ClanSettingsImpl>(1, TimeUnit.HOURS)
        clanSettings.put(status.getOrThrow())


        return@withContext Result.success(clanSettings.get()!!.banner())
    }


}