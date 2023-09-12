package net.astrona.easyclans.gui.ui;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.gui.Paginator;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.models.Clan;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public class MembersGUI extends Paginator {
    private Clan clan;
    private Player player;
    private ClansController clansController;


    public MembersGUI(Player player, Clan clan, ClansController clansController) {
        super(player, List.of(
                10, 11, 12, 13, 14, 15, 16,
                         19, 20, 21, 22, 23, 24, 25,
                         28, 29, 30, 31, 32, 33, 34,
                         37, 38, 39, 40, 41, 42, 43
        ), "Clan settings", 54);
        this.player = player;
        this.clan = clan;
        this.clansController = clansController;
        init();
        this.open(0);
    }


    private void init(){
        for(CPlayer cPlayer : clan.getMembers()){
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(cPlayer.getUuid()));

            meta.displayName(ClansPlugin.MM.deserialize(cPlayer.getOfflinePlayer().getName()));
            meta.lore(format(List.of("<gray>Last active: <gold>{active}", "<gray>Joined clan on: <gold>{joined}"), cPlayer));
            item.setItemMeta(meta);

            Icon icon = new Icon(item);

            // if player has perm, add click to kick option blabla

            icon.addClickAction((player1 -> {}));

            this.addIcon(icon);
        }
    }


    private List<Component> format(List<String> strings, CPlayer cPlayer){
        Locale loc = new Locale("sl", "SI");

        List<Component> newlist = new ArrayList<>();

        var activeDate = new Date(cPlayer.getLastActive());
        var joinDate = new Date(cPlayer.getJoinClanDate());
        SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM yyyy HH:mm", loc);

        for(String string : strings){
            newlist.add(
                    ClansPlugin.MM.deserialize(string
                            .replace("{active}", sdf.format(activeDate))
                            .replace("{joined}", sdf.format(joinDate))
            ));
        }
        return newlist;
    }
}
