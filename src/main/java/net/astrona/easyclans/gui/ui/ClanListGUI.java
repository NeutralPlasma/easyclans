package net.astrona.easyclans.gui.ui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.LanguageController;
import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.gui.Paginator;
import net.astrona.easyclans.models.Clan;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.List;

public class ClanListGUI extends Paginator {
    private Clan clan;
    private Player player;
    private ClansController clansController;
    private PlayerController playerController;
    private GUI previousUI;


    public ClanListGUI(Player player, Clan clan,
                       ClansController clansController,
                       PlayerController playerController,
                       GUI previousUI) {
        super(player, List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        ), LanguageController.getLocalized("clan_list.menu.title"), 54);


        this.player = player;
        this.clan = clan;
        this.clansController = clansController;
        this.playerController = playerController;
        this.previousUI = previousUI;
        init();
        this.open(0);
    }


    private void init() {
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
                                    .replace("{members}", String.valueOf(c.getMembers().size())))
                            .decoration(TextDecoration.ITALIC, false)
            ).toList());
            item.setItemMeta(meta);

            Icon icon = new Icon(item);

            icon.addClickAction((player1 -> {
                player1.sendMessage("Sent a join request!");
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
