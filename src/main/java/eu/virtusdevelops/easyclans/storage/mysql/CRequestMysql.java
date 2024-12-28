package eu.virtusdevelops.easyclans.storage.mysql;

import com.zaxxer.hikari.HikariDataSource;
import eu.virtusdevelops.easyclans.dao.CRequestDao;
import eu.virtusdevelops.easyclans.models.CRequest;
import eu.virtusdevelops.easyclans.storage.mysql.mappers.CRequestMapperMysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class CRequestMysql implements CRequestDao {
    private final Logger logger;
    private final HikariDataSource dataSource;
    private final CRequestMapperMysql mapper;

    public CRequestMysql(Logger logger, HikariDataSource dataSource, CRequestMapperMysql mapper) {
        this.logger = logger;
        this.dataSource = dataSource;
        this.mapper = mapper;
    }

    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                        CREATE TABLE IF NOT EXISTS ec_clan_join_requests (
                            id CHAR(36) PRIMARY KEY,
                            clan_id VARCHAR(36),
                            player_id VARCHAR(36),
                            expire_date TIMESTAMP,
                            created_on TIMESTAMP,
                            FOREIGN KEY (clan_id) REFERENCES ec_clan(id) ON DELETE CASCADE,
                            FOREIGN KEY (player_id) REFERENCES ec_player(uuid) ON DELETE CASCADE
                        )
                        """
            );
            statement.executeUpdate();
        }catch (SQLException e) {
            logger.severe("Exception occurred while initializing requests dao");
            e.printStackTrace();
        }
    }

    @Override
    public CRequest getById(UUID uuid) {

        try{
            PreparedStatement statement = dataSource.getConnection().prepareStatement("""
            SELECT * FROM ec_clan_join_request
                WHERE id = ?
            """);
            statement.setObject(1, uuid);
            var rs = statement.executeQuery();
            if(rs.next())
                return mapper.apply(rs);

        }catch (SQLException e){
            logger.severe("Exception occurred while getting requests dao");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CRequest> getAll() {
        List<CRequest> requests = new ArrayList<>();
        try{
            PreparedStatement statement = dataSource.getConnection().prepareStatement("""
            SELECT * FROM ec_clan_join_request
            """);

            var rs = statement.executeQuery();
            while(rs.next())
                requests.add(mapper.apply(rs));

        }catch (SQLException e){
            logger.severe("Exception occurred while getting requests dao");
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public CRequest save(CRequest cRequest) {
        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statement = connection.prepareStatement(
            """
                INSERT INTO ec_clan_join_request
                (id, clan_id, player_id, expire_date, created_on)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    clan_id = excluded.clan_id,
                    player_id = excluded.player_id,
                    expire_date = excluded.expire_date,
                    created_on = excluded.created_on;
                """
            );

            statement.setObject(1, cRequest.getId());
            statement.setObject(2, cRequest.getClanId());
            statement.setObject(3, cRequest.getPlayerUuid());
            statement.setLong(4, cRequest.getExpireTime());
            statement.setLong(5, cRequest.getCreatedTime());


            if(statement.executeUpdate() > 0){
                return cRequest;
            }

        }catch (SQLException e) {
            logger.severe("Failed saving currency with id: " + cRequest.getId());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(CRequest cRequest) {
        try{
            PreparedStatement statement = dataSource.getConnection().prepareStatement("""
            DELETE FROM ec_clan_join_request
                WHERE id = ?
            """);
            statement.setObject(1, cRequest.getId());

            return statement.executeUpdate() > 0;

        }catch (SQLException e){
            logger.severe("Exception occurred while getting requests dao");
            e.printStackTrace();
        }
        return false;
    }
}
