package net.astrona.easyclans.models;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class CPlayer {
    private UUID uuid;
    private Integer clan;
    private long lastActive, joinClanDate;

    public CPlayer(UUID uuid, Integer clan, long lastActive, long joinClanDate) {
        this.uuid = uuid;
        this.clan = clan;
        this.lastActive = lastActive;
        this.joinClanDate = joinClanDate;
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

    public long getJoinClanDate() {
        return joinClanDate;
    }

    public void setJoinClanDate(long joinClanDate) {
        this.joinClanDate = joinClanDate;
    }

    public OfflinePlayer getOfflinePlayer(){
        return Bukkit.getOfflinePlayer(uuid);
    }
}
