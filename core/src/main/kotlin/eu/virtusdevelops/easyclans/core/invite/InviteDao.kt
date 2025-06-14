package eu.virtusdevelops.easyclans.core.invite

import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.invite.ClanInvite
import eu.virtusdevelops.easyclans.core.database.DaoCrud
import java.util.UUID

interface InviteDao : DaoCrud<ClanInvite, UUID>{

    fun getByClan(clan: Clan): Result<Set<ClanInvite>>

}