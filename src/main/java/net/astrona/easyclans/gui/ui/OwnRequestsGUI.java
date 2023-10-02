package net.astrona.easyclans.gui.ui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.*;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.gui.Paginator;
import net.astrona.easyclans.models.Log;
import net.astrona.easyclans.models.LogType;
import net.kyori.adventure.sound.Sound;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.entity.Player;

import java.util.List;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class OwnRequestsGUI extends Paginator {
    private Player player;
    private ClansController clansController;
    private PlayerController playerController;
    private RequestsController requestsController;
    private GUI previousUI;
    private LogController logController;

    public OwnRequestsGUI(Player player, ClansController clansController, PlayerController playerController,
                          RequestsController requestsController, LogController logController, GUI previousUI){
        super(player, List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        ), "<gold>Members <white>[<gold>{page}<white>]", 54);

        this.player = player;
        this.clansController = clansController;
        this.playerController = playerController;
        this.requestsController = requestsController;
        this.logController = logController;
        this.previousUI = previousUI;

        init();
        open(0);
    }


    private void init(){
        var requests = requestsController.getPlayerRequests(player.getUniqueId());
        for(var request : requests){
            var clan = clansController.getClan(request.getClanId());
            var item = clan.getBanner();
            var meta = item.getItemMeta();
            meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests_own.menu.title")));

            var expireDate = Math.abs((System.currentTimeMillis() - request.getExpireTime()));

            meta.lore(LanguageController.getLocalizedList("requests_own.menu.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                    .replace("{time}", DurationFormatUtils.formatDurationWords(expireDate, true,true))
            )).toList());

            item.setItemMeta(meta);
            var icon = new Icon(item);
            icon.addClickAction((player) -> {
                new ConfirmGUI(player, (player1) -> {
                    // confirm
                    removeIcon(icon);
                    requestsController.deleteRequest(request);
                    player.sendMessage(
                            ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests_own.cancel")
                                    .replace("{clan}", clan.getName()))
                    );
                    player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));

                    logController.addLog(new Log("", player.getUniqueId(), clan.getId(), LogType.REQUEST_CANCELED));
                }, (player1) -> {
                    // decline
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    open();
                }, LanguageController.getLocalized("requests_own.cancel_menu.title").replace("{clan}", clan.getName()));
            });
        }
    }
}
