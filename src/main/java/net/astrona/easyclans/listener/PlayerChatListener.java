package net.astrona.easyclans.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.models.components.chat.impl.PlayerChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class PlayerChatListener implements Listener {
    private final FileConfiguration config;
    private final PlayerController playerController;
    private final ClansController clansController;
    private final PlayerChatComponent playerChatComponent;

    public PlayerChatListener(FileConfiguration config, PlayerController playerController, ClansController clansController, PlayerChatComponent playerChatComponent) {
        this.config = config;
        this.playerController = playerController;
        this.clansController = clansController;
        this.playerChatComponent = playerChatComponent;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        CPlayer cPlayer = playerController.getPlayer(player.getUniqueId());
        Clan clan = clansController.getClan(cPlayer.getClanID());

        if (playerChatComponent.isPlayerInChatPrompt(player)) {
            playerChatComponent.handlePlayerChat(player, ClansPlugin.MM.serialize(event.message()));
            event.setCancelled(true);
            return;
        }

        if (!cPlayer.isInClubChat() && clan == null) {
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
                    .replace("{player}", cPlayer.getName()))
                    .replaceText(config -> {
                        config.matchLiteral("{message}")
                                .replacement(event.message());
                    })
            );
        }
    }
}
