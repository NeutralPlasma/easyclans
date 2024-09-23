package eu.virtusdevelops.easyclans.storage.mysql.mappers;

import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.Notification;

import java.sql.ResultSet;
import java.util.function.Function;

public class NotificationMapperMysql implements Function<ResultSet, Notification> {
    @Override
    public Notification apply(ResultSet resultSet) {
        return null;
    }
}
