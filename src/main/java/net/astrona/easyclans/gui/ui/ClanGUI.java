package net.astrona.easyclans.gui.ui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.gui.actions.Action;
import net.astrona.easyclans.models.Clan;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ClanGUI extends GUI {
    private Clan clan;
    private ClansController clansController;


    public ClanGUI(Player player, Clan clan, ClansController clansController) {
        super(54, "Clan settings");

        this.clan = clan;
        this.clansController = clansController;

        construct();
        fancyBackground();
        open(player);
    }


    private void construct() {
        setIcon(13, clanInfoIcon());
        setIcon(11, membersIcon());
        setIcon(15, invitesIcon());
        setIcon(29, bankIcon());
        setIcon(31, clanSettingsIcon());
        setIcon(33, requestsIcon());
        //  33, 31, 29


        /*ItemStack test = new ItemStack(Material.APPLE);
        Icon appleIcon = new Icon(test, player -> test.setAmount(player.getLevel()));
        appleIcon.addClickAction(player -> this.update(player, 10));
        setIcon(10, appleIcon);*/
    }



    ItemStack clanInfoIconItem(){
        ItemStack itemStack = clan.getBanner().clone();
        var meta = itemStack.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize("<reset><#ffffff>[<#08fcfc>%s<#ffffff>]".formatted(clan.getName())));
        meta.lore(List.of(
                ClansPlugin.MM.deserialize(""),
                ClansPlugin.MM.deserialize("<gray>L-Click to change name"),
                ClansPlugin.MM.deserialize("<gray>R-Click to change display name")
        ));
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    Icon clanInfoIcon(){
        Icon icon = new Icon(clanInfoIconItem(), (it, player) -> {
            it.itemStack = clanInfoIconItem();
        });
        icon.addClickAction((this::refresh));
        return icon;
    }




    Icon membersIcon(){
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        var meta = itemStack.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize("<reset><#ffffff>[<gold>Members</gold><#ffffff>]"));
        meta.lore(List.of(
                ClansPlugin.MM.deserialize("<gray>Click to see members</gray>")
        ));
        itemStack.setItemMeta(meta);
        Icon icon = new Icon(itemStack);
        icon.addClickAction((player -> {
            // open new menu
            player.closeInventory();

            new MembersGUI(player, clan, clansController);

            //player.sendMessage(ClansPlugin.MM.deserialize("Okay clicked!"));
        }));
        return icon;
    }

    Icon invitesIcon(){
        ItemStack itemStack = new ItemStack(Material.BOOK);
        var meta = itemStack.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize("<reset><#ffffff>[<gold>Invites</gold><#ffffff>]"));
        meta.lore(List.of(
                ClansPlugin.MM.deserialize("<gray>Click to invites</gray>")
        ));
        itemStack.setItemMeta(meta);
        Icon icon = new Icon(itemStack);
        icon.addClickAction((player -> {
            // open new menu
            player.sendMessage(ClansPlugin.MM.deserialize("Okay clicked!"));
        }));
        return icon;
    }



    ItemStack bankIconItem(){
        ItemStack itemStack = new ItemStack(Material.SUNFLOWER);
        var meta = itemStack.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize("<reset><#ffffff>[<gold>BANK</gold><#ffffff>]"));
        meta.lore(List.of(
                ClansPlugin.MM.deserialize(""),
                ClansPlugin.MM.deserialize("L-Click to withdraw"),
                ClansPlugin.MM.deserialize("R-Click to deposit")
        ));
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    Icon bankIcon(){
        Icon icon = new Icon(bankIconItem(), ((it, player) -> {
            it.itemStack = bankIconItem();
        }));

        icon.addLeftClickAction((player)-> {
            player.sendMessage("Okay withdrawn!");
        });

        icon.addRightClickAction((player)-> {
            player.sendMessage("Okay deposited!");
        });

        return icon;
    }

    Icon clanSettingsIcon(){
        ItemStack itemStack = new ItemStack(Material.ANVIL);
        var meta = itemStack.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize("<reset><#ffffff>[<red>Settings</red><#ffffff>]"));
        meta.lore(List.of(
                ClansPlugin.MM.deserialize("<gray>Click to check clan settings</gray>")
        ));
        itemStack.setItemMeta(meta);
        Icon icon = new Icon(itemStack);
        icon.addClickAction((player -> {
            // open new menu
            player.sendMessage(ClansPlugin.MM.deserialize("Okay clicked!"));
        }));
        return icon;
    }


    Icon requestsIcon(){
        ItemStack itemStack = new ItemStack(Material.CHEST);
        var meta = itemStack.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize("<reset><#ffffff>[<red>Join requests</red><#ffffff>]"));
        meta.lore(List.of(
                ClansPlugin.MM.deserialize("<gray>Click to see join requests</gray>")
        ));
        itemStack.setItemMeta(meta);
        Icon icon = new Icon(itemStack);
        icon.addClickAction((player -> {
            // open new menu
            player.sendMessage(ClansPlugin.MM.deserialize("Okay clicked!"));
        }));
        return icon;
    }




}
