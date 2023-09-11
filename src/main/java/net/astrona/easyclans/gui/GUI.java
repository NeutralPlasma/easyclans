package net.astrona.easyclans.gui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.gui.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
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
    private List<Integer> noBackgroundSlots = new ArrayList<>();

    public GUI(int size, String title) {
        this.size = size;
        this.title = title;
    }

    public GUI(int size, String title, List<Integer> noBackgroundSlots) {
        this.size = size;
        this.title = title;
        this.noBackgroundSlots = noBackgroundSlots;
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


    public void setBackground(ItemStack itemStack){
        Icon icon = new Icon(itemStack);
        for(int x = 0; x < size; x++){
            if(!icons.containsKey(x)){
                setIcon(x, icon);
            }
        }
    }

    public void fancyBackground(){
        var stack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        var stack2 = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);

        var icon1 = new Icon(stack);
        var icon2 = new Icon(stack2);


        // set items in inventory
        setIcon(0, icon2);
        setIcon(1, icon2);
        setIcon(7, icon2);
        setIcon(8, icon2);
        setIcon(9, icon2);
        setIcon(17, icon2);

        int start = 20;
        if(size < 45){
            start = 29;
        }else if(size < 53){
            start = 38;
        }else {
            start = 47;
        }

        for(int i = start; i < start+5; i++){
            if(!icons.containsKey(i) && !noBackgroundSlots.contains(i)) {
                setIcon(i, icon2);
            }
        }

        for(int i = 0; i < size ; i++){
            if(!icons.containsKey(i) && !noBackgroundSlots.contains(i)){
                if(getIcon(i) == null) {
                    setIcon(i, icon1);
                }
            }
        }

    }

    /**
     * Updates specific slot in inventory.
     * @param player to which it has to update
     * @param index index of slot to update
     */
    public void update(Player player, int index){
        if(player.getOpenInventory().getTopInventory().getHolder() instanceof GUI){
            icons.get(index).refresh(player);
            player.getOpenInventory().getTopInventory().setItem(index, icons.get(index).itemStack);
        }
    }

    /**
     * Refreshes all refreshable icons in the inventory
     * @param player to which it has to update
     */
    public void refresh(Player player){
        if(player.getOpenInventory().getTopInventory().getHolder() instanceof GUI){
            var inv = player.getOpenInventory().getTopInventory();
            icons.forEach((integer, icon) -> {
                icon.refresh(player);
                inv.setItem(integer, icon.itemStack);
            });
        }
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
