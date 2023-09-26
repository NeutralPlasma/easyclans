package net.astrona.easyclans.gui.ui;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.LanguageController;
import net.astrona.easyclans.controller.LogController;
import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.gui.Paginator;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.models.Log;
import net.astrona.easyclans.models.LogType;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

public class MembersGUI extends Paginator {
    private Clan clan;
    private Player player;
    private ClansController clansController;
    private PlayerController playerController;
    private LogController logController;
    private GUI previousUI;

    public MembersGUI(Player player, Clan clan,
                      ClansController clansController,
                      PlayerController playerController,
                      GUI previousUI, LogController logController) {
        super(player, List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        ), LanguageController.getLocalized("members.menu.title"), 54);

        this.player = player;
        this.clan = clan;
        this.clansController = clansController;
        this.playerController = playerController;
        this.previousUI = previousUI;
        this.logController = logController;
        init();
        this.open(0);
    }

    private void init() {
        boolean isOwner = clan.getOwner().equals(player.getUniqueId());

        for (CPlayer cPlayer : playerController.getClanPlayers(clan.getId())) {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(cPlayer.getUuid()));

            meta.displayName(ClansPlugin.MM.deserialize(cPlayer.getName()));




            if(cPlayer.getUuid().equals(clan.getOwner()))
                meta.lore(format(LanguageController.getLocalizedList("members.menu.owner_lore"), cPlayer, clan.getAutoKickTime()));
            else{
                if(cPlayer.isActive() || clan.getAutoKickTime() == -1){
                    meta.lore(format(LanguageController.getLocalizedList("members.menu.lore"), cPlayer, clan.getAutoKickTime()));
                }else{
                    meta.lore(format(LanguageController.getLocalizedList("members.menu.inactive_lore"), cPlayer, clan.getAutoKickTime()));
                }
            }



            item.setItemMeta(meta);

            Icon icon = new Icon(item);

            if (isOwner && !cPlayer.getUuid().equals(player.getUniqueId())) {
                icon.addLeftClickAction((player) -> {
                    new ConfirmGUI(player, (ignored) -> {
                        cPlayer.removeFromClan();
                        clan.getMembers().remove(cPlayer.getUuid());
                        playerController.updatePlayer(cPlayer);
                        player.sendMessage(ClansPlugin.MM.deserialize(
                                LanguageController.getLocalized("members.kick.kick_success")
                                        .replace("{player}", cPlayer.getName())
                        ));
                        var bplayer = Bukkit.getPlayer(cPlayer.getUuid());
                        if(bplayer != null){
                            bplayer.sendMessage(LanguageController.getLocalized("members.kick.kicked"));
                        }
                        logController.addLog(new Log( cPlayer.getUuid().toString(), player.getUniqueId(), clan.getId(), LogType.MEMBER_KICK));
                        open();
                    }, (ignored) -> {
                        open();
                    }, LanguageController.getLocalized("members.kick.title").replace("{player}", cPlayer.getName()));
                });
            }

            icon.addClickAction((player1 -> {
            }));

            this.addIcon(icon);
        }

        if (previousUI != null) {
            addCloseAction((player) -> {
                previousUI.open(player);
            });
        }
    }

    private List<Component> format(List<String> strings, CPlayer cPlayer, int kickTime) {
        Locale loc = new Locale("sl", "SI");

        List<Component> newlist = new ArrayList<>();

        var activeDate = new Date(cPlayer.getLastActive());
        var joinDate = new Date(cPlayer.getJoinClanDate());

        var timeDiff = Math.abs(kickTime - (System.currentTimeMillis() - activeDate.getTime()));


        SimpleDateFormat sdf = new SimpleDateFormat(LanguageController.getLocalized("time_format"), loc);

        for (String string : strings) {
            newlist.add(
                    ClansPlugin.MM.deserialize(string
                            .replace("{active}", sdf.format(activeDate))
                            .replace("{joined}", sdf.format(joinDate))
                            .replace("{time_remaining}", DurationFormatUtils.formatDurationWords(timeDiff, true,false))
                            .replace("{status}", cPlayer.isActive() ?
                                    LanguageController.getLocalized("active") : LanguageController.getLocalized("inactive"))
                    ));
        }
        return newlist;
    }
}
