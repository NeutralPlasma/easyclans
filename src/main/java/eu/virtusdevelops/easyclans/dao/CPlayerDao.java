package eu.virtusdevelops.easyclans.dao;

import eu.virtusdevelops.easyclans.models.CPlayer;

import java.util.UUID;

public interface CPlayerDao extends DaoCrud<CPlayer, UUID> {
    boolean savePlayerPermissions(CPlayer player);
}
