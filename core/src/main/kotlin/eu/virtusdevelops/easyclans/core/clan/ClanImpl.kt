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
import eu.virtusdevelops.easyclans.core.EasyClansPlugin
import eu.virtusdevelops.easyclans.core.ExpiringCache
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

    override fun setTag(tag: String): Result<Success> {
        this.tag = tag
        api.plugin().launch {
            // save clan
            


        }
        TODO("Not yet implemented")
    }

    override fun setBanner(itemStack: Any): Result<Success> {
        // clan settings set banner
    }

    override fun getBanner(
        clanMember: ClanMember,
        bypass: Boolean
    ): Result<ItemStack> {
        //


        TODO("Not yet implemented")
    }


}