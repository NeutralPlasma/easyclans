package net.astrona.easyclans.models.components.chat;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.LanguageController;
import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.controller.RequestsController;
import net.astrona.easyclans.gui.ui.ClanCreateGUI;
import net.astrona.easyclans.models.components.chat.impl.ChatPrompt;
import net.astrona.easyclans.models.components.chat.impl.PlayerChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChangeClanNamePrompt implements ChatPrompt {
    private final ClansPlugin plugin;
    private final String displayName;
    private final ItemStack banner;
    private PlayerController playerController;
    private ClansController clansController;
    private RequestsController requestsController;
    private final PlayerChatComponent playerChatComponent;

    public ChangeClanNamePrompt(ClansPlugin plugin, String displayName, ItemStack banner, PlayerController playerController,
                                ClansController clansController, RequestsController requestsController, PlayerChatComponent playerChatComponent) {
        this.plugin = plugin;
        this.displayName = displayName;
        this.banner = banner;
        this.playerController = playerController;
        this.clansController = clansController;
        this.requestsController = requestsController;
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
                new ClanCreateGUI(input, displayName, banner, player, plugin, playerController, clansController, requestsController, playerChatComponent);
            });
            return true;
        }
        return false;
    }
}
