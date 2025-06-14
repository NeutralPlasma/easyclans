package eu.virtusdevelops.easyclans.core.clan

import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.core.database.DaoCrud
import java.util.UUID

interface ClanDao : DaoCrud<Clan, UUID> {



}