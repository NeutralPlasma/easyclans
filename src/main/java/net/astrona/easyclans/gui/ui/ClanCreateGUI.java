package net.astrona.easyclans.gui.ui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.*;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.models.Log;
import net.astrona.easyclans.models.LogType;
import net.astrona.easyclans.utils.AbstractChatUtil;
import net.astrona.easyclans.utils.BannerUtils;
import net.astrona.easyclans.utils.Formatter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.astrona.easyclans.controller.LanguageController.getLocalizedList;
import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanCreateGUI extends GUI {
    private ItemStack banner;
    private int kickTime = 7*24*60*60*1000;
    private double moneyPrice;
    private String name = "DEFAULT", displayName = "NONE", tag = "DE";
    private final ClansPlugin plugin;
    private final PlayerController playerController;
    private final ClansController clansController;
    private final RequestsController requestsController;
    private final LogController logController;
    private CurrenciesController currenciesController;


    public ClanCreateGUI(Player player, ClansPlugin plugin,
                         PlayerController playerController, ClansController clansController,
                         RequestsController requestsController, LogController logController,
                         CurrenciesController currenciesController) {
        super(54, LanguageController.getLocalized("create.menu.title"));
        this.plugin = plugin;
        this.playerController = playerController;
        this.clansController = clansController;
        this.requestsController = requestsController;
        this.logController = logController;
        this.currenciesController = currenciesController;
        this.moneyPrice = plugin.getConfig().getDouble("clan.default_join_price");
        this.name = player.getName();
        this.displayName = player.getName() + "_DISPLAY";
        init();
        fancyBackground();
        open(player);
    }


    private ItemStack strip(ItemStack itemStack){
        var item = itemStack.clone();
        var meta = item.getItemMeta();
        meta.lore(null);
        meta.displayName(null);
        item.setItemMeta(meta);
        return item;
    }

    private void legalizeBanner() {
        var meta = banner.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.banner.name")
                        .replace("{name}", name)
                    ).decoration(TextDecoration.ITALIC, false)
        );
        for (var enchant : meta.getEnchants().keySet()) {
            meta.removeEnchant(enchant);
        }
        var loreText = LanguageController.getLocalizedList("create.menu.banner.lore");
        meta.lore(loreText.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                                .replace("{name}", name)
                                .replace("{display_name}", displayName)
                        ).decoration(TextDecoration.ITALIC, false)
        ).toList());
        banner.setItemMeta(meta);
    }

    private Icon clanBanner() {
        legalizeBanner();
        Icon icon = new Icon(banner, (self, player) -> {
            legalizeBanner();
            self.itemStack = banner;
        });

        icon.addDragItemAction(((player, itemStack) -> {
            player.sendMessage(itemStack.getType().toString());
            if (itemStack.getType().toString().endsWith("BANNER")) {
                banner = itemStack.clone();
                legalizeBanner();
                this.update(player, 13);
                player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            } else {
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
            }

        }));

        icon.addLeftClickAction((player) -> {
            player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.banner.name_message")));
            new AbstractChatUtil(player, (meow) -> {
                var stripped = meow.message().replace(" ", "_").strip().trim();
                if(stripped.length() < 3){
                    // not good
                    player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.banner.invalid_name")));
                    return;
                }

                for(var clan : clansController.getClans()){
                    if(clan.getName().equalsIgnoreCase(stripped)){
                        player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.banner.invalid_name")));
                        return;
                    }
                }
                name = stripped;
            }, plugin).setOnClose(() -> {
                legalizeBanner();
                icon.itemStack = banner;
                open(player);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    refresh(player);
                }, 5L);
            });
            player.closeInventory();
        });

        icon.addRightClickAction((player) -> {
            player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.banner.display_name_message")));
            new AbstractChatUtil(player, (meow) -> {
                if(meow.message().isEmpty() || meow.message().isBlank() || meow.message().length() < 3){
                    // not good
                    player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.banner.invalid_display_name")));
                    return;
                }
                displayName = meow.message();
            }, plugin).setOnClose(() -> {
                legalizeBanner();
                icon.itemStack = banner;
                open(player);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    refresh(player);
                }, 5L);

            });
            player.closeInventory();
        });

        return icon;
    }

    private ItemStack confirmButtonItem(){
        ItemStack itemStack = new ItemStack(Material.LIME_CONCRETE);
        var meta = itemStack.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.create.name")));
        var loreText = getLocalizedList("create.menu.create.lore");

        meta.lore(loreText.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                        .replace("{name}", name)
                        .replace("{display_name}", displayName)
                ).decoration(TextDecoration.ITALIC, false)
        ).toList());

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private Icon confirmButton() {
        Icon icon = new Icon(confirmButtonItem(), (self, player) -> {
            self.itemStack = confirmButtonItem();
        });
        icon.addClickAction(player -> {
            new ConfirmGUI(player, (confirmPlayer) -> {
                // TODO: add multi currency support

                if(currenciesController.getProvider("Vault").getValue(player) < plugin.getConfig().getDouble("clan.create.price.money") ){
                    player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.create.not_enough_money")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    player.sendMessage(ClansPlugin.MM.deserialize(
                            LanguageController.getLocalized("not_enough_money")
                                    .replace("{price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.create.price.money")))
                    ));
                }else{
                    currenciesController.getProvider("Vault").removeValue(player, plugin.getConfig().getDouble("clan.create.price.money"));
                    //ClansPlugin.Economy.withdrawPlayer(player, plugin.getConfig().getDouble("clan.create.price.money"));
                    Clan clan = clansController.createClan(
                            confirmPlayer.getUniqueId(),
                            name,
                            displayName,
                            kickTime,
                            0,
                            moneyPrice,
                            strip(banner),
                            0.0,
                            0.0,
                            tag,
                            List.of(confirmPlayer.getUniqueId())
                    );
                    new AdminClanGUI(player, clan, clansController, playerController, requestsController, plugin, logController, currenciesController);
                    logController.addLog(new Log(name, player.getUniqueId(), clan.getId(), LogType.CLAN_CREATE));
                }
            }, (cancelPlayer) -> {
                cancelPlayer.openInventory(getInventory());
            }, LanguageController.getLocalized("create.menu.create.confirm-title"));
        });

        return icon;
    }


    private ItemStack priceSettingsItem() {
        var item = new ItemStack(Material.SUNFLOWER);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.money_price.name"))
                .decoration(TextDecoration.ITALIC, false)
        );
        var loreStrings = LanguageController.getLocalizedList("create.menu.money_price.lore");
        var moneyString = Formatter.formatMoney(moneyPrice);
        meta.lore(loreStrings.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                                .replace("{price}", moneyString))
                        .decoration(TextDecoration.ITALIC, false)
        ).toList());
        item.setItemMeta(meta);
        return item;
    }
    private Icon priceSettings(){
        Icon icon = new Icon(priceSettingsItem(), (self, player) -> {
            self.itemStack = priceSettingsItem();
        });

        icon.addLeftClickAction((player) -> {
            if(!player.hasPermission("easyclans.settings.join_price")) {
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                return;
            }
            new AbstractChatUtil(player, (meow) -> {
                try{
                    double price = Double.parseDouble(meow.message());
                    if(price < 0){
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    }else{
                        moneyPrice = price;
                        player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    }
                }catch (NumberFormatException e){
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() -> {
                icon.itemStack = priceSettingsItem();
                open(player);
            });
        });
        icon.addRightClickAction((player) -> {
            if(!player.hasPermission("easyclans.settings.join_price")) {
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                return;
            }
            moneyPrice = 1000;
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
        });


        return icon;
    }


    private ItemStack kickSettingsItem(){
        var item = new ItemStack(Material.ANVIL);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.kick_time.name"))
                .decoration(TextDecoration.ITALIC, false)
        );
        var loreStrings = LanguageController.getLocalizedList("create.menu.kick_time.lore");
        var kickText = kickTime == -1 ? LanguageController.getLocalized("disabled") : DurationFormatUtils.formatDurationWords(kickTime, true,true);
        meta.lore(loreStrings.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                                .replace("{time}", kickText))
                        .decoration(TextDecoration.ITALIC, false)
        ).toList());
        item.setItemMeta(meta);
        return item;
    }

    private Icon kickSettings(){
        Icon icon = new Icon(kickSettingsItem(), (self, player) -> self.itemStack = kickSettingsItem());

        icon.addRightClickAction((player) -> {
            if(!player.hasPermission("easyclans.settings.kick_time")) {
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                return;
            }
            kickTime = -1;
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
        });

        icon.addLeftClickAction((player) -> {
            if(!player.hasPermission("easyclans.settings.kick_time")) {
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                return;
            }
            new AbstractChatUtil(player, (meow) -> {
                try{
                    int lkickTime = Integer.parseInt(meow.message());
                    if(lkickTime < -1){
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    }else{
                        player.sendMessage(" " +lkickTime);
                        kickTime = lkickTime;
                        player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    }
                }catch (NumberFormatException e){
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() -> {
                icon.itemStack = kickSettingsItem();
                open(player);
            });
        });


        return icon;
    }


    private ItemStack tagSettingsItem(){
        var item = new ItemStack(Material.BOOK);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.tag.name"))
                .decoration(TextDecoration.ITALIC, false)
        );
        var loreStrings = LanguageController.getLocalizedList("create.menu.tag.lore");
        meta.lore(loreStrings.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                                .replace("{tag}", tag))
                        .decoration(TextDecoration.ITALIC, false)
        ).toList());
        item.setItemMeta(meta);
        return item;
    }


    private Icon tagSettngsIcon(){
        var icon = new Icon(tagSettingsItem(), (self, player) -> {
            self.itemStack = tagSettingsItem();
        });

        icon.addLeftClickAction((player) -> {

            new AbstractChatUtil(player, (meow) -> {
                var stripped = meow.message().replace(" ", "_").strip().trim();
                if(stripped.length() != 2){
                    // not good
                    player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.tag.invalid")));
                    return;
                }

                tag = stripped;
            }, plugin).setOnClose(() -> {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    open(player);
                    refresh(player);
                }, 5L);
            });
            player.closeInventory();

        });


        icon.addRightClickAction((player) -> {
            tag = String.valueOf(name.charAt(0)) + name.charAt(1);
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            refresh(player);
        });

        return icon;
    }


    /*private Icon payoutSettings(){
        moneyPrice = 1000;
    }*/


    private void init() {
        if (banner == null) {
            banner = BannerUtils.generateRandomBanner();
        }

        addIcon(13, clanBanner());
        addIcon(40, confirmButton());
        addIcon(20, priceSettings());
        addIcon(24, kickSettings());
        addIcon(30, tagSettngsIcon());
        /*addIcon(31, payoutSettings());*/
    }







    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
