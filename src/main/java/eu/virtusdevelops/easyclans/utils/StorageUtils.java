package eu.virtusdevelops.easyclans.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StorageUtils {


    /**
     * Check if a specific inventory has space for a specific item.
     * @param inventory - inventory of chest/player
     * @param itemStack - item to check if it has space in inventory
     * @return - returns true if there is space and false if not.
     */
    public static boolean hasSpace(Inventory inventory, ItemStack itemStack){
        for(ItemStack item : inventory){
            if(item == null) return true;
            if(item.isSimilar(itemStack)){
                if(item.getAmount() + itemStack.getAmount() < item.getAmount()){
                    return  true;
                }
            }
        }
        return false;
    }
}
