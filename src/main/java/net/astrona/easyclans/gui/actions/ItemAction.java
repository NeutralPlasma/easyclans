package net.astrona.easyclans.gui.actions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemAction {

    void execute(Player player, ItemStack itemStack);
}
