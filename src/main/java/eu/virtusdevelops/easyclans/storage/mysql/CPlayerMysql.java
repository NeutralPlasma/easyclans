package eu.virtusdevelops.easyclans.storage.mysql;

import com.zaxxer.hikari.HikariDataSource;
import eu.virtusdevelops.easyclans.dao.CPlayerDao;
import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.UserPermissions;
import eu.virtusdevelops.easyclans.storage.mysql.mappers.CPlayerMapperMysql;
import eu.virtusdevelops.easyclans.storage.mysql.mappers.NotificationMapperMysql;
import eu.virtusdevelops.easyclans.storage.mysql.mappers.PermissionMapperMysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CPlayerMysql implements CPlayerDao {
    private final Logger logger;
    private final HikariDataSource dataSource;

    private final CPlayerMapperMysql playerMapper;
    private final PermissionMapperMysql permissionMapper;
    private final NotificationMapperMysql notificationMapper;

    public CPlayerMysql(final Logger logger, final HikariDataSource dataSource, CPlayerMapperMysql playerMapper,
                        PermissionMapperMysql permissionMapper, NotificationMapperMysql notificationMapper) {
        this.dataSource = dataSource;
        this.logger = logger;
        this.playerMapper = playerMapper;
        this.permissionMapper = permissionMapper;
        this.notificationMapper = notificationMapper;
    }


    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
            """
                CREATE TABLE IF NOT EXISTS ec_player (
                    uuid UUID PRIMARY KEY,
                    clan_id UUID,
                    last_active TIMESTAMP,
                    joined_clan TIMESTAMP,
                    name TEXT,
                    rank VARCHAR(255)
                );
                """
            );
            statement.execute();

            statement = connection.prepareStatement(
        """
                CREATE TABLE IF NOT EXISTS ec_permission (
                    id INTEGER PRIMARY KEY AUTO_INCREMENT,
                    player_id UUID,
                    permission VARCHAR(128),
                    FOREIGN KEY (player_id) REFERENCES ec_player(uuid) ON DELETE CASCADE
                );
            """);
            statement.executeUpdate();

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
            SELECT ec_player.*, ec_permission.*
            FROM ec_player
            LEFT JOIN ec_permission ON ec_player.uuid = ec_permission.player_id
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
            SELECT ec_player.*, ec_permission.*
            FROM ec_player
            LEFT JOIN ec_permission ON ec_player.uuid = ec_permission.player_id
            ORDER BY ec_player.uuid DESC
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
        try(Connection connection = dataSource.getConnection()){
            String sql =
            """
            INSERT INTO ec_player (uuid, clan_id, last_active, joined_clan, rank)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(uuid) DO UPDATE SET
                clan_id = excluded.clan_id,
                last_active = excluded.last_active,
                joined_clan = excluded.joined_clan,
                rank = excluded.rank;
            """;
            var statement = connection.prepareStatement(sql);
            statement.setObject(1, cPlayer.getUuid());
            statement.setObject(2, cPlayer.getClanID());
            statement.setLong(3, cPlayer.getLastActive());
            statement.setLong(4, cPlayer.getJoinClanDate());
            statement.setString(5, cPlayer.getRank());
            statement.executeUpdate();

            savePlayerPermissions(cPlayer);

        }catch (SQLException e) {
            logger.severe("Exception occured while saving player data");
            e.printStackTrace();
        }
        return cPlayer;
    }


    @Override
    public boolean savePlayerPermissions(CPlayer cPlayer) {
        try (Connection connection = dataSource.getConnection()) {

            var st_statement = String.format("""
                    DELETE FROM
                    ec_permission
                    WHERE ec_permissions.player_id = ?
                    AND ec_permissions.permission NOT IN (%s)
                    """,
                    cPlayer.getUserPermissionsList()
                            .stream().map(it -> "?")
                            .collect(Collectors.joining(", "))
            );

            PreparedStatement statement = connection.prepareStatement(st_statement);



            statement.setObject(1, cPlayer.getUuid());
            int index = 2;
            for(var perm : cPlayer.getUserPermissionsList()){
                statement.setString(index, perm.name());
                index++;
            }
            statement.executeUpdate();
            for(var permission : cPlayer.getUserPermissionsList()){
                PreparedStatement statement2 = connection.prepareStatement("""
                INSERT INTO ec_permission (permission, player_id)
                SELECT ?, ?
                WHERE NOT EXISTS (
                    SELECT *
                    FROM ec_permission
                    WHERE permission = ?
                    AND player_id = ?
                )
                """);
                statement2.setString(1, permission.name());
                statement2.setObject(2, cPlayer.getUuid());
                statement2.setString(3, permission.name());
                statement2.setObject(4, cPlayer.getUuid());
                statement2.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean delete(CPlayer cPlayer) {
        // just run delete, other databases should use cascade to delete information

        try(Connection connection = dataSource.getConnection()){
            String sql =
            """
            DELETE FROM ec_player
            WHERE uuid = ?
            """;

            var statement = connection.prepareStatement(sql);
            statement.setObject(1, cPlayer.getUuid());
            return statement.executeUpdate() > 0;
        }catch (SQLException e){
            logger.severe("Exception occured while deleting player data");
            e.printStackTrace();
            return false;
        }
    }
}
