package net.astrona.easyclans.gui.ui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.LanguageController;
import net.astrona.easyclans.controller.LogController;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.models.Log;
import net.astrona.easyclans.models.LogType;
import net.astrona.easyclans.utils.AbstractChatUtil;
import net.astrona.easyclans.utils.Formatter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class BankGUI extends GUI {
    private ClansPlugin plugin;
    private ClansController clansController;
    private final Clan clan;
    private GUI previousUI;
    private LogController logController;

    public BankGUI(Player player, Clan clan, GUI previousUI, ClansPlugin plugin,
                   ClansController clansController, LogController logController) {
        super(27, LanguageController.getLocalized("bank.menu.title"));
        this.clan = clan;
        this.previousUI = previousUI;
        this.plugin = plugin;
        this.clansController = clansController;
        this.logController = logController;
        init();
        fancyBackground();
        this.open(player);
    }

    private ItemStack depositMoneyItem(){
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

        return item;
    }

    private Icon depositMoney() {
        Icon icon = new Icon(depositMoneyItem(), (self, player) -> {
            self.itemStack = depositMoneyItem();
        });

        icon.addClickAction(player -> {
            this.setForceClose(true);
            new AbstractChatUtil(player, (event) -> {
                try{
                    double value = Double.parseDouble(event.message());
                    if(value <= 0){
                        // FUCK THE PLAYER :)
                        player.sendMessage(ClansPlugin.MM.deserialize(
                                LanguageController.getLocalized("invalid_amount")
                        ));
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        return;
                    }
                    if(ClansPlugin.Economy.getBalance(player) >= value){
                        ClansPlugin.Economy.withdrawPlayer(player, value);
                        clan.setBank(clan.getBank() + value);
                        player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                        logController.addLog(new Log(String.valueOf(value), player.getUniqueId(), clan.getId(), LogType.DEPOSIT));
                    }else{
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        player.sendMessage(ClansPlugin.MM.deserialize(
                                LanguageController.getLocalized("not_enough_money")
                                        .replace("{price}", Formatter.formatMoney(value))
                        ));
                    }

                }catch (NumberFormatException e){
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() ->{
                clansController.updateClan(clan);
                this.setForceClose(false);
                open(player);
                refresh(player);
            });
        });

        return icon;
    }

    private ItemStack withdrawMoneyItem(){
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
        return item;
    }

    private Icon withdrawMoney() {
        Icon icon = new Icon(withdrawMoneyItem(), (self, player) -> {
            self.itemStack = withdrawMoneyItem();
        });

        icon.addClickAction(player -> {
            this.setForceClose(true);
            new AbstractChatUtil(player, (event) -> {
                try{
                    double value = Double.parseDouble(event.message());
                    if(value <= 0){
                        player.sendMessage(ClansPlugin.MM.deserialize(
                                LanguageController.getLocalized("invalid_amount")
                        ));
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        return;
                    }
                    if(clan.getBank() >= value){
                        ClansPlugin.Economy.depositPlayer(player, value);
                        clan.setBank(clan.getBank() - value);
                        player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                        logController.addLog(new Log(String.valueOf(value), player.getUniqueId(), clan.getId(), LogType.WITHDRAW));

                    }else{
                        player.sendMessage(ClansPlugin.MM.deserialize(
                                LanguageController.getLocalized("not_enough_money")
                                        .replace("{price}", Formatter.formatMoney(value))
                        ));
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));

                    }

                }catch (NumberFormatException e){
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() ->{
                clansController.updateClan(clan);
                this.setForceClose(false);
                open(player);
                refresh(player);
            });
        });

        return icon;
    }

    private void init() {
        setIcon(15, depositMoney());
        setIcon(11, withdrawMoney());

        if (previousUI != null) {
            addCloseAction((player) -> {
                previousUI.open(player);
            });
        }
    }
}
