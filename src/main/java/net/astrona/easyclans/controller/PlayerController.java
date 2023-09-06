package net.astrona.easyclans.controller;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.models.Cache;
import net.astrona.easyclans.storage.SQLStorage;

import java.util.UUID;

public class PlayerController {
    private final ClansPlugin plugin;
    private final Cache cache;
    private final SQLStorage sqlStorage;

    public PlayerController(ClansPlugin plugin, Cache cache, SQLStorage sqlStorage) {
        this.plugin = plugin;
        this.cache = cache;
        this.sqlStorage = sqlStorage;
    }

    public void addPlayer(UUID uuid) {
        CPlayer clanPlayer = new CPlayer(uuid, null);
        cache.add(clanPlayer);
    }

    public void removePlayer(UUID uuid) {
        cache.remove(uuid);
    }
}
