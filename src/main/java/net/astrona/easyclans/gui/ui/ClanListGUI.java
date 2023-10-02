package net.astrona.easyclans.gui.ui;

import com.google.common.collect.Multimap;
import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.*;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.gui.Paginator;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.models.Log;
import net.astrona.easyclans.models.LogType;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanListGUI extends Paginator {

    private Player player;
    private ClansController clansController;
    private RequestsController requestsController;
    private PlayerController playerController;
    private GUI previousUI;
    private LogController logController;

    public ClanListGUI(Player player, ClansController clansController,
                       PlayerController playerController,
                       RequestsController requestsController,
                       GUI previousUI, LogController logController) {
        super(player, List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        ), LanguageController.getLocalized("clan_list.menu.title"), 54);

        this.player = player;
        this.clansController = clansController;
        this.playerController = playerController;
        this.requestsController = requestsController;
        this.previousUI = previousUI;
        this.logController = logController;
        init();
        this.open(0);
    }


    private void init() {
        var cPlayer = playerController.getPlayer(player.getUniqueId());
        for (Clan c : clansController.getClans()) {
            var item = c.getBanner();
            var meta = item.getItemMeta();
            var loreText = LanguageController.getLocalizedList("clan_list.menu.banner.lore");
            meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_list.menu.banner.name")
                    .replace("{name}", c.getName())
                    .replace("{display_name}", c.getDisplayName())
                    .replace("{tag}", c.getTag())
                    .replace("{members}", String.valueOf(c.getMembers().size()))
            ));
            meta.lore(loreText.stream().map(it ->
                    ClansPlugin.MM.deserialize(it
                                    .replace("{name}", c.getName())
                                    .replace("{display_name}", c.getDisplayName())
                                    .replace("{tag}", c.getTag())
                                    .replace("{interest_rate}", String.format("%.5f", c.getInterestRate()))
                                    .replace("{members}", String.valueOf(c.getMembers().size())))
                            .decoration(TextDecoration.ITALIC, false)
            ).toList());
            meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
            item.setItemMeta(meta);

            Icon icon = new Icon(item);

            icon.addClickAction((player1 -> {

                var owner = Bukkit.getPlayer(c.getOwner());


                if(cPlayer.getClanID() == c.getId()){
                    player1.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("invite.same_clan")));
                    return;
                }

                if(requestsController.getClanRequests(c.getId()).stream().anyMatch((rq) -> rq.getPlayerUuid().equals(player1.getUniqueId()))){
                    player1.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("invite.already_sent")));
                    player1.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }else{
                    requestsController.createRequest(
                            c.getId(),
                            player1.getUniqueId(),
                            System.currentTimeMillis() + 100000,
                            System.currentTimeMillis()
                    );
                    player1.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("invite.sent")));
                    player1.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    logController.addLog(new Log(c.getName(), player.getUniqueId(), c.getId(), LogType.REQUEST_SENT));

                    if(owner != null){
                        owner.sendMessage(
                                ClansPlugin.MM.deserialize(LanguageController.getLocalized(
                                        "invite.invite_received").replace("{player}", cPlayer.getName())
                                ));
                        owner.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    }
                }




            }));

            this.addIcon(icon);
        }

        if (previousUI != null) {
            addCloseAction((player) -> {
                previousUI.open(player);
            });
        }
    }
}
