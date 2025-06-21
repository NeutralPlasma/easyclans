package eu.virtusdevelops.easyclans.core.clan

import com.github.shynixn.mccoroutine.bukkit.scope
import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.clan.ClansController
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import kotlinx.coroutines.future.future
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import java.util.concurrent.CompletionStage

class ClanControllerImpl(
    private val plugin: JavaPlugin
) : ClansController{


    override suspend fun createClanAsync(
        clanName: String,
        tag: String,
        owner: ClanPlayer
    ): Result<Clan> {
        TODO("Not yet implemented")
    }

    override fun createClan(
        clanName: String,
        tag: String,
        owner: ClanPlayer
    ): CompletionStage<Result<Clan>> {
        return plugin.scope.future {
            createClanAsync(clanName, tag, owner)
        }
    }

    override suspend fun getClanAsync(id: UUID): Clan? {
        TODO("Not yet implemented")
    }

    override fun getClan(id: UUID): CompletionStage<Clan?> {
        return plugin.scope.future {
            getClanAsync(id)
        }
    }

    override suspend fun deleteClanAsync(clan: Clan): Result<Success> {
        TODO("Not yet implemented")
    }

    override fun deleteClan(clan: Clan): CompletionStage<Result<Success>> {
        return plugin.scope.future {
            deleteClanAsync(clan)
        }
    }
}