package eu.virtusdevelops.easyclans.storage.mysql.mappers;

import eu.virtusdevelops.easyclans.models.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;

public class CurrencyMapperMysql implements Function<ResultSet, Currency> {

    @Override
    public Currency apply(ResultSet rs) {
        try{
            return new Currency(
                    rs.getObject("ec_clan_currencies.id", UUID.class),
                    rs.getDouble("ec_clan_currencies.amount"),
                    rs.getString("ec_clan_currencies.name"),
                    rs.getObject("ec_clan_currencies.clan_id", UUID.class)
            );
        }catch (SQLException e){
            return null;
        }
    }
}
