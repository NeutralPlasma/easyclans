package eu.virtusdevelops.easyclans.dao;

import eu.virtusdevelops.easyclans.models.Log;

import java.util.List;
import java.util.UUID;

public interface LogDao extends DaoCrud<Log, Long> {

    List<Log> getClanLogs(UUID clanId, int amount, int page);

    List<Log> getPlayerClanLogs(UUID clanId, UUID playerId, int amount, int page);
}
