package eu.virtusdevelops.easyclans.gui;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.gui.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GUI implements InventoryHolder {
    protected final Player player;
    protected List<Action> closeActions = new ArrayList<>();

    protected final Map<Integer, Icon> activeIcons = new HashMap<>();
    protected final Map<Integer, List<Icon>> actualIcons = new HashMap<>();
    protected final int size;
    protected String title;
    protected boolean forceClose = false;
    protected List<Integer> noBackgroundSlots = new ArrayList<>();

    public GUI(Player player, int size, String title) {
        this.player = player;
        this.size = size;
        this.title = title;
    }

    public GUI(Player player,int size, String title, List<Integer> noBackgroundSlots) {
        this.player = player;
        this.size = size;
        this.title = title;
        this.noBackgroundSlots = noBackgroundSlots;
    }

    public void addIcon(int pos, Icon icon) {
        this.actualIcons.computeIfAbsent(pos, k -> new ArrayList<>());
        this.actualIcons.get(pos).add(icon);
    }

    public void setIcon(int pos, Icon icon) {
        this.actualIcons.computeIfAbsent(pos, k -> new ArrayList<>());
        this.actualIcons.get(pos).clear();
        this.actualIcons.get(pos).add(icon);
    }
    /*public void setIcon(int pos, Icon icon) {
        this.icons.put(pos, icon);
    }*/

    public Icon getIcon(int pos) {
        return this.activeIcons.get(pos);
    }

    public void addCloseAction(Action action) {
        this.closeActions.add(action);
    }

    public List<Action> getCloseActions() {
        return closeActions;
    }

    public void open() {
        player.openInventory(getInventory());
    }

    public void setBackground(ItemStack itemStack) {
        Icon icon = new Icon(itemStack);
        for (int x = 0; x < size; x++) {
            addIcon(x, icon);
        }
    }

    public void fancyBackground() {
        var stack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        var stack2 = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);

        var icon1 = new Icon(stack, 0);
        var icon2 = new Icon(stack2, 0);


        // set items in inventory
        addIcon(0, icon2);
        addIcon(1, icon2);
        addIcon(7, icon2);
        addIcon(8, icon2);
        addIcon(9, icon2);
        addIcon(17, icon2);

        int start = 20;
        if (size < 27) {
            start = -100;
        } else if (size < 36) {
            start = 20;
        } else if (size < 45) {
            start = 29;
        } else if (size < 53) {
            start = 38;
        } else {
            start = 47;
        }

        for (int i = start; i < start + 5; i++) {
            if (!noBackgroundSlots.contains(i)) {
                addIcon(i, icon2);
            }
        }

        for (int i = 0; i < size; i++) {
            if (!noBackgroundSlots.contains(i)) {
                if (getIcon(i) == null) {
                    addIcon(i, icon1);
                }
            }
        }
    }

    /**
     * Updates specific slot in inventory.
     *
     * @param index  index of slot to update
     */
    public void update(int index) {
        if (player.getOpenInventory().getTopInventory().getHolder() instanceof GUI) {
            activeIcons.get(index).refresh(player);
            player.getOpenInventory().getTopInventory().setItem(index, activeIcons.get(index).itemStack);
        }
    }

    /**
     * Refreshes all refreshable icons in the inventory
     *
     */
    public void refresh() {
        if (player.getOpenInventory().getTopInventory().getHolder() instanceof GUI) {
            setupIcons();
            var inv = player.getOpenInventory().getTopInventory();
            activeIcons.forEach((integer, icon) -> {
                icon.refresh(player);
                inv.setItem(integer, icon.itemStack);
            });

        }
    }

    private void setupIcons() {
        for (Map.Entry<Integer, List<Icon>> entry : this.actualIcons.entrySet()) {
            Collections.sort(entry.getValue());
            for (Icon icon : entry.getValue()) {
                if (icon == null) continue;
                if (icon.getVisibilityCondition() == null) {
                    activeIcons.put(entry.getKey(), icon);
                    break;
                } else if (icon.getVisibilityCondition().checkCondition(player, icon)) {
                    activeIcons.put(entry.getKey(), icon);
                    break;
                }
            }
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, this.size, ClansPlugin.MM.deserialize(this.title));
        setupIcons();
        for (Map.Entry<Integer, Icon> entry : this.activeIcons.entrySet()) {
            if (entry.getValue() == null) {
                inventory.setItem(entry.getKey(), null);
            } else {
                inventory.setItem(entry.getKey(), entry.getValue().itemStack);
            }
        }
        return inventory;
    }

    public boolean isForceClose() {
        return forceClose;
    }

    public void setForceClose(boolean forceClose) {
        this.forceClose = forceClose;
    }
}
