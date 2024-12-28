package eu.virtusdevelops.easyclans.storage.mysql;

import eu.virtusdevelops.easyclans.dao.NotificationDao;
import eu.virtusdevelops.easyclans.models.Notification;

import java.util.List;
import java.util.UUID;

public class NotificationMysql implements NotificationDao {
    @Override
    public void init() {

    }

    @Override
    public Notification getById(UUID uuid) {
        return null;
    }

    @Override
    public List<Notification> getAll() {
        return List.of();
    }

    @Override
    public Notification save(Notification notification) {
        return null;
    }

    @Override
    public boolean delete(Notification notification) {
        return false;
    }
}
