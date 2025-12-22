package eu.virtusdevelops.easyclans.core.invite

import com.github.shynixn.mccoroutine.bukkit.scope
import eu.virtusdevelops.easyclans.api.EasyClansAPI
import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.invite.ClanInvite
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import eu.virtusdevelops.easyclans.core.database.Database
import kotlinx.coroutines.future.future
import java.util.Date
import java.util.UUID
import java.util.concurrent.CompletionStage

class ClanInviteImpl(
    private val id: UUID,
    private val clan: UUID,
    private val sender: UUID,
    private val target: UUID,
    override val sentDate: Date,
    override val expireDate: Date,
    override val cancelled: Boolean = false,

    private val api: EasyClansAPI,
    private val database: Database
) : ClanInvite {

    override suspend fun clanAsync(): Clan {
        return api.clanController().getClanAsync(clan)!!
    }

    override fun clan(): CompletionStage<Clan> {
        return api.plugin().scope.future {
            clanAsync()
        }
    }

    override suspend fun senderAsync(): ClanPlayer {
        return api.playerController().getAsync(sender).getOrThrow()
    }

    override fun sender(): CompletionStage<ClanPlayer> {
        return api.plugin().scope.future {
            senderAsync()
        }
    }

    override suspend fun targetAsync(): ClanPlayer {
        return api.playerController().getAsync(target).getOrThrow()
    }

    override fun target(): CompletionStage<ClanPlayer> {
        return api.plugin().scope.future {
           targetAsync()
        }
    }

    override val expired: Boolean = expireDate.before(Date())

    override suspend fun declineAsync() {
        TODO("Not yet implemented")
    }

    override fun decline(): CompletionStage<Void> {
        TODO("Not yet implemented")
    }

    override suspend fun acceptAsync(): Result<Success> {
        TODO("Not yet implemented")

        // get clan
        // try to add player to clan
        // if success then gud



    }

    override fun accept(): CompletionStage<Result<Success>> {
        TODO("Not yet implemented")
    }
}