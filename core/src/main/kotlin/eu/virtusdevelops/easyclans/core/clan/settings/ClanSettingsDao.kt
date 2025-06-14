package eu.virtusdevelops.easyclans.core.clan.settings

import eu.virtusdevelops.easyclans.core.clan.ClanSettingsImpl
import eu.virtusdevelops.easyclans.core.database.DaoCrud
import java.util.UUID

interface ClanSettingsDao : DaoCrud<ClanSettingsImpl, UUID> {


}