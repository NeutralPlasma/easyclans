package net.astrona.easyclans.listener;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.PlayerController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    private final ClansPlugin plugin;
    private final PlayerController playerController;

    public PlayerConnectionListener(ClansPlugin plugin, PlayerController playerController) {
        this.plugin = plugin;
        this.playerController = playerController;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.playerController.addPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.playerController.removePlayer(event.getPlayer().getUniqueId());
    }


}
