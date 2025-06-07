package eu.virtusdevelops.easyclans.api.member

import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.permission.ClanPermission

interface ClanMember {


    fun clan(): Clan

    fun permissions(): Set<ClanPermission>

}