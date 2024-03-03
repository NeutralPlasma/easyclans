package eu.virtusdevelops.easyclans.models;

public class Notification {
    private final String message;
    private final long created_on;
    private long read_on;

    public Notification(String message, long created_on, long read_on) {
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


}
