package net.astrona.easyclans.models.components.chat;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.LanguageController;
import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.gui.ui.ClanCreateGUI;
import net.astrona.easyclans.models.components.chat.impl.ChatPrompt;
import net.astrona.easyclans.models.components.chat.impl.PlayerChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChangeClanNamePrompt implements ChatPrompt {
    private final ClansPlugin plugin;
    private final String displayName;
    private PlayerController playerController;
    private ClansController clansController;
    private final PlayerChatComponent playerChatComponent;

    public ChangeClanNamePrompt(ClansPlugin plugin, String displayName, PlayerChatComponent playerChatComponent) {
        this.displayName = displayName;
        this.plugin = plugin;
        this.playerChatComponent = playerChatComponent;
    }

    @Override
    public String getPrompt() {
        return LanguageController.getLocalized("create.name.prompt");
    }

    @Override
    public String getRetryPrompt() {
        return LanguageController.getLocalized("create.name.invalid_prompt");
    }

    @Override
    public boolean onInput(Player player, String input) {
        if (!input.isEmpty()) {
            // yes change the name
            player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.name.set")
                    .replace("{name}", input)));

            Bukkit.getScheduler().runTask(plugin, () -> {
                new ClanCreateGUI(input, displayName, player, plugin, playerController, clansController, playerChatComponent);
            });
            return true;
        }
        return false;
    }
}
