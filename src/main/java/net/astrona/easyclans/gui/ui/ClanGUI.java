package net.astrona.easyclans.gui.ui;

import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.gui.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ClanGUI extends GUI {
    public ClanGUI(Player player) {
        super(54, "");
        construct();
        fancyBackground();
        open(player);



    }


    private void construct() {
        ItemStack test = new ItemStack(Material.APPLE);
        Icon appleIcon = new Icon(test, player -> test.setAmount(player.getLevel()));
        appleIcon.addClickAction(player -> this.update(player, 10));
        setIcon(10, appleIcon);
    }


}
