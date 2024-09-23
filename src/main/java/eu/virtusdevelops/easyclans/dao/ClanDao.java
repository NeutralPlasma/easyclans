package eu.virtusdevelops.easyclans.dao;

import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.Log;

import java.util.List;
import java.util.UUID;

public interface ClanDao extends DaoCrud<Clan, UUID> {

    List<Log> getClanLogs(UUID id, int amount, int page);

    List<Log> getPlayerClanLogs(UUID id, UUID playerId, int amount, int page);
}
