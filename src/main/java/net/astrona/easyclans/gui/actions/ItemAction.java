package net.astrona.easyclans.gui.actions;

import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public interface ItemAction {

    void execute(Player player, ItemStack itemStack);
}
