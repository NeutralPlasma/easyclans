package net.astrona.easyclans.utils;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.astrona.easyclans.ClansPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbstractChatUtil implements Listener {

    private static final List<UUID> registered = new ArrayList<>();

    private final ChatConfirmHandler handler;

    private OnClose onClose = null;
    private Listener listener;

    public AbstractChatUtil(Player player, ChatConfirmHandler hander, JavaPlugin plugin) {
        this.handler = hander;
        player.closeInventory();
        initializeListeners(plugin);
        registered.add(player.getUniqueId());
    }

    public static boolean isRegistered(Player player) {
        return registered.contains(player.getUniqueId());
    }

    public static boolean unregister(Player player) {
        return registered.remove(player.getUniqueId());
    }

    public void initializeListeners(JavaPlugin plugin) {

        this.listener = new Listener() {
            @EventHandler(priority = EventPriority.LOW)
            public void onChat(AsyncChatEvent event) {

                Player player = event.getPlayer();
                if (!AbstractChatUtil.isRegistered(player)) return;

                AbstractChatUtil.unregister(player);
                event.setCancelled(true);

                ChatConfirmEvent chatConfirmEvent = new ChatConfirmEvent(player, ClansPlugin.MM.serialize(event.message()));

                handler.onChat(chatConfirmEvent);

                if (onClose != null) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                            onClose.onClose(), 1L);
                }
                HandlerList.unregisterAll(listener);
            }
        };


        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public void setOnClose(OnClose onClose) {
        this.onClose = onClose;
    }

    public interface ChatConfirmHandler {
        void onChat(ChatConfirmEvent event);
    }

    public interface OnClose {
        void onClose();
    }

    public record ChatConfirmEvent(Player player, String message) {}
}
