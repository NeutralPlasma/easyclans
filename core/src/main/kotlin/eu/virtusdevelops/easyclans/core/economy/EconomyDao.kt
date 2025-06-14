package eu.virtusdevelops.easyclans.core.economy

import eu.virtusdevelops.easyclans.api.clan.Clan
import eu.virtusdevelops.easyclans.api.economy.Economy
import eu.virtusdevelops.easyclans.core.database.DaoCrud
import java.util.UUID

interface EconomyDao : DaoCrud<Economy<Any>, UUID> {


    fun getByClan(clan: Clan): Result<Set<Economy<Any>>>
}