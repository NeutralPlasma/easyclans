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
    private final Logger logger;
    private HikariDataSource dataSource;

    public SQLStorage(ClansPlugin plugin, Logger logger, FileConfiguration config) {
        this.logger = logger;

        ConfigurationSection dbSection = config.getConfigurationSection("db");

        if (dbSection == null) {
            logger.severe("Database properties not found in 'config.yml'.");
            return;
        }

        boolean mySQL = dbSection.getBoolean("mysql");
        String username = config.getString("username");
        String password = config.getString("password");
        String hostname = config.getString("ip");
        int port = config.getInt("port");
        String database = config.getString("database");
        boolean SSL = config.getBoolean("ssl");
        String path = plugin.getDataFolder().getPath();

        HikariConfig hikariConfig = new HikariConfig();
        if (mySQL) {
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
        this.initTables(mySQL);
    }

    private void initTables(boolean mySQL) {
        String autoIncrement = mySQL ? "AUTO_INCREMENT" : "AUTOINCREMENT";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement firstStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS ec_player_data (uuid CHAR(36) PRIMARY KEY, INT clan);"
            );
            PreparedStatement secondStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS ec_clan_data (id INT PRIMARY KEY " + autoIncrement + ", name VARCHAR(16), display_name TEXT" + ");"
            );

            firstStatement.execute();
            secondStatement.execute();
        } catch (SQLException e) {
            logger.severe("Could not initialize the sql tables!");
            e.printStackTrace();
        }
    }
}
