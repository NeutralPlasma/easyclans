package net.astrona.easyclans.models.components.chat.impl;

import net.astrona.easyclans.ClansPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerChatComponent {
    private final Map<UUID, ChatPrompt> chatPromptMap = new HashMap<>();

    public void startChatPrompt(Player player, ChatPrompt prompt) {
        chatPromptMap.put(player.getUniqueId(), prompt);
        player.sendMessage(ClansPlugin.MM.deserialize(prompt.getPrompt()));
    }

    public void handlePlayerChat(Player player, String message) {
        UUID playerUUID = player.getUniqueId();

        if (chatPromptMap.containsKey(playerUUID)) {
            ChatPrompt prompt = chatPromptMap.get(playerUUID);

            if (prompt != null) {
                if (prompt.onInput(player, message)) {
                    chatPromptMap.remove(playerUUID);
                } else {
                    player.sendMessage(ClansPlugin.MM.deserialize(prompt.getRetryPrompt()));
                }
            }
        }
    }

    public boolean isPlayerInChatPrompt(Player player) {
        return chatPromptMap.containsKey(player.getUniqueId());
    }

    public void cancelChatPrompt(Player player) {
        chatPromptMap.remove(player.getUniqueId());
    }
}
