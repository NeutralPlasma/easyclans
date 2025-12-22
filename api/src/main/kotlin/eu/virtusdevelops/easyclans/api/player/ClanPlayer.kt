package eu.virtusdevelops.easyclans.api.player

import java.util.Date
import java.util.UUID

interface ClanPlayer {

    fun name(): String

    fun uuid(): UUID

    fun lastActive(): Date

    fun isInClan(): Boolean

    fun rank(): String

}