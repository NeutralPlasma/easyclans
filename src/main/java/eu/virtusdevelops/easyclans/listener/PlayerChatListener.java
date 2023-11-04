package eu.virtusdevelops.easyclans.listener;

import eu.virtusdevelops.easyclans.controller.PlayerController;
import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.Clan;
import io.papermc.paper.event.player.AsyncChatEvent;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.ClansController;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class PlayerChatListener implements Listener {
    private final FileConfiguration config;
    private final PlayerController playerController;
    private final ClansController clansController;

    public PlayerChatListener(FileConfiguration config, PlayerController playerController, ClansController clansController) {
        this.config = config;
        this.playerController = playerController;
        this.clansController = clansController;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        CPlayer cPlayer = playerController.getPlayer(player.getUniqueId());
        Clan clan = clansController.getClan(cPlayer.getClanID());

        if (!cPlayer.isInClubChat() || clan == null) {
            return;
        }


        event.setCancelled(true);

        for (UUID uuid : clan.getMembers()) {
            CPlayer clanPlayer = playerController.getPlayer(uuid);

            if (!clanPlayer.isActive()) {
                continue;
            }

            Player activePlayer = Bukkit.getPlayer(uuid);
            assert activePlayer != null;
            activePlayer.sendMessage(ClansPlugin.MM.deserialize(config.getString("chat-format")
                            .replace("{player}", cPlayer.getName())).replaceText(config -> {
                                config.matchLiteral("{message}").replacement(event.message());
                            })
            );
        }
    }
}
