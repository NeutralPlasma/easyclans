package net.astrona.easyclans.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GUIListener implements Listener {
    private final Handler handler;
    private final Plugin plugin;


    public GUIListener(Handler handler, Plugin plugin) {
        this.handler = handler;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory() instanceof GUI) {
            handler.addPlayer(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory() instanceof GUI)) return;
        if (!handler.hasPlayer(event.getPlayer().getUniqueId())) return;
        handler.removePlayer(event.getPlayer().getUniqueId());


        //Get our CustomHolder
        GUI gui;
        if (event.getView().getTopInventory().getHolder() instanceof GUI) {
            gui = (GUI) event.getView().getTopInventory().getHolder();
        } else {
            return;
        }
        if (gui != null)
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!handler.hasPlayer(event.getPlayer().getUniqueId()))
                        gui.getCloseActions().forEach(it -> it.execute((Player) event.getPlayer()));
                }
            }.runTaskLater(plugin, 2L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof GUI)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        GUI gui = (GUI) event.getView().getTopInventory().getHolder();

        if (gui == null) return;

        Icon icon = gui.getIcon(event.getRawSlot());
        event.setCancelled(true);
        if (icon == null) return;


        if (event.getClick() == ClickType.LEFT)
            icon.getLeftClickActions().forEach(it -> it.execute(player));

        if (event.getClick() == ClickType.RIGHT)
            icon.getRightClickActions().forEach(it -> it.execute(player));
        if (event.getClick() == ClickType.SHIFT_LEFT)
            icon.getShiftLeftClickActions().forEach(it -> it.execute(player));
        if (event.getClick() == ClickType.SHIFT_RIGHT)
            icon.getShiftRightClickActions().forEach(it -> it.execute(player));

        // TODO
        /*if(event.getClick() == ClickType.LEFT)
            icon.getDragItemActions().forEach(it -> it.execute(player, itemStack));*/


        icon.getClickActions().forEach(it -> it.execute(player));
    }
}
