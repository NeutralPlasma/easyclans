package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.gui.Paginator;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.models.LogType;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.List;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanListGUI extends Paginator {

    private ClansPlugin plugin;
    private ClansController clansController;
    private RequestsController requestsController;
    private PlayerController playerController;
    private GUI previousUI;
    private LogController logController;

    public ClanListGUI(Player player,
                       ClansPlugin plugin, GUI previousUI) {
        super(player, List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        ), LanguageController.getLocalized("clan_list.menu.title"), 54);

        this.plugin = plugin;
        this.clansController = plugin.getClansController();
        this.playerController = plugin.getPlayerController();
        this.requestsController = plugin.getRequestsController();
        this.previousUI = previousUI;
        this.logController = plugin.getLogController();
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
                                    .replace("{interest_rate_members}", String.format("%.5f", c.getActualInterestRate()))
                                    .replace("{interest_rate_time}", String.format("%.5f", c.getInterestRate()))
                                    .replace("{interest_rate_full}", String.format("%.5f", c.getInterestRate() + c.getActualInterestRate()))
                                    .replace("{members}", String.valueOf(c.getMembers().size())))
                            .decoration(TextDecoration.ITALIC, false)
            ).toList());
            meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
            item.setItemMeta(meta);

            Icon icon = new Icon(item);

            icon.addClickAction((player1 -> {

                var playerClan = clansController.getClan(cPlayer.getClanID());
                if(playerClan != null && playerClan.getOwner().equals(cPlayer.getUuid())){
                    player1.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("invite.cant_request_owner")));
                    player1.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }
                if(playerClan != null){
                    player1.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("invite.already_in_clan")));
                    player1.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }


                var owner = Bukkit.getPlayer(c.getOwner());


                if(cPlayer.getClanID() == c.getId()){
                    player1.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("invite.same_clan")));
                    player1.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }

                if(requestsController.getClanRequests(c.getId()).stream().anyMatch((rq) -> rq.getPlayerUuid().equals(player1.getUniqueId()))){
                    player1.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("invite.already_sent")));
                    player1.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }else{
                    requestsController.createRequest(
                            c.getId(),
                            player1.getUniqueId(),
                            System.currentTimeMillis() + (plugin.getConfig().getLong("clan.default_request_expire_duration") * 1000),
                            System.currentTimeMillis()
                    );
                    player1.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("invite.sent")));
                    player1.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    logController.addLog(new Log(c.getName() + ":" + player.getName(), player.getUniqueId(), c.getId(), LogType.REQUEST_SENT));

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
                previousUI.open();
            });
        }
    }
}
