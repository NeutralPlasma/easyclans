package net.astrona.easyclans.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Handler {
    private final List<UUID> openedInv = new ArrayList<>();
    private final GUIListener listener;

    public Handler(Plugin plugin) {
        this.listener = new GUIListener(this, plugin);
        Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }


    public void addPlayer(UUID player) {
        this.openedInv.add(player);
    }

    public void removePlayer(UUID player) {
        this.openedInv.remove(player);
    }

    public boolean hasPlayer(UUID player) {
        return this.openedInv.contains(player);
    }
}
