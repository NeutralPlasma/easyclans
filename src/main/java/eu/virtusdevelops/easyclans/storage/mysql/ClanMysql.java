package eu.virtusdevelops.easyclans.storage.mysql;

import com.zaxxer.hikari.HikariDataSource;
import eu.virtusdevelops.easyclans.dao.ClanDao;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.storage.mysql.mappers.ClanMysqlMapper;
import eu.virtusdevelops.easyclans.storage.mysql.mappers.CurrencyMapperMysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class ClanMysql implements ClanDao {
    private final Logger logger;
    private final HikariDataSource dataSource;

    private final ClanMysqlMapper clanMysqlMapper;
    private final CurrencyMapperMysql currencyMapperMysql;

    public ClanMysql(Logger logger, HikariDataSource dataSource,
                     ClanMysqlMapper clanMysqlMapper, CurrencyMapperMysql currencyMapperMysql) {
        this.logger = logger;
        this.dataSource = dataSource;
        this.clanMysqlMapper = clanMysqlMapper;
        this.currencyMapperMysql = currencyMapperMysql;
    }


    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                """
                CREATE TABLE IF NOT EXISTS ec_clan(
                    id UUID PRIMARY KEY,
                    owner CHAR(36) NOT NULL,
                    clan_name VARCHAR(16) UNIQUE,
                    display_name TEXT,
                    autokick_time INT,
                    join_points_price INT,
                    join_money_price DOUBLE,
                    interest_rate DOUBLE,
                    banner TEXT,
                    tag VARCHAR(16),
                    pvp_enabled TINYINT,
                    created_on TIMESTAMP,
                    FOREIGN KEY (owner) REFERENCES ec_player(uuid)
                );
                """
            );
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Could not clans table");
            e.printStackTrace();
        }
    }

    @Override
    public Clan getById(UUID uuid) {

        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statement = connection.prepareStatement("""
             SELECT * FROM ec_clan
             LEFT JOIN ec_clan_currencies ON ec_clan.clan_id == ec_clan_currencies.clan_id
             LEFT JOIN ec_player ON ec_player.clan_id = ec_clan.id
             WHERE ec_clan.clan_id == ?
             """
            );
            var rs = statement.executeQuery();

            Clan clan = null;

            while(rs.next()){
                if(clan == null)
                    clan = clanMysqlMapper.apply(rs);

                // add members

                try{
                    UUID member = rs.getObject("ec_player.id", UUID.class);
                    clan.addMember(member);


                }catch (SQLException ignored){}


                // parse currency and add it
                var currency = currencyMapperMysql.apply(rs);
                clan.addCurrency(currency);
            }


            if(rs.next()){
                return clanMysqlMapper.apply(rs);
            }

        }catch (SQLException e){
            logger.severe("Could not get clan: " + uuid.toString());
            e.printStackTrace();

        }
        return null;
    }

    @Override
    public List<Clan> getAll() {
        return List.of();
    }

    @Override
    public Clan save(Clan clan) {
        return null;
    }

    @Override
    public boolean delete(Clan clan) {
        return false;
    }
}
