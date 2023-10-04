package net.astrona.easyclans.gui.ui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.*;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.gui.Paginator;
import net.astrona.easyclans.models.*;
import net.astrona.easyclans.providers.ProviderType;
import net.astrona.easyclans.utils.AbstractChatUtil;
import net.astrona.easyclans.utils.Formatter;
import net.kyori.adventure.sound.Sound;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class CurrenciesGUI extends Paginator {
    private ClansPlugin plugin;
    private Clan clan;
    private Player player;
    private ClansController clansController;
    private PlayerController playerController;
    private RequestsController requestsController;
    private GUI previousUI;
    private LogController logController;
    private SimpleDateFormat sdf;
    private CurrenciesController currenciesController;

    public CurrenciesGUI(Player player, Clan clan,
                         ClansController clansController,
                         PlayerController playerController,
                         GUI previousUI, LogController logController,
                         ClansPlugin plugin, CurrenciesController currenciesController) {
        super(player, List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25
        ), "<gold>Currencies <white>[<gold>{page}<white>]", 36);

        this.plugin = plugin;
        this.player = player;
        this.clan = clan;
        this.clansController = clansController;
        this.playerController = playerController;
        this.previousUI = previousUI;
        this.logController = logController;
        this.currenciesController = currenciesController;
        Locale loc = new Locale(plugin.getConfig().getString("language.language"), plugin.getConfig().getString("language.country"));
        sdf = new SimpleDateFormat(LanguageController.getLocalized("time_format"), loc);
        init();
        this.open(0);
    }


    private ItemStack currencyItem(Currency currency){
        ItemStack item = new ItemStack(Material.SUNFLOWER);
        var meta = item.getItemMeta();
        meta.lore(LanguageController.getLocalizedList("currencies.item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{amount}", Formatter.formatMoney(currency.getValue()))
                .replace("{currency}", plugin.getConfig().getString("currency." + currency.getName()))
        )).toList());
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("currencies.item.title")
                .replace("{currency}", plugin.getConfig().getString("currency." + currency.getName()))
        ));
        item.setItemMeta(meta);
        return item;
    }

    private void init() {
        boolean isOwner = clan.getOwner().equals(player.getUniqueId());

        for (var currency : clan.getCurrencies()) {
            ItemStack item = currencyItem(currency);
            Icon icon = new Icon(item, (self, pl) -> {
                self.itemStack = currencyItem(currency);
            });

            if (isOwner) {
                icon.addLeftClickAction((player1) -> {
                    setForceClose(true);
                    new AbstractChatUtil(player, (event) -> {
                        try {
                            double value = -1;
                            if(currenciesController.getProvider(currency.getName()).type() == ProviderType.INT){
                                value = Integer.parseInt(event.message());
                            }else{
                                value = Double.parseDouble(event.message());
                            }

                            if (value <= 0) {
                                player.sendMessage(ClansPlugin.MM.deserialize(
                                        LanguageController.getLocalized("invalid_amount")
                                ));
                                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                                return;
                            }
                            if (currency.getValue() >= value) {
                                currenciesController.getProvider(currency.getName()).addValue(player, value);
                                currency.setValue(currency.getValue() - value);
                                player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                                logController.addLog(new Log(String.valueOf(value), player.getUniqueId(), clan.getId(), LogType.WITHDRAW));

                            } else {
                                player.sendMessage(ClansPlugin.MM.deserialize(
                                        LanguageController.getLocalized("not_enough_money")
                                                .replace("{price}", Formatter.formatMoney(value))
                                ));
                                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));

                            }

                        } catch (NumberFormatException e) {
                            player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        }
                    }, plugin).setOnClose(() -> {
                        clansController.updateClan(clan);
                        setForceClose(false);
                        open();
                        refresh();
                    });
                });


                icon.addRightClickAction((player1) -> {

                    setForceClose(true);
                    new AbstractChatUtil(player, (event) -> {
                        try {

                            double value = -1;
                            if(currenciesController.getProvider(currency.getName()).type() == ProviderType.INT){
                                value = Integer.parseInt(event.message());
                            }else{
                                value = Double.parseDouble(event.message());
                            }

                            if (value <= 0) {
                                player.sendMessage(ClansPlugin.MM.deserialize(
                                        LanguageController.getLocalized("invalid_amount")
                                ));
                                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                                return;
                            }

                            if((currency.getValue() + value) > plugin.getConfig().getDouble("deposit_maxs." +currency.getName())){
                                player.sendMessage(ClansPlugin.MM.deserialize(
                                        LanguageController.getLocalized("currencies.limit_reached")
                                                .replace("{value}", Formatter.formatMoney(plugin.getConfig().getDouble("deposit_maxs." +currency.getName())))
                                ));
                                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                                return;
                            }

                            if (currenciesController.getProvider(currency.getName()).getValue(player) >= value) {
                                currenciesController.getProvider(currency.getName()).removeValue(player, value);
                                currency.setValue(currency.getValue() + value);
                                player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                                logController.addLog(new Log(String.valueOf(value), player.getUniqueId(), clan.getId(), LogType.WITHDRAW));

                            } else {
                                player.sendMessage(ClansPlugin.MM.deserialize(
                                        LanguageController.getLocalized("not_enough_money")
                                                .replace("{price}", Formatter.formatMoney(value))
                                ));
                                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));

                            }

                        } catch (NumberFormatException e) {
                            player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        }
                    }, plugin).setOnClose(() -> {
                        clansController.updateClan(clan);
                        setForceClose(false);
                        open();
                        refresh();
                    });
                });
            }
            addIcon(icon);
        }


        if (previousUI != null) {
            addCloseAction((player) -> {
                previousUI.open(player);
            });
        }
    }
}
