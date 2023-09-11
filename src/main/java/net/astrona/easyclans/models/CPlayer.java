package net.astrona.easyclans.models;

import java.util.UUID;

public class CPlayer {
    private UUID uuid;
    private Integer clan;
    private long lastActive;

    public CPlayer(UUID uuid, Integer clan, long lastActive) {
        this.uuid = uuid;
        this.clan = clan;
        this.lastActive = lastActive;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Integer getClan() {
        return clan;
    }

    public void setClan(Integer clan) {
        this.clan = clan;
    }

    public long getLastActive() {
        return lastActive;
    }

    public void setLastActive(long lastActive) {
        this.lastActive = lastActive;
    }
}
