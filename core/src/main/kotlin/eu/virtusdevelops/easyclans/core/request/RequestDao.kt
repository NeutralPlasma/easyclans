package eu.virtusdevelops.easyclans.core.request

import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.request.ClanRequest
import eu.virtusdevelops.easyclans.core.database.DaoCrud
import java.util.UUID

interface RequestDao : DaoCrud<ClanRequest, UUID> {

    fun getByClan(clan: Clan): Result<Set<ClanRequest>>
}