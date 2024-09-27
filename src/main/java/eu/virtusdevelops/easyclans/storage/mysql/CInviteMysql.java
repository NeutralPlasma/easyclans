package eu.virtusdevelops.easyclans.storage.mysql;

import com.zaxxer.hikari.HikariDataSource;
import eu.virtusdevelops.easyclans.dao.CInviteDao;
import eu.virtusdevelops.easyclans.models.CInvite;
import eu.virtusdevelops.easyclans.storage.mysql.mappers.CInviteMapperMysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class CInviteMysql implements CInviteDao {
    private final Logger logger;
    private final HikariDataSource dataSource;
    private final CInviteMapperMysql mapper;

    public CInviteMysql(final Logger logger, final HikariDataSource dataSource, CInviteMapperMysql mapper) {
        this.logger = logger;
        this.dataSource = dataSource;
        this.mapper = mapper;
    }


    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
            """
                CREATE TABLE IF NOT EXISTS ec_clan_invite (
                    id UUID PRIMARY KEY,
                    clan_id UUID,
                    player_id UUID,
                    expire_date BIGINT,
                    created_on BIGINT,
                    FOREIGN KEY (player_id) REFERENCES ec_player_data(uuid) ON DELETE CASCADE,
                    FOREIGN KEY (clan_id) REFERENCES ec_clan_data(id) ON DELETE CASCADE
                );
                """
            );
            statement.execute();
        }catch (SQLException e) {
            logger.severe("Exception occured while initializing CInvite MYSQL Dao");
            e.printStackTrace();
        }
    }

    @Override
    public CInvite getById(UUID uuid) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
            """
                SELECT * FROM ec_clan_invite
                WHERE id = ?
                """
            );
            statement.setObject(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return mapper.apply(resultSet);
            }
        }catch (SQLException e) {
            logger.severe("Exception while trying to get invite: " + uuid.toString());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CInvite> getAll() {
        List<CInvite> invites = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
            """
                SELECT * FROM ec_clan_invite
                """
            );
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                var invite = mapper.apply(resultSet);
                if(invite != null) {
                    invites.add(invite);
                }
            }
        }catch (SQLException e) {
            logger.severe("Exception while trying to get all clan invites");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CInvite save(CInvite cInvite) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
            """
                INSERT INTO ec_clan_invite (invite_id, inviter_id, invitee_id, expire_date, created_on)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT(invite_id) DO UPDATE SET
                    expire_date = excluded.expire_date,
                    created_on = excluded.created_on;
                """
            );
            statement.setObject(1, cInvite.inviteId());
            statement.setObject(2, cInvite.clanId());
            statement.setObject(3, cInvite.playerUuid());
            statement.setLong(4, cInvite.expireTime());
            statement.setLong(5, cInvite.createdTime());

            int updatedRows = statement.executeUpdate();
            if(updatedRows > 0) {
                return cInvite;
            }
        }catch (SQLException e) {
            logger.severe("Exception while trying to get invite: " + cInvite.toString());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(CInvite cInvite) {
        try(Connection connection = dataSource.getConnection()){
            String sql =
                    """
                    DELETE FROM ec_clan_invite
                    WHERE id = ?
                    """;

            var statement = connection.prepareStatement(sql);
            statement.setObject(1, cInvite.inviteId());
            return statement.executeUpdate() > 0;
        }catch (SQLException e){
            logger.severe("Exception occured while deleting clan invite " + cInvite.inviteId());
            e.printStackTrace();
            return false;
        }
    }
}
