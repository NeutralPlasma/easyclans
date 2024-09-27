package eu.virtusdevelops.easyclans.storage.mysql.mappers;

import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.models.LogType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;

public class LogMapperMysql implements Function<ResultSet, Log> {
    @Override
    public Log apply(ResultSet resultSet) {

        try{
            return new Log(
                    resultSet.getLong("ec_log.id"),
                    resultSet.getString("ec_log.message"),
                    resultSet.getObject("ec_log.player_id", UUID.class),
                    resultSet.getObject("ec_log.clan_id", UUID.class),
                    LogType.valueOf(resultSet.getString("ec_log.log_type")),
                    resultSet.getLong("ec_log.created_on")
            );
        }catch (SQLException e){
            return null;
        }
    }
}
