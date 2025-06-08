package eu.virtusdevelops.easyclans.api.permission

import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.member.ClanMember

interface ClanPermission {

    fun target(): ClanMember

    fun clan(): Clan

    fun permission(): Permission


}