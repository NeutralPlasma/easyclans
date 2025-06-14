package eu.virtusdevelops.easyclans.core.member

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.scope
import eu.virtusdevelops.easyclans.api.EasyClansAPI
import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.member.ClanMember
import eu.virtusdevelops.easyclans.api.permission.ClanPermission
import eu.virtusdevelops.easyclans.api.permission.Permission
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import eu.virtusdevelops.easyclans.core.database.Database
import kotlinx.coroutines.future.future
import java.util.Date
import java.util.UUID
import java.util.concurrent.CompletionStage

class ClanMemberImpl(
    private val id: UUID,
    private val clan: UUID,
    private val joinDate: Date,

    private var clanChat: Boolean = false,
    private var active: Boolean = false,


    private var permissions: Set<ClanPermission> = emptySet()
) : ClanMember {

    private val api by lazy {
        EasyClansAPI.get()
    }

    private val database by lazy {
        Database.get()
    }


    override suspend fun clanAsync(): Clan {
        return api.clanController().getClanAsync(clan)!!
    }

    override fun clan(): CompletionStage<Clan> {
        return api.plugin().scope.future {
            clanAsync()
        }
    }

    override fun joinDate(): Date {
        return joinDate
    }

    override suspend fun playerAsync(): ClanPlayer {
        return api.playerController().getAsync(id).getOrThrow()
    }

    override fun player(): CompletionStage<ClanPlayer> {
        return api.plugin().scope.future {
            playerAsync()
        }
    }

    override fun permissions(): Set<ClanPermission> {
        return permissions
    }

    override fun clanChat(): Boolean = clanChat

    override fun setClanChat(clanChat: Boolean) {
        this.clanChat = clanChat
    }

    override fun isActive(): Boolean = active


    override fun interestRate(): Double {
        TODO("Not yet implemented")
    }

    override fun has(permission: Permission): Boolean {
        return permissions.find { it.permission() == permission } != null
    }

    override suspend fun addAsync(permission: ClanPermission): Result<Success> {
        if(has(permission.permission()))
            return Result.failure(IllegalArgumentException("Permission is already present!"))

        val status = with(api.plugin().asyncDispatcher){
            database.memberDao().save(this@ClanMemberImpl)
        }

        if(status.isFailure) {
            api.plugin().logger.severe("Could not add member to database! $this")
            api.plugin().logger.severe("Errors: ${status.exceptionOrNull()}")
            return Result.failure(status.exceptionOrNull()!!)
        }

        permissions = permissions.plus(permission)


        return status
    }

    override fun add(permission: ClanPermission): CompletionStage<Result<Success>> {
        return api.plugin().scope.future {
            addAsync(permission)
        }
    }

    override suspend fun removeAsync(permission: Permission): Result<Success> {
        if(!has(permission))
            return Result.failure(IllegalArgumentException("Permission is not present!"))

        val status = with(api.plugin().asyncDispatcher){
            database.memberDao().save(this@ClanMemberImpl)
        }
        if(status.isFailure) {
            api.plugin().logger.severe("Could not delete member from database! $this")
            api.plugin().logger.severe("Errors: ${status.exceptionOrNull()}")
            return Result.failure(status.exceptionOrNull()!!)
        }
        permissions.find { it.permission() == permission }?.let {
            permissions = permissions.minus(it)
        }

        return status
    }

    override fun remove(permission: Permission): CompletionStage<Result<Success>> {
        return api.plugin().scope.future {
            removeAsync(permission)
        }
    }
}