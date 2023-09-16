package net.astrona.easyclans.gui.ui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.LanguageController;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.utils.Formatter;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BankGUI extends GUI {
    private final Clan clan;
    private GUI previousUI;

    public BankGUI(Player player, Clan clan, GUI previousUI) {
        super(27, LanguageController.getLocalized("bank.menu.title"));
        this.clan = clan;
        this.previousUI = previousUI;
        init();
        fancyBackground();
        this.open(player);
    }

    private Icon depositMoney(){
        var item = new ItemStack(Material.CHEST);
        var meta = item.getItemMeta();
        var loreText = LanguageController.getLocalizedList("bank.menu.deposit.lore");

        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("bank.menu.deposit.name")));
        meta.lore(loreText.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                                .replace("{name}", clan.getName())
                                .replace("{display_name}", clan.getDisplayName())
                                .replace("{money}", Formatter.formatMoney(clan.getBank())))
                        .decoration(TextDecoration.ITALIC, false)
        ).toList());

        item.setItemMeta(meta);

        Icon icon = new Icon(item);
        icon.addClickAction(player -> {
            player.sendMessage("deposit money");
        });


        return icon;
    }

    private Icon withdrawMoney(){
        var item = new ItemStack(Material.DISPENSER);
        var meta = item.getItemMeta();
        var loreText = LanguageController.getLocalizedList("bank.menu.withdraw.lore");

        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("bank.menu.withdraw.name")));
        meta.lore(loreText.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                                .replace("{name}", clan.getName())
                                .replace("{display_name}", clan.getDisplayName())
                                .replace("{money}", Formatter.formatMoney(clan.getBank())))
                        .decoration(TextDecoration.ITALIC, false)
        ).toList());

        item.setItemMeta(meta);

        Icon icon = new Icon(item);
        icon.addClickAction(player -> {
            player.sendMessage("withdraw money");
        });


        return icon;
    }

    private void init() {
        setIcon(15, depositMoney());
        setIcon(11, withdrawMoney());

        if(previousUI != null){
            addCloseAction((player) -> {
                previousUI.open(player);
            });
        }
    }
}
