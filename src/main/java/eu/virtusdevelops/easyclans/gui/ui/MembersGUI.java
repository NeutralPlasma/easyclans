package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.controller.LogController;
import eu.virtusdevelops.easyclans.controller.PlayerController;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.gui.Paginator;
import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.ClansController;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.models.LogType;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public class MembersGUI extends Paginator {
    private ClansPlugin plugin;
    private Clan clan;
    private Player player;
    private ClansController clansController;
    private PlayerController playerController;
    private LogController logController;
    private GUI previousUI;
    private SimpleDateFormat sdf;

    public MembersGUI(Player player, Clan clan,
                      ClansController clansController,
                      PlayerController playerController,
                      GUI previousUI, LogController logController,
                      ClansPlugin plugin) {
        super(player, List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        ), LanguageController.getLocalized("members.menu.title"), 54);


        this.plugin = plugin;
        this.player = player;
        this.clan = clan;
        this.clansController = clansController;
        this.playerController = playerController;
        this.previousUI = previousUI;
        this.logController = logController;
        Locale loc = new Locale(plugin.getConfig().getString("language.language"), plugin.getConfig().getString("language.country"));
        sdf = new SimpleDateFormat(LanguageController.getLocalized("time_format"), loc);

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
        List<Component> newlist = new ArrayList<>();
        var activeDate = new Date(cPlayer.getLastActive());
        var joinDate = new Date(cPlayer.getJoinClanDate());
        var timeDiff = Math.abs(kickTime - (System.currentTimeMillis() - activeDate.getTime()));
        double interest_player = plugin.getConfig().getDouble("rank_interest_value." + cPlayer.getRank());
        for (String string : strings) {
            newlist.add(
                    ClansPlugin.MM.deserialize(string
                            .replace("{active}", sdf.format(activeDate))
                            .replace("{joined}", sdf.format(joinDate))
                            .replace("{time_remaining}", DurationFormatUtils.formatDurationWords(timeDiff, true,false))
                            .replace("{status}", cPlayer.isActive() ?
                                    LanguageController.getLocalized("active") : LanguageController.getLocalized("inactive"))
                            .replace("{interest}", String.format("%.7f", interest_player))
                    ));
        }
        return newlist;
    }
}
