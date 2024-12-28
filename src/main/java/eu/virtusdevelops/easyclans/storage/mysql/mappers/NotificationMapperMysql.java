package eu.virtusdevelops.easyclans.storage.mysql.mappers;

import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.models.LogType;
import eu.virtusdevelops.easyclans.models.Notification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;

public class NotificationMapperMysql implements Function<ResultSet, Notification> {
    @Override
    public Notification apply(ResultSet resultSet) {
        try{
            return new Notification(
                    resultSet.getObject("ec_notification.id", UUID.class),
                    resultSet.getString("ec_notification.message"),
                    resultSet.getLong("ec_notification.created_on"),
                    resultSet.getLong("ec_notification.read_on")
            );
        }catch (SQLException e){
            return null;
        }
    }
}
