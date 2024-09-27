package eu.virtusdevelops.easyclans.storage.mysql;

import com.zaxxer.hikari.HikariDataSource;
import eu.virtusdevelops.easyclans.dao.LogDao;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.storage.mysql.mappers.LogMapperMysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class LogMysql implements LogDao {
    private final Logger logger;
    private final HikariDataSource dataSource;
    private final LogMapperMysql logMapper;

    public LogMysql(Logger logger, HikariDataSource dataSource, LogMapperMysql logMapper) {
        this.logger = logger;
        this.dataSource = dataSource;
        this.logMapper = logMapper;
    }


    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
            """
                    CREATE TABLE IF NOT EXISTS ec_log (
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

    @Override
    public Log getById(Long aLong) {
        try(Connection connection = dataSource.getConnection()){
            var statement = connection.prepareStatement(
          """
                SELECT * FROM ec_log
                 WHERE id = ?
             """);

            var resultSet = statement.executeQuery();
            if(resultSet.next()){
                return logMapper.apply(resultSet);
            }

        }catch (SQLException e) {
            logger.severe("Error getting log: " + aLong);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Log> getAll() {
        List<Log> logs = new ArrayList<>();
        try(Connection connection = dataSource.getConnection()){
            var statement = connection.prepareStatement(
            """
                  SELECT * FROM ec_log
               """);

            var resultSet = statement.executeQuery();
            while(resultSet.next()){
                logs.add(logMapper.apply(resultSet));
            }

        }catch (SQLException e) {
            logger.severe("Error getting logs");
            e.printStackTrace();
        }
        return logs;
    }

    @Override
    public List<Log> getClanLogs(UUID clanId, int amount, int page) {

        List<Log> logs = new ArrayList<>();
        try(Connection connection = dataSource.getConnection()){
            var statement = connection.prepareStatement(
            """
                SELECT *
                FROM ec_log
                WHERE clan_id = ?
                LIMIT ?, ?
               """);

            statement.setObject(1, clanId);
            statement.setInt(2, page*amount);
            statement.setInt(3, amount);

            var resultSet = statement.executeQuery();
            while(resultSet.next()){
                logs.add(logMapper.apply(resultSet));
            }

        }catch (SQLException e) {
            logger.severe("Error getting logs for clan: " + clanId);
            e.printStackTrace();
        }
        return logs;
    }

    @Override
    public List<Log> getPlayerClanLogs(UUID clanId, UUID playerId, int amount, int page) {
        List<Log> logs = new ArrayList<>();
        try(Connection connection = dataSource.getConnection()){
            var statement = connection.prepareStatement(
                    """
                        SELECT *
                        FROM ec_log
                        WHERE clan_id = ?
                        AND player_id = ?
                        LIMIT ?, ?
                       """);

            statement.setObject(1, clanId);
            statement.setObject(2, playerId);
            statement.setInt(3, page*amount);
            statement.setInt(4, amount);

            var resultSet = statement.executeQuery();
            while(resultSet.next()){
                logs.add(logMapper.apply(resultSet));
            }

        }catch (SQLException e) {
            logger.severe("Error getting logs for clan: " + clanId + " player: " + playerId);
            e.printStackTrace();
        }
        return logs;
    }


    @Override
    public Log save(Log log) {
        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statement = connection.prepareStatement(
            """
                INSERT INTO ec_log
                (message, clan_id, player_id, log_type, created_on)
                VALUES
                (?, ?, ?, ?, ?)
                """
            );
            statement.setString(1, log.log());
            statement.setObject(2, log.clan());
            statement.setObject(3, log.player());
            statement.setObject(4, log.type());
            statement.setLong(5, log.timeStamp());

            return statement.executeUpdate() > 0 ? log : null;
        }catch (SQLException e) {
            logger.severe("Failed deleting log: " + log);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(Log log) {
        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statement = connection.prepareStatement(
                    """
                        DELETE FROM ec_log
                        WHERE id = ?
                        """
            );
            statement.setLong(1, log.id());
            return statement.executeUpdate() > 0;
        }catch (SQLException e) {
            logger.severe("Failed deleting log: " + log);
            e.printStackTrace();
        }
        return false;
    }

}
