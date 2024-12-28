package eu.virtusdevelops.easyclans.storage.mysql.mappers;

import eu.virtusdevelops.easyclans.models.CRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;

public class CRequestMapperMysql implements Function<ResultSet, CRequest> {
    @Override
    public CRequest apply(ResultSet resultSet) {
        try{
            return new CRequest(
                    resultSet.getObject("ec_clan_join_requests.id", UUID.class),
                    resultSet.getObject("ec_clan_join_requests.clan_id", UUID.class),
                    resultSet.getObject("ec_clan_join_requests.player_id", UUID.class),
                    resultSet.getLong("ec_clan_join_requests.expire_date"),
                    resultSet.getLong("ec_clan_join_requests.created_on")
            );
        }catch (SQLException e){
            return null;
        }
    }
}
