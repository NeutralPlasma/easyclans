package eu.virtusdevelops.easyclans.api.member

import eu.virtusdevelops.easyclans.api.Success
import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.permission.ClanPermission
import eu.virtusdevelops.easyclans.api.player.ClanPlayer
import java.util.Date

interface ClanMember {

    fun clan(): Clan

    fun player(): ClanPlayer

    fun permissions(): Set<ClanPermission>

    fun joinDate(): Date

    fun clanChat(): Boolean

    fun setClanChat(clanChat: Boolean)

    fun isActive(): Boolean

    fun interestRate(): Double


    // permissions

    fun has(permission: ClanPermission): Boolean

    fun add(permission: ClanPermission): Result<Success>

    fun remove(permission: ClanPermission): Result<Success>
}