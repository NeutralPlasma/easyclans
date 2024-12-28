package eu.virtusdevelops.easyclans.storage.mysql;

import eu.virtusdevelops.easyclans.dao.TrophyDao;
import eu.virtusdevelops.easyclans.models.Trophy;

import java.util.List;
import java.util.UUID;

public class TrophyMysql implements TrophyDao{

    @Override
    public void init() {

    }

    @Override
    public Trophy getById(UUID uuid) {
        return null;
    }

    @Override
    public List<Trophy> getAll() {
        return List.of();
    }

    @Override
    public Trophy save(Trophy trophy) {
        return null;
    }

    @Override
    public boolean delete(Trophy trophy) {
        return false;
    }
}
