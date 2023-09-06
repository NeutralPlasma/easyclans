package net.astrona.easyclans.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cache {
    private final Map<UUID, CPlayer> clanPlayers;
    private final Map<String, Clan> clans;

    public Cache() {
        this.clanPlayers = new HashMap<>();
        this.clans = new HashMap<>();
    }

    public void add(CPlayer clanPlayer) {
        this.clanPlayers.put(clanPlayer.uuid(), clanPlayer);
    }

    public void remove(UUID uuid) {
        this.clanPlayers.remove(uuid);
    }

    public Map<String, Clan> getClans() {
        return clans;
    }

    public Map<UUID, CPlayer> getClanPlayers() {
        return clanPlayers;
    }
}
