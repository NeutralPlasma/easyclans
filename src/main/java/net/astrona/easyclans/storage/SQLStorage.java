package net.astrona.easyclans.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.models.CRequest;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.models.Log;
import net.astrona.easyclans.utils.Serialization;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Logger;

public class SQLStorage {

    private final Logger logger;
    private HikariDataSource dataSource;

    private boolean isMysql;

    public SQLStorage(ClansPlugin plugin) {
        this.logger = plugin.getLogger();

        ConfigurationSection dbSection = plugin.getConfig().getConfigurationSection("db");

        if (dbSection == null) {
            logger.severe("Database properties not found in 'config.yml'.");
            //plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        isMysql = dbSection.getBoolean("mysql");
        String username = plugin.getConfig().getString("username");
        String password = plugin.getConfig().getString("password");
        String hostname = plugin.getConfig().getString("ip");
        int port = plugin.getConfig().getInt("port");
        String database = plugin.getConfig().getString("database");
        boolean SSL = plugin.getConfig().getBoolean("ssl");
        String path = plugin.getDataFolder().getPath();

        HikariConfig hikariConfig = new HikariConfig();
        if (isMysql) {
            hikariConfig.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");

            hikariConfig.addDataSourceProperty("user", username);
            hikariConfig.addDataSourceProperty("password", password);
            hikariConfig.addDataSourceProperty("useSSL", SSL);
            hikariConfig.addDataSourceProperty("databaseName", database);
            hikariConfig.addDataSourceProperty("serverName", hostname);
            hikariConfig.addDataSourceProperty("port", port);
            //hikariConfig.setUsername(username);
            //hikariConfig.setPassword(password);
            //hikariConfig.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=" + SSL);
        } else {
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + path + "/db.sqlite");
        }

        hikariConfig.setPoolName("EasyClansPlugin");
        hikariConfig.setMaximumPoolSize(60000);
        hikariConfig.setMaximumPoolSize(10);
        //hikariConfig.addDataSourceProperty("database", database);
        this.dataSource = new HikariDataSource(hikariConfig);

        logger.info("Creating database tables...");
        this.createPlayersTable();
        this.createClansTable();
        this.createClanInvitesTable();
        this.createClanJoinRequestsTable();
        this.createLogsTable();
    }


