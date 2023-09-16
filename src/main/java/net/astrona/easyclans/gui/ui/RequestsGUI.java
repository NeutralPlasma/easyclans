package net.astrona.easyclans.gui.ui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.LanguageController;
import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.controller.RequestsController;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.gui.Paginator;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.models.CRequest;
import net.astrona.easyclans.models.Clan;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestsGUI extends Paginator {
    private Clan clan;
    private Player player;
    private ClansController clansController;
    private PlayerController playerController;
    private RequestsController requestsController;
    private GUI previousUI;

    public RequestsGUI(Player player, Clan clan,
                       ClansController clansController,
                       PlayerController playerController,
                       RequestsController requestsController,
                       GUI previousUI) {
        super(player, List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        ), "<gold>Members <white>[<gold>{page}<white>]", 54);

        this.player = player;
        this.clan = clan;
        this.clansController = clansController;
        this.playerController = playerController;
        this.requestsController = requestsController;
        this.previousUI = previousUI;
        init();
        this.open(0);
    }


    private void init() {
        boolean isOwner = clan.getOwner() == player.getUniqueId();

        for (CRequest cRequest : requestsController.getClanRequests(clan.getId())) {
            var oPlayer = Bukkit.getOfflinePlayer(cRequest.getPlayerUuid());
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwningPlayer(oPlayer);

            meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.menu.invite.name").replace("{player}", playerController.getPlayer(cRequest.getPlayerUuid()).getName())));
            meta.lore(format(LanguageController.getLocalizedList("requests.menu.invite.lore"), cRequest));

            item.setItemMeta(meta);

            Icon icon = new Icon(item);

            var cPlayer = playerController.getPlayer(cRequest.getPlayerUuid());

            icon.addClickAction((player) -> {
                player.sendMessage("CLICKED!");


                new ConfirmGUI(player, (ignored) -> {
                    requestsController.deleteRequest(cRequest);
                    cPlayer.setClanID(clan.getId());
                    cPlayer.setJoinClanDate(System.currentTimeMillis());
                    playerController.updatePlayer(cPlayer);
                    player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.accepted")
                            .formatted("{player}", cPlayer.getName())));
                    removeIcon(icon);
                    open();
                }, (ignored) -> {
                    requestsController.deleteRequest(cRequest);
                    player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.decline")
                            .formatted("{player}", cPlayer.getName())));
                    removeIcon(icon);
                    open();
                }, LanguageController.getLocalized("requests.menu.invite.title").formatted("{player}", cPlayer.getName()));
            });

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

    private List<Component> format(List<String> strings, CRequest cRequest) {
        Locale loc = new Locale("sl", "SI");

        List<Component> newlist = new ArrayList<>();

        var createdDate = new Date(cRequest.getCreatedTime());
        var expireDate = new Date(cRequest.getExpireTime());
        SimpleDateFormat sdf = new SimpleDateFormat(LanguageController.getLocalized("time_format"), loc);

        for (String string : strings) {
            newlist.add(
                    ClansPlugin.MM.deserialize(string
                            .replace("{requested}", sdf.format(createdDate))
                            .replace("{expires}", sdf.format(expireDate))
                    ));
        }
        return newlist;
    }

}
