package eu.virtusdevelops.easyclans.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {
    /**
     * Plays the breaking of item animation to player
     * @param player - player to play animation to.
     * @param itemStack - item that breaks.
     */
    public static void breakAnimation(Player player, ItemStack itemStack){
        Location loc = player.getLocation();
        player.playSound(loc, Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        player.spawnParticle(Particle.ITEM_CRACK, loc.add(loc.getDirection()), 10, 0.3, 0.5, 0.3, 0.0, itemStack);
    }

    /**
     *
     * @param player - player to give item to
     * @param itemStack - item to give
     * @param drop - if player inventory full drop item
     * @return if drop is on true it returns true if any item was dropped on floor, if drop is on false
     * it returns true of it was able to give item to player.
     */
    public static boolean giveItem(Player player, ItemStack itemStack, boolean drop){
        if(drop){
            boolean dropped = false;
            for(ItemStack todrop : player.getInventory().addItem(itemStack).values()){
                player.getWorld().dropItemNaturally(player.getLocation(), todrop);
                dropped = true;
            }
            return  dropped;
        }else{
            if(StorageUtils.hasSpace(player.getInventory(), itemStack)){
                player.getInventory().addItem(itemStack);
                return true;
            }else{
                return false;
            }
        }
    }

}
