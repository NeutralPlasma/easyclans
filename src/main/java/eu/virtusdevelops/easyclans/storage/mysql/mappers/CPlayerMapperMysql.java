package eu.virtusdevelops.easyclans.storage.mysql.mappers;

import eu.virtusdevelops.easyclans.models.CPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;

public class CPlayerMapperMysql implements Function<ResultSet, CPlayer> {
    @Override
    public CPlayer apply(ResultSet rs) {
        try{
            return new CPlayer(
                    rs.getObject("ec_player_data.uuid", UUID.class),
                    rs.getObject("ec_player_data.clan_id", UUID.class),
                    rs.getLong("ec_player_data.last_active"),
                    rs.getLong("ec_player_data.joined_clan"),
                    rs.getString("ec_player_data.name"),
                    rs.getString("ec_player_data.rank")
            );
        }catch (SQLException e){
            return null;
        }
    }
}
