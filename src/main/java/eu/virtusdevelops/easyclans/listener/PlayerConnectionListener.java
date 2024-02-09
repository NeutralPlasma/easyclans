package eu.virtusdevelops.easyclans.listener;

import eu.virtusdevelops.easyclans.controller.ClansController;
import eu.virtusdevelops.easyclans.controller.PlayerController;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.RanksController;
import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.RankMultiplyer;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayerConnectionListener implements Listener {
    private final ClansPlugin plugin;
    private final PlayerController playerController;
    private final RanksController ranksController;



    public PlayerConnectionListener(ClansPlugin plugin, PlayerController playerController, RanksController ranksController) {
        this.plugin = plugin;
        this.playerController = playerController;
        this.ranksController = ranksController;
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

            RankMultiplyer rank = ranksController.parsePlayerRank(event.getPlayer());
            if(rank == null){
                cPlayer.setRank("null");
            }else{
                cPlayer.setRank(rank.getName());
            }
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
