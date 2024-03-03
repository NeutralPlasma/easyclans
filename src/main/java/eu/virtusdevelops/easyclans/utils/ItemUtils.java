package eu.virtusdevelops.easyclans.utils;

import org.bukkit.inventory.ItemStack;

public class ItemUtils {

    public static ItemStack strip(ItemStack itemStack){
        var item = itemStack.clone();
        var meta = item.getItemMeta();
        meta.lore(null);
        meta.displayName(null);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack removeEnchants(ItemStack itemStack){
        var item = itemStack.clone();
        var meta = item.getItemMeta();
        for (var enchant : meta.getEnchants().keySet()) {
            meta.removeEnchant(enchant);
        }
        item.setItemMeta(meta);
        return item;
    }
}
