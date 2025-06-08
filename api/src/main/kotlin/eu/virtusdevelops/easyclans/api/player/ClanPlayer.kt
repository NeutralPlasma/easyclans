package eu.virtusdevelops.easyclans.api.player

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.Date
import java.util.UUID

interface ClanPlayer {

    fun name(): String

    fun uuid(): UUID

    fun player(): Player?

    fun offlinePlayer(): OfflinePlayer

    fun lastActive(): Date

    fun isInClan(): Boolean

    fun rank(): String

}