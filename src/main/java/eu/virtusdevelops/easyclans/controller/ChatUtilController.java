package eu.virtusdevelops.easyclans.controller;

import eu.virtusdevelops.easyclans.ClansPlugin;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ChatUtilController implements Listener {
    private final JavaPlugin plugin;
    private final Map<Player, ChatAction> registeredChats = new HashMap<>();


    public ChatUtilController(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    public void unload() {
        HandlerList.unregisterAll(this);
    }


    public void newChat(final Player player, final ChatAction action) {
        registeredChats.remove(player);
        registeredChats.put(player, action);
        player.closeInventory();
    }


    public void removeChat(Player player) {
        registeredChats.remove(player);
    }

    public boolean hasChat(Player player) {
        return registeredChats.containsKey(player);
    }



    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if(!hasChat(player)) {
            return;
        }
        event.setCancelled(true);
        ChatAction action = registeredChats.get(player);
        ChatConfirmEvent chatConfirmEvent = new ChatConfirmEvent(player, ClansPlugin.MM.serialize(event.message()));
        action.chatConfirmHandler.onChat(chatConfirmEvent);

        if(action.onClose != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                    action.onClose.onClose(), 1L);
        }
        removeChat(player);
    }

    public interface ChatConfirmHandler {
        void onChat(ChatConfirmEvent event);
    }

    public interface OnClose {
        void onClose();
    }

    public record ChatConfirmEvent(Player player, String message) {}

    public static class ChatAction{

        protected final OnClose onClose;
        protected final ChatConfirmHandler chatConfirmHandler;

        public ChatAction(ChatConfirmHandler hander) {
            this.chatConfirmHandler = hander;
            this.onClose = null;
        }

        public ChatAction(ChatConfirmHandler hander, OnClose onClose) {
            this.chatConfirmHandler = hander;
            this.onClose = onClose;
        }
    }
}
