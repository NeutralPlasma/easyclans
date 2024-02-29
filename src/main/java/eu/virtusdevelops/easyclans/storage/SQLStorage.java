package eu.virtusdevelops.easyclans.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.models.*;
import eu.virtusdevelops.easyclans.models.Currency;
import eu.virtusdevelops.easyclans.utils.Serialization;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        this.createTrophiesTable();

        // dependant tables
        this.createPermissionsTable();
        this.createClanTrophytable();
        this.createClanInvitesTable();
        this.createClanJoinRequestsTable();
        this.createCurrenciesTable();

        this.createLogsTable();
    }


    // <editor-fold desc="Table creation">
    private void createPlayersTable() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            CREATE TABLE IF NOT EXISTS ec_player_data (
                                uuid CHAR(36) PRIMARY KEY,
                                clan_id CHAR(36),
                                last_active BIGINT,
                                joined_clan BIGINT,
                                name TEXT,
                                rank TEXT
                            );
                            """
            );
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not players table");
            e.printStackTrace();
        }
    }
    private void createPermissionsTable(){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                        CREATE TABLE IF NOT EXISTS ec_permissions (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            player_id VARCHAR(36),
                            permission VARCHAR(128),
                            FOREIGN KEY (player_id) REFERENCES ec_player_data(uuid) ON DELETE CASCADE
                        );
                        """
            );
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not permissions table");
            //logger.severe(e.getMessage());
            e.printStackTrace();
        }
    }
    private void createClansTable() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            CREATE TABLE IF NOT EXISTS ec_clan_data(
                                id CHAR(36) PRIMARY KEY,
                                owner CHAR(36) NOT NULL,
                                clan_name VARCHAR(16) UNIQUE,
                                display_name TEXT,
                                autokick_time INT,
                                join_points_price INT,
                                join_money_price DOUBLE,
                                interest_rate DOUBLE,
                                banner TEXT,
                                tag VARCHAR(16),
                                pvp_enabled TINYINT(1),
                                created_on DATETIME,
                                FOREIGN KEY (owner) REFERENCES ec_player_data(uuid)
                            );
                            """//.formatted(autoIncrement)
            );
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not clans table");
            e.printStackTrace();
        }
    }

    private void createCurrenciesTable() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS ec_clan_currencies(
                    id CHAR(36) PRIMARY KEY,
                    amount DOUBLE,
                    currency_name VARCHAR(128),
                    clan_id VARCHAR(36),
                    
                    FOREIGN KEY (clan_id) REFERENCES ec_clan_data(id) ON DELETE CASCADE
                    )
                    """);
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not currencies table");
            e.printStackTrace();
        }

    }

    private void createClanInvitesTable() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            CREATE TABLE IF NOT EXISTS ec_clan_invites (
                                id CHAR(36) PRIMARY KEY,
                                clan_id VARCHAR(36),
                                player_id VARCHAR(36),
                                expire_date BIGINT,
                                created_on BIGINT,
                                
                                FOREIGN KEY (clan_id) REFERENCES ec_clan_data(id) ON DELETE CASCADE,
                                FOREIGN KEY (player_id) REFERENCES ec_player_data(uuid) ON DELETE CASCADE
                            );
                            """
            );
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not invites table");
            e.printStackTrace();
        }
    }

    private void createLogsTable() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            CREATE TABLE IF NOT EXISTS ec_logs (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                message TEXT,
                                clan_id VARCHAR(36),
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
            logger.severe("Could not logs table");
            e.printStackTrace();
        }
    }

    private void createClanJoinRequestsTable() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            CREATE TABLE IF NOT EXISTS ec_clan_join_requests (
                                id CHAR(36) PRIMARY KEY,
                                clan_id VARCHAR(36),
                                player_id VARCHAR(36),
                                expire_date BIGINT,
                                created_on BIGINT,
                          
                                FOREIGN KEY (clan_id) REFERENCES ec_clan_data(id) ON DELETE CASCADE,
                                FOREIGN KEY (player_id) REFERENCES ec_player_data(uuid) ON DELETE CASCADE
                            );
                            """
            );
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not requests table");
            e.printStackTrace();
        }
    }

    private void createTrophiesTable() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            CREATE TABLE IF NOT EXISTS ec_trophy (
                                id CHAR(36) PRIMARY KEY,
                                trophy_name VARCHAR(128),
                                title VARCHAR(128),
                                start_date BIGINT,
                                end_date BIGINT,
                                description TEXT
                            );
                            """
            );
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not trophies table");
            e.printStackTrace();
        }
    }


    private void createClanTrophytable(){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            CREATE TABLE IF NOT EXISTS ec_clan_trophy (
                                id CHAR(36) PRIMARY KEY,
                                clan_id CHAR(36),
                                trophy_id CHAR(36),
                                ranking INT,
                                achieve_date BIGINT,
                                FOREIGN KEY (clan_id) REFERENCES ec_clan_data(id) ON DELETE CASCADE,
                                FOREIGN KEY (trophy_id) REFERENCES ec_trophy(id) ON DELETE CASCADE
                            );
                            """
            );
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not clan trophies table");
            e.printStackTrace();
        }
    }

    //</editor-fold>

    //<editor-fold desc="player stuff">
    public void insertPlayer(CPlayer cPlayer) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO ec_player_data
                    (uuid, clan_id, last_active, joined_clan, name, rank)
                    VALUES
                    (?, ?, ?, ?, ?, ?)
                    """);
            statement.setString(1, cPlayer.getUuid().toString());
            statement.setString(2, cPlayer.getClanID() != null ? cPlayer.getClanID().toString(): null);
            statement.setLong(3, cPlayer.getLastActive());
            statement.setLong(4, cPlayer.getJoinClanDate());
            statement.setString(5, cPlayer.getName());
            statement.setString(6, cPlayer.getRank());
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
                    clan_id = ?,
                    last_active = ?,
                    joined_clan = ?,
                    rank = ?
                    WHERE uuid = ?
                    """);
            statement.setString(1, cPlayer.getClanID() != null ? cPlayer.getClanID().toString() : null);
            statement.setLong(2, cPlayer.getLastActive());
            statement.setLong(3, cPlayer.getJoinClanDate());
            statement.setString(4, cPlayer.getRank());
            statement.setString(5, cPlayer.getUuid() != null ? cPlayer.getUuid().toString(): null);
            statement.execute();

            updatePlayerPermissions(cPlayer);

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
                        UUID.fromString(result.getString("clan_id")),
                        result.getLong("last_active"),
                        result.getLong("joined_clan"),
                        result.getString("name"),
                        result.getString("rank")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updatePlayerPermissions(CPlayer cPlayer){
        try (Connection connection = dataSource.getConnection()) {

            var st_statement = String.format("""
                    DELETE FROM
                    ec_permissions
                    WHERE ec_permissions.player_id = ?
                    AND ec_permissions.permission NOT IN (%s)
                    """,
                    cPlayer.getUserPermissionsList()
                            .stream().map(it -> "?")
                            .collect(Collectors.joining(", "))
            );

            PreparedStatement statement = connection.prepareStatement(st_statement);



            statement.setString(1, cPlayer.getUuid().toString());
            int index = 2;
            for(var perm : cPlayer.getUserPermissionsList()){
                statement.setString(index, perm.name());
                index++;
            }
            statement.execute();
            for(var permission : cPlayer.getUserPermissionsList()){
                PreparedStatement statement2 = connection.prepareStatement("""
                INSERT INTO ec_permissions (permission, player_id)
                SELECT ?, ?
                WHERE NOT EXISTS (
                    SELECT *
                    FROM ec_permissions
                    WHERE permission = ?
                    AND player_id = ?
                )
                """);
                statement2.setString(1, permission.name());
                statement2.setString(2, cPlayer.getUuid().toString());
                statement2.setString(3, permission.name());
                statement2.setString(4, cPlayer.getUuid().toString());
                statement2.execute();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<UserPermissions> getAllPlayerPermissions(UUID player){
        List<UserPermissions> permissions = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                    SELECT permission as 'permission' FROM
                    ec_permissions
                    WHERE ec_permissions.player_id = ?
                    """);
            statement.setString(1, player.toString());
            var result = statement.executeQuery();
            while (result.next()) {
                permissions.add(UserPermissions.valueOf(result.getString("permission")));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permissions;
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
                var cPlayer = new CPlayer(
                        UUID.fromString(result.getString("uuid")),
                        result.getString("clan_id") == null ? null : UUID.fromString(result.getString("clan_id")),
                        result.getLong("last_active"),
                        result.getLong("joined_clan"),
                        result.getString("name"),
                        result.getString("rank")
                );
                var permissions = getAllPlayerPermissions(cPlayer.getUuid());
                cPlayer.setUserPermissionsList(permissions);
                players.add(cPlayer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }


    //</editor-fold>

    //<editor-fold desc="clan stuff">

    private List<UUID> getClanMembers(UUID clan_id) {
        List<UUID> members = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT uuid FROM
                    ec_player_data
                    WHERE clan_id = ?
                    """);
            statement.setString(1, clan_id.toString());

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

    private List<eu.virtusdevelops.easyclans.models.Currency> getClanCurrencies(UUID clan_id) {
        List<eu.virtusdevelops.easyclans.models.Currency> currencies = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM
                    ec_clan_currencies
                    WHERE clan_id = ?
                    """);
            statement.setString(1, clan_id.toString());

            var result = statement.executeQuery();
            while (result.next()) {
                currencies.add(new eu.virtusdevelops.easyclans.models.Currency(
                        UUID.fromString(result.getString("id")),
                        result.getDouble("amount"),
                        result.getString("currency_name"),
                        clan_id
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currencies;
    }

    public void updateClanCurrencies(Clan clan){
        try (Connection connection = dataSource.getConnection()) {
            for (var currency : clan.getCurrencies()) {
                PreparedStatement statement = connection.prepareStatement(
                        """
                                UPDATE ec_clan_currencies
                                SET amount = ?
                                WHERE clan_id = ?
                                AND id = ?
                            """);

                statement.setDouble(1, currency.getValue());
                statement.setString(2, clan.getId().toString());
                statement.setString(3, currency.getId().toString());
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean insertSingleClanCurrency(Currency currency){
        boolean success = true;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            INSERT INTO ec_clan_currencies
                                (id, clan_id, amount, currency_name)
                            VALUES
                                (?, ?, ?, ?)
                        """);

            statement.setString(1, currency.getId().toString());
            statement.setString(2, currency.getClanId().toString());
            statement.setDouble(3, currency.getValue());
            statement.setString(4, currency.getName());
            int rows = statement.executeUpdate();
            if (rows != 0) {
                var result = statement.getGeneratedKeys();

            }else{
                success = false;
            }

        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return success;
    }
    public boolean insertClanCurrencies(Clan clan) {
        boolean success = true;
        try (Connection connection = dataSource.getConnection()) {
            for (var currency : clan.getCurrencies()) {
                PreparedStatement statement = connection.prepareStatement(
                        """
                                INSERT INTO ec_clan_currencies
                                    (id, clan_id, amount, currency_name)
                                VALUES
                                    (?, ?, ?, ?)
                            """);

                statement.setString(1, currency.getId().toString());
                statement.setString(2, currency.getClanId().toString());
                statement.setDouble(3, currency.getValue());
                statement.setString(4, currency.getName());
                int rows = statement.executeUpdate();
                if (rows != 0) {
                    var result = statement.getGeneratedKeys();
                }else{
                    success = false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return success;
    }

    private void deleteClanCurrencies(Clan clan){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            DELETE FROM ec_clan_currencies
                            WHERE clan_id = ?
                        """);

            statement.setString(1, clan.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                        UUID.fromString(result.getString("id")),
                        UUID.fromString(result.getString("owner")),
                        result.getString("clan_name"),
                        result.getString("display_name"),
                        result.getInt("autokick_time"),
                        result.getInt("join_points_price"),
                        result.getDouble("join_money_price"),
                        Serialization.decodeItemBase64(result.getString("banner")),
                        result.getDouble("interest_rate"),
                        result.getString("tag"),
                        null,
                        result.getBoolean("pvp_enabled"),
                        result.getLong("created_on")
                );
                clan.setMembers(getClanMembers(clan.getId()));
                clan.setCurrencies(getClanCurrencies(clan.getId()));
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
                    interest_rate = ?,
                    tag = ?,
                    pvp_enabled = ?
                    WHERE id = ?
                    """);
            statement.setString(1, clan.getOwner().toString());
            statement.setString(2, clan.getName());
            statement.setString(3, clan.getDisplayName());
            statement.setInt(4, clan.getAutoKickTime());
            statement.setInt(5, clan.getJoinPointsPrice());
            statement.setDouble(6, clan.getJoinMoneyPrice());
            statement.setString(7, Serialization.encodeItemBase64(clan.getBanner()));
            statement.setDouble(8, clan.getInterestRate());
            statement.setString(9, clan.getTag());
            statement.setBoolean(10, clan.isPvpEnabled());
            statement.setString(11, clan.getId().toString());

            statement.executeUpdate();
            updateClanCurrencies(clan);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean saveClan(Clan clan) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO ec_clan_data
                    (
                    id,
                    owner,
                    clan_name,
                    display_name,
                    autokick_time,
                    join_points_price,
                    join_money_price,
                    banner,
                    interest_rate,
                    tag,
                    pvp_enabled,
                    created_on
                    )
                    VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """);
            statement.setString(1, clan.getId().toString());
            statement.setString(2, clan.getOwner().toString());
            statement.setString(3, clan.getName());
            statement.setString(4, clan.getDisplayName());
            statement.setInt(5, clan.getAutoKickTime());
            statement.setInt(6, clan.getJoinPointsPrice());
            statement.setDouble(7, clan.getJoinMoneyPrice());
            statement.setString(8, Serialization.encodeItemBase64(clan.getBanner()));
            statement.setDouble(9, clan.getInterestRate());
            statement.setString(10, clan.getTag());
            statement.setBoolean(11, clan.isPvpEnabled());
            statement.setLong(12, clan.getCreatedOn());

            int rows = statement.executeUpdate();
            if (rows > 0) {
                var result = statement.getGeneratedKeys();
                if (result.next()) {
                    insertClanCurrencies(clan);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void deleteClan(Clan clan) {
        try (Connection connection = dataSource.getConnection()) {
            deleteClanCurrencies(clan);
            PreparedStatement statement = connection.prepareStatement("""
                    DELETE FROM
                    ec_clan_data
                    WHERE id = ?
                    """);

            statement.setString(1, clan.getId().toString());
            statement.execute();
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
                        UUID.fromString(result.getString("id")),
                        UUID.fromString(result.getString("clan_id")),
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


    public boolean insertRequest(CRequest request) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO
                    ec_clan_join_requests
                    (id, clan_id, player_id, expire_date, created_on)
                    VALUES
                    (?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, request.getRequestId().toString());
            statement.setString(2, request.getClanId().toString());
            statement.setString(3, request.getPlayerUuid().toString());
            statement.setLong(4, request.getExpireTime());
            statement.setLong(5, request.getCreatedTime());
            int rows = statement.executeUpdate();
            if (rows != 0) {
                var result = statement.getGeneratedKeys();
                if (result.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warning("Could not insert new clan join request into the database: " + request.toString());
        }
        return false;
    }


    public void deleteRequest(CRequest cRequest) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    DELETE FROM
                    ec_clan_join_requests
                    WHERE id = ?
                    """);
            statement.setString(1, cRequest.getRequestId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warning("Could not delete clan join request: " + cRequest.toString());
        }
    }


    //</editor-fold">

    //<editor-fold desc="logs

    public void addLog(Log log) {
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
            statement.setString(3, log.clan().toString());
            statement.setString(4, log.type().toString());
            statement.setLong(5, log.timeStamp());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     *  Fetches logs based on parameters :3
     *
     * @param page which page
     * @param perPage how many logs per page
     * @param clanID clanID can be -1 (if -1 it fetches based on player or all logs if player is also null)
     * @param playerUUID playerUUID can be null (if null it fetches based on clan or all if clan is also -1)
     * @return returns list of logs
     */
    public List<Log> getLogs(int page, int perPage, UUID clanID, UUID playerUUID){
        List<Log> logs = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement;
            if(clanID != null && playerUUID != null){
                statement = connection.prepareStatement("""
                    SELECT * FROM
                    ec_logs
                    WHERE clan_id = ?
                    AND player_id = ?
                    LIMIT ?, ?
                    """);

                statement.setString(1, clanID.toString());
                statement.setString(2, playerUUID.toString());
                statement.setInt(3, page*perPage);
                statement.setInt(4, perPage);
            }else if(clanID != null){
                statement = connection.prepareStatement("""
                    SELECT * FROM
                    ec_logs
                    WHERE clan_id = ?
                    LIMIT ?, ?
                    """);
                statement.setString(1, clanID.toString());
                statement.setInt(2, page*perPage);
                statement.setInt(3, perPage);
            }else if(playerUUID != null){
                statement = connection.prepareStatement("""
                    SELECT * FROM
                    ec_logs
                    WHERE player_id = ?
                    LIMIT ?, ?
                    """);

                statement.setString(1, playerUUID.toString());
                statement.setInt(2, page*perPage);
                statement.setInt(3, perPage);
            }else{
                statement = connection.prepareStatement("""
                    SELECT * FROM
                    ec_logs
                    LIMIT ?, ?
                    """);
                statement.setInt(1, page*perPage);
                statement.setInt(2, perPage);
            }
            var result = statement.executeQuery();
            while (result.next()) {
                UUID uuid = result.getString("player_id").isEmpty() ? null : UUID.fromString(result.getString("player_id"));
                logs.add(new Log(
                        result.getString("message"),
                        uuid,
                        UUID.fromString(result.getString("clan_id")),
                        LogType.valueOf(result.getString("log_type")),
                        result.getLong("created_on")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;

    }
    public List<Log> getPlayerLogs(int page, int perPage, UUID player){
        return getLogs(page, perPage, null, player);
    }
    public List<Log> getClanLogs(int page, int perPage, UUID clanID){
        return getLogs(page, perPage, clanID, null);
    }
    public List<Log> getLogs(int page, int perPage){
        return getLogs(page, perPage, null, null);
    }


    public int getLogsCount(UUID clanID, UUID playerUUID){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement;
            if(clanID != null && playerUUID != null){
                statement = connection.prepareStatement("""
                    SELECT COUNT(*) FROM
                    ec_logs
                    WHERE clan_id = ?
                    AND player_id = ?
                    """);

                statement.setString(1, clanID.toString());
                statement.setString(2, playerUUID.toString());
            }else if(clanID != null){
                statement = connection.prepareStatement("""
                    SELECT COUNT(*) FROM
                    ec_logs
                    WHERE clan_id = ?
                    """);
                statement.setString(1, clanID.toString());
            }else if(playerUUID != null){
                statement = connection.prepareStatement("""
                    SELECT COUNT(*) FROM
                    ec_logs
                    WHERE player_id = ?
                    """);
                statement.setString(1, playerUUID.toString());
            }else{
                statement = connection.prepareStatement("""
                    SELECT COUNT(*) FROM
                    ec_logs
                    """);
            }
            var result = statement.executeQuery();
            if(result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    //</editor-fold">


    //<editor-fold desc="trophies">

    public List<Trophy> loadAllTrophies(){
        List<Trophy> trophies = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM
                    ec_trophy
                    """);
            var results = statement.executeQuery();

            while(results.next()){

                Trophy trophy = new Trophy(
                        UUID.fromString(results.getString("id")),
                        results.getString("trophy_name"),
                        results.getString("title"),
                        results.getString("description"),
                        results.getLong("start_date"),
                        results.getLong("end_date")
                );

                PreparedStatement statement2 = connection.prepareStatement("""
                    SELECT * FROM
                        ec_clan_trophy
                    WHERE
                        trophy_id = ?
                    """);
                statement2.setString(1, trophy.getId().toString());

                var ctResults = statement2.executeQuery();
                while(ctResults.next()){
                    trophy.addTrophy(new ClanTrophy(
                            UUID.fromString(ctResults.getString("id")),
                            UUID.fromString(ctResults.getString("clan_id")),
                            trophy.getId(),
                            ctResults.getInt("ranking"),
                            ctResults.getLong("achieve_date")
                    ));
                }
                trophies.add(trophy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return trophies;
        }
        return trophies;
    }

    public boolean saveTrophy(Trophy trophy){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO
                    ec_trophy
                    (id, trophy_name, title, description, start_date, end_date)
                    VALUES
                    (?, ?, ?, ?, ?, ?)
                    """);
            statement.setString(1, trophy.getId().toString());
            statement.setString(2, trophy.getName());
            statement.setString(3, trophy.getTitle());
            statement.setString(4, trophy.getDescription());
            statement.setLong(5, trophy.getStartDate());
            statement.setLong(6, trophy.getEndDate());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteTropyh(Trophy trophy){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement2 = connection.prepareStatement("""
                DELETE FROM ec_trophy
                WHERE
                id = ?
                """);
            statement2.setString(1, trophy.getId().toString());
            statement2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean removeClanFromTrophy(ClanTrophy trophy){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement2 = connection.prepareStatement("""
                DELETE FROM ec_clan_trophy
                WHERE
                id = ?
                """);
            statement2.setString(1, trophy.getId().toString());
            statement2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addClanToTrophy(ClanTrophy trophy){
        try (Connection connection = dataSource.getConnection()) {
            // insert or update
            PreparedStatement statement2 = connection.prepareStatement("""
            INSERT INTO ec_clan_trophy (id, clan_id, trophy_id, ranking, achieve_date)
            VALUES(?, ?, ?, ?, ?)
            """);
            statement2.setString(1, trophy.getId().toString());
            statement2.setString(2, trophy.getClanID().toString());
            statement2.setString(3, trophy.getTrophyID().toString());
            statement2.setInt(4, trophy.getRanking());
            statement2.setLong(5, trophy.getAchievedDate());
            statement2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean insertTrophyClans(Trophy trophy){
        try (Connection connection = dataSource.getConnection()) {
            for(var cTrophy : trophy.getOrganizedTrophies().values()){
                // insert or update
                PreparedStatement statement2 = connection.prepareStatement("""
                INSERT INTO ec_clan_trophy (id, clan_id, trophy_id, ranking, achieve_date)
                SELECT ?, ?, ?, ?, ?
                WHERE NOT EXISTS (
                    SELECT *
                    FROM ec_clan_trophy
                    id = ?
                )
                """);
                statement2.setString(1, cTrophy.getId().toString());
                statement2.setString(2, cTrophy.getClanID().toString());
                statement2.setString(3, trophy.getId().toString());
                statement2.setInt(4, cTrophy.getRanking());
                statement2.setLong(5, cTrophy.getAchievedDate());
                statement2.setString(6, cTrophy.getId().toString());
                statement2.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateTrophy(Trophy trophy){

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE ec_trophy
                    SET
                    title = ?,
                    name = ?
                    description = ?,
                    start_date = ?,
                    end_date = ?
                    WHERE id = ?
                    """);
            statement.setString(1, trophy.getTitle());
            statement.setString(2, trophy.getName());
            statement.setString(3, trophy.getDescription());
            statement.setLong(4, trophy.getStartDate());
            statement.setLong(5, trophy.getEndDate());
            statement.setString(6, trophy.getId().toString());
            statement.execute();
            insertTrophyClans(trophy);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    //</editor-fold">
}
