package net.astrona.easyclans.models.components.chat.impl;

import org.bukkit.entity.Player;

public interface ChatPrompt {
    String getPrompt();
    String getRetryPrompt();
    boolean onInput(Player player, String input);
}
