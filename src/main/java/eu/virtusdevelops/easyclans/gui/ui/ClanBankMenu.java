package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.AsyncPaginator;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.gui.actions.AsyncReturnTask;
import eu.virtusdevelops.easyclans.models.*;
import eu.virtusdevelops.easyclans.providers.Provider;
import eu.virtusdevelops.easyclans.providers.ProviderType;
import eu.virtusdevelops.easyclans.providers.VotingPluginProvider;
import eu.virtusdevelops.easyclans.utils.AbstractChatUtil;
import eu.virtusdevelops.easyclans.utils.Formatter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanBankMenu extends AsyncPaginator {

    private final CPlayer cPlayer;
    private final Clan clan;
    private final ClansController clansController;
    private final PlayerController playerController;
    private final CurrenciesController currenciesController;
    private final RequestsController requestsController;
    private final InvitesController invitesController;
    private final LogController logController;
    private final ClansPlugin plugin;
    private final GUI previousUI;

    public ClanBankMenu(Player player, Clan clan, ClansController clansController, PlayerController playerController, CurrenciesController currenciesController,
                        RequestsController requestsController, InvitesController invitesController, LogController logController, ClansPlugin plugin, GUI previousUI){
        super(player, plugin,36, LanguageController.getLocalized("banks_menu.title"), List.of(
                10, 11, 12, 13, 14, 15, 16
        ));

        this.clan = clan;
        this.clansController = clansController;
        this.playerController = playerController;
        this.currenciesController = currenciesController;
        this.requestsController = requestsController;
        this.invitesController = invitesController;
        this.logController = logController;
        this.plugin = plugin;
        this.cPlayer = playerController.getPlayer(player.getUniqueId());
        this.previousUI = previousUI;

        setup();
        init();



    }

    private void setup(){
        setFetchPageTask(new AsyncReturnTask<>() {
            @Override
            public List<Icon> fetchPageData(int page, int perPage) {
                var cSize = clan.getCurrencies().size();
                var currencies = clan.getCurrencies();
                List<Icon> icons = new ArrayList<>();

                for(int i = 0; i < perPage; i++){
                    var index = i + (page * perPage);
                    if(index >= cSize) break;
                    var currency = currencies.get(index);
                    icons.add(createCurrencyIcon(currency));
                }

                return icons;
            }

            @Override
            public List<Icon> fetchData() {return null;}
        });

        setGetItemsCountTask(new AsyncReturnTask<>() {
            @Override
            public Integer fetchPageData(int page, int perPage) {
                return clan.getCurrencies().size();
            }

            @Override
            public Integer fetchData() {
                return clan.getCurrencies().size();
            }
        });


        if(previousUI != null)
            addCloseAction((target) -> {
                previousUI.open();
            });
    }


    private ItemStack createCurrencyItem(Currency currency){
        var item = new ItemStack(Material.SUNFLOWER);
        var meta = item.getItemMeta();

        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("banks_menu.currency_item.title")
                                .replace("{amount}", Formatter.formatMoney(currency.getValue()))
                                .replace("{raw}", new DecimalFormat("#").format(currency.getValue()))
                                .replace("{currency}", plugin.getConfig().getString("currency." + currency.getName() + ".name"))
                ).decoration(TextDecoration.ITALIC, false)
        );

        meta.lore(LanguageController.getLocalizedList("banks_menu.currency_item.lore").stream().map(it ->
                ClansPlugin.MM.deserialize(it
                        .replace("{amount}", Formatter.formatMoney(currency.getValue()))
                        .replace("{raw}", new DecimalFormat("#").format(currency.getValue()))
                        .replace("{currency}", plugin.getConfig().getString("currency." + currency.getName() + ".name"))
                ).decoration(TextDecoration.ITALIC, false)
        ).toList());
        item.setItemMeta(meta);
        return item;
    }

    private Icon createCurrencyIcon(Currency currency){
        var icon = new Icon(createCurrencyItem(currency), (self, target) -> {
            self.itemStack = createCurrencyItem(currency);
        });


        icon.addShiftRightClickAction((target) -> {
            if(!target.getUniqueId().equals(clan.getOwner())
                    && !target.hasPermission("easyclans.admin.bank_deposit")
                    && !(cPlayer.hasPermission(UserPermissions.BANK_DEPOSIT) && cPlayer.getClanID() == clan.getId())){

                target.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            var provider = currenciesController.getProvider(currency.getName());
            var value = provider.getValue(player);


            if((currency.getValue() + value) > plugin.getConfig().getDouble("deposit_maxs." +currency.getName())){
                value = plugin.getConfig().getDouble("deposit_maxs." +currency.getName()) - currency.getValue();
            }

            if((currency.getValue() + value) > plugin.getConfig().getDouble("deposit_maxs." +currency.getName())){
                player.sendMessage(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("currencies.limit_reached")
                                .replace("{max}", Formatter.formatMoney(plugin.getConfig().getDouble("deposit_maxs." +currency.getName())))
                                .replace("{value}", Formatter.formatMoney(value))
                ));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            provider.removeValue(player, value);
            currency.setValue(currency.getValue() + value);
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            refresh();
        });

        icon.addShiftLeftClickAction((target) -> {
            if(!target.getUniqueId().equals(clan.getOwner())
                    && !target.hasPermission("easyclans.admin.bank_withdraw")
                    && !(cPlayer.hasPermission(UserPermissions.BANK_WITHDRAW) && cPlayer.getClanID() == clan.getId())){

                target.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }
            if(currency.getValue() == 0){
                target.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            var provider = currenciesController.getProvider(currency.getName());
            if(provider instanceof VotingPluginProvider) {
                target.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            provider.addValue(player, currency.getValue());
            logController.addLog(new Log("currency:" + currency.getName() + ":" + currency.getValue(), player.getUniqueId(), clan.getId(), LogType.WITHDRAW));
            currency.setValue(0);
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            clansController.updateClan(clan);
            refresh();
        });


        icon.addLeftClickAction((target) -> {
            if(!target.getUniqueId().equals(clan.getOwner())
                    && !target.hasPermission("easyclans.admin.bank_deposit")
                    && !(cPlayer.hasPermission(UserPermissions.BANK_DEPOSIT) && cPlayer.getClanID() == clan.getId())){

                target.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }



            var provider = currenciesController.getProvider(currency.getName());
            player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("currencies.deposit")));
            setForceClose(true);

            new AbstractChatUtil(target, (event) -> {
                double value = parseFromEvent(provider, event);
                if(value == -1) return;

                if((currency.getValue() + value) > plugin.getConfig().getDouble("deposit_maxs." +currency.getName())){
                    player.sendMessage(ClansPlugin.MM.deserialize(
                            LanguageController.getLocalized("currencies.limit_reached")
                                    .replace("{max}", Formatter.formatMoney(plugin.getConfig().getDouble("deposit_maxs." +currency.getName())))
                                    .replace("{value}", Formatter.formatMoney(value))
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
                            LanguageController.getLocalized("currencies.not_enough")
                                    .replace("{price}", Formatter.formatMoney(value))
                                    .replace("{currency}", plugin.getConfig().getString("currency." + currency.getName() + ".symbol"))
                    ));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));

                }
                clansController.updateClan(clan);
            }, plugin)
            .setOnClose(() -> {
                setForceClose(false);
                open();
                refresh();
            });
        });


        icon.addRightClickAction((target) -> {
            if(!target.getUniqueId().equals(clan.getOwner())
                    && !target.hasPermission("easyclans.admin.bank_withdraw")
                    && !(cPlayer.hasPermission(UserPermissions.BANK_WITHDRAW) && cPlayer.getClanID() == clan.getId())){

                target.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }
            var provider = currenciesController.getProvider(currency.getName());
            player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("currencies.withdraw")));
            setForceClose(true);

            new AbstractChatUtil(target, (event) -> {
                double value = parseFromEvent(provider, event);
                if(value == -1) return;

                if(provider instanceof VotingPluginProvider){

                    if(value%clan.getMembers().size() != 0){
                        player.sendMessage(ClansPlugin.MM.deserialize(
                                LanguageController.getLocalized("currencies.invalid_amount")
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
                    logController.addLog(new Log("currency:" + currency.getName() + ":" + value, player.getUniqueId(), clan.getId(), LogType.WITHDRAW));
                }else{
                    currenciesController.getProvider(currency.getName()).addValue(player, value);
                    currency.setValue(currency.getValue() - value);
                    player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    logController.addLog(new Log("currency:" + currency.getName() + ":" + value, player.getUniqueId(), clan.getId(), LogType.WITHDRAW));
                }
                clansController.updateClan(clan);


            }, plugin)
            .setOnClose(() -> {
                setForceClose(false);
                open();
                refresh();
            });



        });

        return icon;
    }


    private double parseFromEvent(Provider provider, AbstractChatUtil.ChatConfirmEvent event){
        double value = -1;
        try{
            if(provider.type() == ProviderType.INT){
                value = Integer.parseInt(event.message());
            }else{
                value = Double.parseDouble(event.message());
            }
        }catch (NumberFormatException e){
            player.sendMessage(ClansPlugin.MM.deserialize(
                    LanguageController.getLocalized("currencies.invalid_amount")
            ));
            player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
            return -1;
        }
        if(value <= 0){
            player.sendMessage(ClansPlugin.MM.deserialize(
                    LanguageController.getLocalized("currencies.invalid_amount")
            ));
            player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
            return -1;
        }

        return value;

    }
}
