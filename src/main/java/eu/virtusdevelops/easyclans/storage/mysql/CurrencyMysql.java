package eu.virtusdevelops.easyclans.storage.mysql;

import com.zaxxer.hikari.HikariDataSource;
import eu.virtusdevelops.easyclans.dao.CurrencyDao;
import eu.virtusdevelops.easyclans.models.Currency;
import eu.virtusdevelops.easyclans.storage.mysql.mappers.CurrencyMapperMysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class CurrencyMysql implements CurrencyDao {
    private final Logger logger;
    private final HikariDataSource dataSource;

    private final CurrencyMapperMysql mapper;

    public CurrencyMysql(Logger logger, HikariDataSource dataSource, CurrencyMapperMysql mapper) {
        this.logger = logger;
        this.dataSource = dataSource;
        this.mapper = mapper;
    }

    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
            """
                CREATE TABLE IF NOT EXISTS ec_clan_currencies(
                    id UUID PRIMARY KEY,
                    amount DOUBLE,
                    currency_name VARCHAR(128),
                    clan_id VARCHAR(36),
                    FOREIGN KEY (clan_id) REFERENCES ec_clan(id) ON DELETE CASCADE
                )
                """
            );
            statement.executeUpdate();
        }catch (SQLException e) {
            logger.severe("Exception occured while initializing Currencies MYSQL Dao");
            e.printStackTrace();
        }
    }

    @Override
    public Currency getById(UUID uuid) {

        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statement = connection.prepareStatement(
            """
                SELECT *
                FROM ec_clan_currencies
                WHERE id = ?
                """
            );

            var resultSet = statement.executeQuery();
            if(resultSet.next()){
                return mapper.apply(resultSet);
            }
        }catch (SQLException e) {
            logger.severe("Failed getting currency with id: " + uuid);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Currency> getAll() {
        List<Currency> currencies = new ArrayList<>();
        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statement = connection.prepareStatement(
                    """
                        SELECT *
                        FROM ec_clan_currencies
                        """
            );

            var resultSet = statement.executeQuery();

            while(resultSet.next()){
                currencies.add(mapper.apply(resultSet));
            }

        }catch (SQLException e) {
            logger.severe("Failed getting currency");
            e.printStackTrace();
        }
        return currencies;
    }

    @Override
    public Currency save(Currency currency) {
        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statement = connection.prepareStatement(
            """
                INSERT INTO ec_clan_currencies (id, amount, currency_name, clan_id)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    amount = excluded.amount,
                    currency_name = excluded.currency_name,
                    clan_id = excluded.clan_id;
                """
            );

            statement.setObject(1, currency.getId());
            statement.setDouble(2, currency.getValue());
            statement.setString(3, currency.getName());
            statement.setObject(4, currency.getClanId());


            if(statement.executeUpdate() > 0){
                return currency;
            }

        }catch (SQLException e) {
            logger.severe("Failed saving currency with id: " + currency.getId());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(Currency currency) {
        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statement = connection.prepareStatement(
            """
                DELETE FROM ec_clan_currencies
                WHERE id = ?
                """
            );
            statement.setObject(1, currency.getId());
            return statement.executeUpdate() > 0;
        }catch (SQLException e) {
            logger.severe("Failed deleting currency");
            e.printStackTrace();
        }
        return false;
    }
}
