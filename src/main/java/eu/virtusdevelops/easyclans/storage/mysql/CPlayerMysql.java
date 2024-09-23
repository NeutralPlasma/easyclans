package eu.virtusdevelops.easyclans.storage.mysql;

import com.zaxxer.hikari.HikariDataSource;
import eu.virtusdevelops.easyclans.dao.CPlayerDao;
import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.Notification;
import eu.virtusdevelops.easyclans.models.UserPermissions;
import eu.virtusdevelops.easyclans.storage.mysql.mappers.CPlayerMapperMysql;
import eu.virtusdevelops.easyclans.storage.mysql.mappers.NotificationMapperMysql;
import eu.virtusdevelops.easyclans.storage.mysql.mappers.PermissionMapperMysql;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class CPlayerMysql implements CPlayerDao {
    private Logger logger;
    private HikariDataSource dataSource;

    private CPlayerMapperMysql playerMapper = new CPlayerMapperMysql();
    private PermissionMapperMysql permissionMapper = new PermissionMapperMysql();
    private NotificationMapperMysql notificationMapper = new NotificationMapperMysql();

    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
            """
                CREATE TABLE IF NOT EXISTS ec_player_data (
                    uuid UUID PRIMARY KEY,
                    clan_id CHAR(36),
                    last_active BIGINT,
                    joined_clan BIGINT,
                    name TEXT,
                    rank VARCHAR(255)
                );
                """
            );
            statement.execute();
        }catch (SQLException e) {
            logger.severe("Exception occured while initializing CPlayer MYSQL Dao");
            e.printStackTrace();
        }
    }


    @Override
    public CPlayer getById(UUID uuid) {
        // fetch player data and its permissions
        // notifications too

        try (Connection connection = dataSource.getConnection()){
            String sql =
            """
            SELECT ec_player_data.*, ec_permission.*
            FROM ec_player_data
            LEFT JOIN ec_permission ON ec_player_data.uuid = ec_permission.player_id
            WHERE uuid = ?
            """;
            var statement = connection.prepareStatement(sql);

            var resultSet = statement.executeQuery();
            if(!resultSet.next()){
                return null;
            }
            var player = playerMapper.apply(resultSet);
            if(player == null)
                return null;

            //
            while(resultSet.next()){
                var permission = permissionMapper.apply(resultSet);
                if(permission != null)
                    player.addPermission(permission);
            }


        }catch (SQLException e) {
            logger.severe("Exception occured while getting player data from database");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CPlayer> getAll() {
        var list = new ArrayList<CPlayer>();

        try (Connection connection = dataSource.getConnection()) {
            String sql =
            """
            SELECT ec_player_data.*, ec_permission.*
            FROM ec_player_data
            LEFT JOIN ec_permission ON ec_player_data.uuid = ec_permission.player_id
            ORDER BY ec_player_data.uuid DESC
            """;
            var statement = connection.prepareStatement(sql);
            CPlayer currentUser = null;
            UserPermissions currentPermission = null;

            var resultSet = statement.executeQuery();
            while(resultSet.next()){
                var uuid = resultSet.getObject("uuid", UUID.class);
                if(uuid == null)
                    continue;

                if(currentUser == null || !uuid.equals(currentUser.getUuid())){
                    currentUser = playerMapper.apply(resultSet);
                    if(currentUser == null)
                        continue;
                    list.add(currentUser);
                }
                // need to check if player already has permission
                var permission = permissionMapper.apply(resultSet);
                if(permission != null ){
                    if(currentPermission == null){
                        currentPermission = permission;
                        currentUser.addPermission(permission);
                    } else if (currentPermission != permission) {
                        currentPermission = permission;
                        currentUser.addPermission(permission);
                    }
                }
            }



        }catch (SQLException e) {
            logger.severe("Exception occured while getting all player data from database");
            e.printStackTrace();
        }


        return List.of();
    }

    @Override
    public CPlayer save(CPlayer cPlayer) {
        return null;
    }


    @Override
    public boolean delete(CPlayer cPlayer) {
        return false;
    }
}
