package eu.virtusdevelops.easyclans.storage.mysql.mappers;

import eu.virtusdevelops.easyclans.models.Clan;

import java.sql.ResultSet;
import java.util.function.Function;

public class ClanMysqlMapper implements Function<ResultSet, Clan> {

    @Override
    public Clan apply(ResultSet resultSet) {
        return null;
    }
}
