package eu.virtusdevelops.easyclans.listener;

import eu.virtusdevelops.easyclans.controller.PlayerController;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.models.CPlayer;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        CPlayer cPlayer = this.playerController.getPlayer(event.getPlayer().getUniqueId());
        if (cPlayer == null) {
            cPlayer = this.playerController.createPlayer(event.getPlayer());
            cPlayer.setActive(true);
            cPlayer.setLastActive(System.currentTimeMillis());
        } else {
            cPlayer.setActive(true);
            cPlayer.setLastActive(System.currentTimeMillis());
            User user = ClansPlugin.Ranks.getPlayerAdapter(Player.class).getUser(event.getPlayer());
            cPlayer.setRank(user.getPrimaryGroup());
            playerController.updatePlayer(cPlayer);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CPlayer cPlayer = this.playerController.getPlayer(event.getPlayer().getUniqueId());
        cPlayer.setLastActive(System.currentTimeMillis());
        cPlayer.setActive(false);
        this.playerController.updatePlayer(cPlayer);
        //this.playerController.removePlayer(event.getPlayer().getUniqueId());
    }
}
