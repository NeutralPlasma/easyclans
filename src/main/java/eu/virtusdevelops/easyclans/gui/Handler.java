package eu.virtusdevelops.easyclans.gui;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Handler {
    private final Plugin plugin;
    private final List<UUID> openedInv = new ArrayList<>();
    private GUIListener listener;

    public Handler(Plugin plugin) {
        this.plugin = plugin;

    }

    public void init(){
        this.listener = new GUIListener(this, plugin);
        Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void disable(){
        HandlerList.unregisterAll(listener);
    }

    public void addPlayer(UUID player) {
        if (!openedInv.contains(player))
            this.openedInv.add(player);
    }

    public void removePlayer(UUID player) {
        this.openedInv.remove(player);
    }

    public boolean hasPlayer(UUID player) {
        return this.openedInv.contains(player);
    }
}
