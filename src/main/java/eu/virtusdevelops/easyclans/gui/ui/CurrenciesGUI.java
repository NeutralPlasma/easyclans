package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.gui.Paginator;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.Currency;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.models.LogType;
import eu.virtusdevelops.easyclans.providers.ProviderType;
import eu.virtusdevelops.easyclans.utils.AbstractChatUtil;
import eu.virtusdevelops.easyclans.utils.Formatter;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.providers.VotingPluginProvider;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
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
                .replace("{raw}", new DecimalFormat("#").format(currency.getValue()))
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

            icon.addShiftLeftClickAction((player1) -> {
                if (!isOwner && !player.hasPermission("easyclans.withdraw." + currency.getName())) {
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }
                var provider = currenciesController.getProvider(currency.getName());
                if(provider instanceof VotingPluginProvider) return;

                currenciesController.getProvider(currency.getName()).addValue(player, currency.getValue());
                currency.setValue(0);
                player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                logController.addLog(new Log(String.valueOf(currency.getValue()), player.getUniqueId(), clan.getId(), LogType.WITHDRAW));
                clansController.updateClan(clan);
                refresh();
            });


            icon.addLeftClickAction((player1) -> {
                if (!isOwner && !player.hasPermission("easyclans.withdraw." + currency.getName())) {
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }
                player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("currencies.withdraw")));
                var provider = currenciesController.getProvider(currency.getName());
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
                        if(value > currency.getValue()){
                            player.sendMessage(ClansPlugin.MM.deserialize(
                                    LanguageController.getLocalized("not_enough_money")
                                            .replace("{price}", Formatter.formatMoney(value))
                                            .replace("{type}", plugin.getConfig().getString("currency." + currency.getName()))
                            ));
                            player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                            return;
                        }

                        if(provider instanceof VotingPluginProvider){

                            if(value%clan.getMembers().size() != 0){
                                player.sendMessage(ClansPlugin.MM.deserialize(
                                        LanguageController.getLocalized("invalid_amount")
                                ));
                                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                                return;
                            }
                            int eachPlayer = (int)value/clan.getMembers().size();
                            for(var member : clan.getMembers()){
                                provider.addValue(Bukkit.getOfflinePlayer(member), eachPlayer);
                            }
                            currency.setValue(currency.getValue() - value);
                            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                            logController.addLog(new Log(String.valueOf(value), player.getUniqueId(), clan.getId(), LogType.WITHDRAW));
                        }else{
                            currenciesController.getProvider(currency.getName()).addValue(player, value);
                            currency.setValue(currency.getValue() - value);
                            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                            logController.addLog(new Log(String.valueOf(value), player.getUniqueId(), clan.getId(), LogType.WITHDRAW));
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
                if (!isOwner && !player.hasPermission("easyclans.deposit." + currency.getName())) {
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }
                var provider =  currenciesController.getProvider(currency.getName());
                player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("currencies.deposit")));
                setForceClose(true);
                new AbstractChatUtil(player, (event) -> {
                    try {

                        double value = -1;
                        if(provider.type() == ProviderType.INT){
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

                        if (provider.getValue(player) >= value) {
                            provider.removeValue(player, value);
                            currency.setValue(currency.getValue() + value);
                            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                            logController.addLog(new Log("deposit:" + currency.getName() + ":" + value, player.getUniqueId(), clan.getId(), LogType.DEPOSIT));

                        } else {
                            player.sendMessage(ClansPlugin.MM.deserialize(
                                    LanguageController.getLocalized("not_enough_money")
                                            .replace("{price}", Formatter.formatMoney(value))
                            ));
                            player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));

                        }
                        clansController.updateClan(clan);

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

            addIcon(icon);
        }


        if (previousUI != null) {
            addCloseAction((player) -> {
                previousUI.open(player);
            });
        }
    }
}
