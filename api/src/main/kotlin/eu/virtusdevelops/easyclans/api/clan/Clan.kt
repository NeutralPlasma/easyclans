package eu.virtusdevelops.easyclans.api.clan

import eu.virtusdevelops.easyclans.api.economy.Economy
import eu.virtusdevelops.easyclans.api.invite.ClanInvite
import eu.virtusdevelops.easyclans.api.member.ClanMember
import eu.virtusdevelops.easyclans.api.request.ClanRequest


interface Clan {


    fun name(): String

    fun tag(): String

    fun members(): Set<ClanMember>

    fun invites(): Set<ClanInvite>

    fun requests(): Set<ClanRequest>

    fun economies(): Set<Economy>
}