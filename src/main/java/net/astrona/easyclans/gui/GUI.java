package net.astrona.easyclans.gui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.gui.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI implements InventoryHolder {

    private List<Action> closeActions = new ArrayList<>();
    private final Map<Integer, Icon> icons = new HashMap<>();
    private final int size;
    private String title;

    public GUI(int size, String title) {
        this.size = size;
        this.title = title;
    }

    public void setIcon(int pos, Icon icon) {
        this.icons.put(pos, icon);
    }

    public Icon getIcon(int pos) {
        return this.icons.get(pos);
    }

    public List<Action> getCloseActions() {
        return closeActions;
    }

    public void open(Player player) {
        player.openInventory(getInventory());
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, this.size, ClansPlugin.MM.deserialize(this.title));
        for (Map.Entry<Integer, Icon> entry : this.icons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().itemStack);
        }
        return inventory;
    }
}
