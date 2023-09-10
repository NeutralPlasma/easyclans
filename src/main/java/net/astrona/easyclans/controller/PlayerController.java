package net.astrona.easyclans.controller;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.storage.SQLStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerController {
    private final Map<UUID, CPlayer> players;
    private final ClansPlugin plugin;
    private final SQLStorage sqlStorage;

    public PlayerController(ClansPlugin plugin, SQLStorage sqlStorage) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        this.players = new HashMap<>();
    }

    /**
     * Adds new player if it's not cached, so new player.
     * @param uuid the id of the player
     */
    public void addPlayer(UUID uuid) {
        CPlayer clanPlayer = new CPlayer(uuid, null, System.currentTimeMillis());
        players.put(uuid, clanPlayer);
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    /**
     * Retrieves a cached player object based on their UUID.
     *
     * @param uuid the id of the player
     * @return the cached player object associated with the provided UUID,
     *         or null if no player with the given UUID is found in the cache.
     */
    public CPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }
}
