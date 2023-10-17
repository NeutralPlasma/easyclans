package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.gui.Paginator;
import eu.virtusdevelops.easyclans.models.CRequest;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.models.LogType;
import net.kyori.adventure.sound.Sound;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class RequestsGUI extends Paginator {
    private ClansPlugin plugin;
    private Clan clan;
    private ClansController clansController;
    private PlayerController playerController;
    private RequestsController requestsController;
    private GUI previousUI;
    private LogController logController;
    private SimpleDateFormat sdf;
    private CurrenciesController currenciesController;

    public RequestsGUI(Player player, Clan clan,
                       ClansController clansController,
                       PlayerController playerController,
                       RequestsController requestsController,
                       GUI previousUI, LogController logController,
                       ClansPlugin plugin, CurrenciesController currenciesController) {
        super(player, List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        ), "<gold>Members <white>[<gold>{page}<white>]", 54);

        this.plugin = plugin;
        this.clan = clan;
        this.clansController = clansController;
        this.playerController = playerController;
        this.requestsController = requestsController;
        this.previousUI = previousUI;
        this.logController = logController;
        this.currenciesController = currenciesController;
        Locale loc = new Locale(plugin.getConfig().getString("language.language"), plugin.getConfig().getString("language.country"));
        sdf = new SimpleDateFormat(LanguageController.getLocalized("time_format"), loc);
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

            var expireDate = Math.abs((System.currentTimeMillis() - cRequest.getExpireTime()));

            meta.lore(LanguageController.getLocalizedList("requests.menu.invite.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                    .replace("{requested}", sdf.format(cRequest.getCreatedTime()))
                    .replace("{expires}", DurationFormatUtils.formatDurationWords(expireDate, true,true))
                    .replace("{time}", DurationFormatUtils.formatDurationWords(expireDate, true,true))
            )).toList());

            meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.menu.invite.name").replace("{player}", playerController.getPlayer(cRequest.getPlayerUuid()).getName())));

            item.setItemMeta(meta);

            Icon icon = new Icon(item);

            var cPlayer = playerController.getPlayer(cRequest.getPlayerUuid());

            icon.addClickAction((player) -> {
                var requester = Bukkit.getPlayer(cRequest.getPlayerUuid());

                new ConfirmGUI(player, (ignored) -> {
                    var provider =  currenciesController.getProvider("Vault");
                    // check if sender has money

                    if(provider.getValue(Bukkit.getOfflinePlayer(cPlayer.getUuid())) < clan.getJoinMoneyPrice()){
                        player.sendMessage(ClansPlugin.MM.deserialize(
                                LanguageController.getLocalized("requests.not_enough_money_accepter")
                                .replace("{player}", cPlayer.getName())));
                        player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));

                        if(requester != null){
                            requester.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                            requester.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.not_enough_money_sender")
                                    .replace("{clan}", clan.getName())));
                        }
                        open();
                        return;
                    }
                    if(clan.getMembers().size() > plugin.getConfig().getInt("clan.max_members")){
                        player.sendMessage(ClansPlugin.MM.deserialize(
                                LanguageController.getLocalized("requests.too_many_members")
                                        .replace("{max}", String.valueOf(plugin.getConfig().getInt("clan.max_members")))
                                        .replace("{current}", String.valueOf(clan.getMembers().size()))
                        ));
                        player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                        open();
                        return;
                    }
                    // remove money and delete request
                    provider.removeValue(Bukkit.getOfflinePlayer(cPlayer.getUuid()), clan.getJoinMoneyPrice());
                    requestsController.deleteRequest(cRequest);

                    // update player
                    cPlayer.setClanID(clan.getId());
                    cPlayer.setJoinClanDate(System.currentTimeMillis());
                    playerController.updatePlayer(cPlayer);

                    // edit clan add member blabla
                    clan.addMember(cPlayer.getUuid());

                    // send message
                    player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.accepted")
                            .replace("{player}", cPlayer.getName())));
                    if(requester != null){
                        requester.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                        requester.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.request_accepted")
                                .replace("{clan}", clan.getName())));
                    }

                    // add log
                    logController.addLog(new Log( cPlayer.getUuid().toString(), player.getUniqueId(), clan.getId(), LogType.REQUEST_ACCEPTED));
                    removeIcon(icon);
                    open();
                }, (ignored) -> {
                    // delete request
                    requestsController.deleteRequest(cRequest);
                    player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.decline")
                            .replace("{player}", cPlayer.getName())));

                    if(requester != null){
                        requester.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        requester.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.request_declined")
                                .replace("{clan}", clan.getName())));
                    }
                    logController.addLog(new Log( cPlayer.getUuid().toString(), player.getUniqueId(), clan.getId(), LogType.REQUEST_DECLINED));
                    removeIcon(icon);
                    open();
                }, LanguageController.getLocalized("requests.menu.invite.title").replace("{player}", cPlayer.getName()));
            });


            this.addIcon(icon);
        }
        if (previousUI != null) {
            addCloseAction((player) -> {
                previousUI.open();
            });
        }
    }
}
