package eu.virtusdevelops.easyclans.storage.mysql.mappers;

import eu.virtusdevelops.easyclans.models.CInvite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;

public class CInviteMapperMysql implements Function<ResultSet, CInvite> {
    @Override
    public CInvite apply(ResultSet resultSet) {
        try{
            return new CInvite(
                    resultSet.getObject("ec_clan_invite.id", UUID.class),
                    resultSet.getObject("ec_clan_invite.clan_id", UUID.class),
                    resultSet.getObject("ec_clan_invite.player_id", UUID.class),
                    resultSet.getLong("ec_clan_invite.expire_date"),
                    resultSet.getLong("ec_clan_invite.created_on")
            );
        }catch (SQLException e){
            return null;
        }
    }
}
