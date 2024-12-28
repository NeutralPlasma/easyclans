package eu.virtusdevelops.easyclans.models;

import java.util.UUID;

public class Notification {
    private final UUID id;
    private final String message;
    private final long created_on;
    private long read_on;

    public Notification(UUID id, String message, long created_on, long read_on) {
        this.id = id;
        this.message = message;
        this.created_on = created_on;
        this.read_on = read_on;
    }

    public String getMessage() {
        return message;
    }

    public long getCreatedDate() {
        return created_on;
    }

    public long getReadDate() {
        return read_on;
    }

    public void setIsRead(long read_on) {
        this.read_on = read_on;
    }

    public boolean isRead() {
        return read_on != 0;
    }

    public UUID getId() {
        return id;
    }
}
