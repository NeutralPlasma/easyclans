package eu.virtusdevelops.easyclans.core.member

import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.member.ClanMember
import eu.virtusdevelops.easyclans.core.database.DaoCrud
import java.util.UUID

interface MemberDao : DaoCrud<ClanMember, UUID> {


    fun getClanMembers(clan: Clan): Result<Set<ClanMember>>
}