    // <editor-fold desc="Table creation">
    private void createPlayersTable() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            CREATE TABLE IF NOT EXISTS ec_player_data (
                                uuid CHAR(36) PRIMARY KEY,
                                clan INT,
                                last_active BIGINT,
                                joined_clan BIGINT,
                                name TEXT
                            );
                            """
            );
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not initialize the sql tables!");
            //logger.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    private void createClansTable() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            CREATE TABLE IF NOT EXISTS ec_clan_data(
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                owner CHAR(36) NOT NULL,
                                clan_name VARCHAR(16),
                                display_name TEXT,
                                autokick_time INT,
                                join_points_price INT,
                                join_money_price DOUBLE,
                                interest_rate DOUBLE,
                                banner TEXT,
                                bank DOUBLE,
                                tag VARCHAR(16),
                                
                                created_on DATETIME,
                                FOREIGN KEY (owner) REFERENCES ec_player_data(uuid)
                            );
                            """//.formatted(autoIncrement)
            );
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not initialize the sql tables!");
            e.printStackTrace();
        }
    }

    private void createClanInvitesTable() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            CREATE TABLE IF NOT EXISTS ec_clan_invites (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                clan INT,
                                player_id VARCHAR(36),
                                expire_date BIGINT,
                                created_on BIGINT,
                                
                                FOREIGN KEY (clan) REFERENCES ec_clan_data(id),
                                FOREIGN KEY (player_id) REFERENCES ec_player_data(uuid)
                            );
                            """
            );
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not initialize the sql tables!");
            e.printStackTrace();
        }
    }

    private void createLogsTable(){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                        CREATE TABLE IF NOT EXISTS ec_logs (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            message TEXT,
                            clan_id INTEGER,
                            player_id VARCHAR(36),
                            log_type VARCHAR(25),
                            created_on BIGINT,
                            FOREIGN KEY (clan_id) REFERENCES ec_clan_data(id),
                            FOREIGN KEY (player_id) REFERENCES ec_player_data(uuid)
                        );
                        """
            );
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not initialize the sql tables!");
            e.printStackTrace();
        }
    }

    private void createClanJoinRequestsTable() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            CREATE TABLE IF NOT EXISTS ec_clan_join_requests (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                clan INT,
                                player_id VARCHAR(36),
                                expire_date BIGINT,
                                created_on BIGINT,
                                
                                FOREIGN KEY (clan) REFERENCES ec_clan_data(id),
                                FOREIGN KEY (player_id) REFERENCES ec_player_data(uuid)
                            );
                            """
            );
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not initialize the sql tables!");
            e.printStackTrace();
        }
    }

    //</editor-fold>

    //<editor-fold desc="player stuff">
    public void insertPlayer(CPlayer cPlayer) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO ec_player_data
                    (uuid, clan, last_active, joined_clan, name)
                    VALUES
                    (?, ?, ?, ?, ?)
                    """);
            statement.setString(1, cPlayer.getUuid().toString());
            statement.setInt(2, cPlayer.getClanID());
            statement.setLong(3, cPlayer.getLastActive());
            statement.setLong(4, cPlayer.getJoinClanDate());
            statement.setString(5, cPlayer.getName());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayer(CPlayer cPlayer) {

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE ec_player_data
                    SET
                    clan = ?,
                    last_active = ?,
                    joined_clan = ?
                    WHERE uuid = ?
                    """);
            statement.setInt(1, cPlayer.getClanID());
            statement.setLong(2, cPlayer.getLastActive());
            statement.setLong(3, cPlayer.getJoinClanDate());
            statement.setString(4, cPlayer.getUuid().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public CPlayer getPlayer(UUID uuid) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM
                    ec_player_data
                    WHERE
                    uuid = ?
                    """);
            statement.setString(1, uuid.toString());
            var result = statement.executeQuery();
            if (result.next()) {
                return new CPlayer(
                        uuid,
                        result.getInt("clan"),
                        result.getLong("last_active"),
                        result.getLong("joined_clan"),
                        result.getString("name")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CPlayer> getAllPlayers() {
        List<CPlayer> players = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM
                    ec_player_data
                    """);
            var result = statement.executeQuery();
            while (result.next()) {
                players.add(new CPlayer(
                        UUID.fromString(result.getString("uuid")),
                        result.getInt("clan"),
                        result.getLong("last_active"),
                        result.getLong("joined_clan"),
                        result.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }


    //</editor-fold>

    //<editor-fold desc="clan stuff">

    private List<UUID> getClanMembers(int clan_id) {
        List<UUID> members = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT uuid FROM
                    ec_player_data
                    WHERE clan = ?
                    """);
            statement.setInt(1, clan_id);

            var result = statement.executeQuery();
            while (result.next()) {
                members.add(
                        UUID.fromString(result.getString("uuid"))
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public List<Clan> getAllClans() {
        List<Clan> clans = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM
                    ec_clan_data
                    """);
            var result = statement.executeQuery();
            while (result.next()) {
                var clan = new Clan(
                        result.getInt("id"),
                        UUID.fromString(result.getString("owner")),
                        result.getString("clan_name"),
                        result.getString("display_name"),
                        result.getInt("autokick_time"),
                        result.getInt("join_points_price"),
                        result.getDouble("join_money_price"),
                        Serialization.decodeItemBase64(result.getString("banner")),
                        result.getDouble("bank"),
                        result.getDouble("interest_rate"),
                        result.getString("tag"),
                        null,
                        result.getLong("created_on")
                );
                clan.setMembers(getClanMembers(clan.getId()));
                clans.add(clan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clans;
    }


    public void updateClan(Clan clan) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE ec_clan_data
                    SET
                    owner = ?,
                    clan_name = ?,
                    display_name = ?,
                    autokick_time = ?,
                    join_points_price = ?,
                    join_money_price = ?,
                    banner = ?,
                    bank = ?,
                    interest_rate = ?,
                    tag = ?
                    """);
            statement.setString(1, clan.getOwner().toString());
            statement.setString(2, clan.getName());
            statement.setString(3, clan.getDisplayName());
            statement.setInt(4, clan.getAutoKickTime());
            statement.setInt(5, clan.getJoinPointsPrice());
            statement.setDouble(6, clan.getJoinMoneyPrice());
            statement.setString(7, Serialization.encodeItemBase64(clan.getBanner()));
            statement.setDouble(8, clan.getBank());
            statement.setDouble(9, clan.getInterestRate());
            statement.setString(10, clan.getTag());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveClan(Clan clan) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO ec_clan_data
                    (
                    owner,
                    clan_name,
                    display_name,
                    autokick_time,
                    join_points_price,
                    join_money_price,
                    banner,
                    bank,
                    interest_rate,
                    tag,
                    created_on
                    )
                    VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """);
            statement.setString(1, clan.getOwner().toString());
            statement.setString(2, clan.getName());
            statement.setString(3, clan.getDisplayName());
            statement.setInt(4, clan.getAutoKickTime());
            statement.setInt(5, clan.getJoinPointsPrice());
            statement.setDouble(6, clan.getJoinMoneyPrice());
            statement.setString(7, Serialization.encodeItemBase64(clan.getBanner()));
            statement.setDouble(8, clan.getBank());
            statement.setDouble(9, clan.getInterestRate());
            statement.setString(10, clan.getTag());
            statement.setLong(11, clan.getCreatedOn());

            int rows = statement.executeUpdate();
            if(rows>0) {
                var result = statement.getGeneratedKeys();
                if (result.next()) {
                    clan.setId(result.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //</editor-fold">


    //<editor-fold desc="requests stuff">

    public List<CRequest> getAllRequests() {
        List<CRequest> requests = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM
                    ec_clan_join_requests
                    """);

            var result = statement.executeQuery();

            while (result.next()) {
                requests.add(new CRequest(
                        result.getInt("id"),
                        result.getInt("clan"),
                        UUID.fromString(result.getString("player_id")),
                        result.getLong("expire_date"),
                        result.getLong("created_on")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }


    public CRequest insertRequest(CRequest request) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO
                    ec_clan_join_requests
                    (clan, player_id, expire_date, created_on)
                    VALUES
                    (?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, request.getClanId());
            statement.setString(2, request.getPlayerUuid().toString());
            statement.setLong(3, request.getExpireTime());
            statement.setLong(4, request.getCreatedTime());
            int rows = statement.executeUpdate();
            if (rows == 0) {
                return null;
            } else {

                var result = statement.getGeneratedKeys();
                if (result.next()) {
                    request.setRequestId(result.getInt(1));
                    return request;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void deleteRequest(CRequest cRequest) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    DELETE FROM
                    ec_clan_join_requests
                    WHERE id = ?
                    """);
            statement.setInt(1, cRequest.getRequestId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //</editor-fold">

    //<editor-fold desc="logs

    public void addLog(Log log){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO
                    ec_logs
                    (message, player_id, clan_id, log_type, created_on)
                    VALUES
                    (?, ?, ?, ?, ?)
                    """);
            statement.setString(1, log.log());
            statement.setString(2, log.player() != null ? log.player().toString() : "");
            statement.setInt(3, log.clan());
            statement.setString(4, log.type().toString());
            statement.setLong(5, System.currentTimeMillis());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    //</editor-fold">


    //<editor-fold desc="notifications">





    //</editor-fold">
}
