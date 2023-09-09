package net.astrona.easyclans.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.astrona.easyclans.ClansPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class SQLStorage {

    private Logger logger;
    private HikariDataSource dataSource;

    private boolean isMysql;

    public SQLStorage(ClansPlugin plugin) {
        this.logger = plugin.getLogger();

        ConfigurationSection dbSection = plugin.getConfig().getConfigurationSection("db");

        if (dbSection == null) {
            logger.severe("Database properties not found in 'config.yml'.");
            // probably good to disable plugin?=
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
            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            hikariConfig.setUsername(username);
            hikariConfig.setPassword(password);
            hikariConfig.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=" + SSL);
        } else {
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
            hikariConfig.setDriverClassName("jdbc:sqlite:" + path + "/db.sqlite");
        }

        hikariConfig.setPoolName("EasyClansPlugin");
        hikariConfig.setMaximumPoolSize(60000);
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.addDataSourceProperty("database", database);

        this.dataSource = new HikariDataSource(hikariConfig);

        this.createPlayersTable();
        this.createClansTable();
        this.createClanInvitesTable();
        this.createClanJoinRequestsTable();
        //this.initTables(isMysql);
    }


    private void createPlayersTable(){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                        CREATE TABLE IF NOT EXISTS ec_player_data (
                            uuid CHAR(36) PRIMARY KEY,
                            INT clan,
                            last_active BIGINT
                        );
                        """
            );
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Could not initialize the sql tables!");
            e.printStackTrace();
        }
    }


    private void createClansTable(){
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
                        auto_pay_out_time INT,
                        auto_pay_out_percentage DOUBLE,
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


    private void createClanInvitesTable(){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                        CREATE TABLE IF NOT EXISTS ec_clan_invites (
                            id INTEGER PRIMARY KEY,
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

    private void createClanJoinRequestsTable(){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                        CREATE TABLE IF NOT EXISTS ec_clan_join_requests (
                            id INTEGER PRIMARY KEY,
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
}
