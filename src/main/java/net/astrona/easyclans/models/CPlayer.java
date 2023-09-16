package net.astrona.easyclans.models;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.units.qual.C;

import java.util.UUID;

public class CPlayer {
    private String name;
    private UUID uuid;
    private int clanId;
    private long lastActive, joinClanDate;
    private boolean isActive = false;
    private boolean inClubChat = false;

    public CPlayer(UUID uuid, int clanId, long lastActive, long joinClanDate, String name) {
        this.uuid = uuid;
        this.clanId = clanId;
        this.lastActive = lastActive;
        this.joinClanDate = joinClanDate;
        this.name = name;
    }

    public boolean isInClubChat() {
        return inClubChat;
    }

    public void setInClubChat(boolean inClubChat) {
        this.inClubChat = inClubChat;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getClanID() {
        return clanId;
    }

    public void setClanID(int clan_id) {
        this.clanId = clan_id;
    }

    public void removeFromClan() {
        this.clanId = -1;
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

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
