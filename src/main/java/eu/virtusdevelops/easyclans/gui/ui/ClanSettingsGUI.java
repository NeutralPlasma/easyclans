package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.controller.CurrenciesController;
import eu.virtusdevelops.easyclans.controller.LogController;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.utils.AbstractChatUtil;
import eu.virtusdevelops.easyclans.utils.Formatter;
import eu.virtusdevelops.easyclans.utils.PlayerUtils;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.ClansController;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.models.LogType;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static eu.virtusdevelops.easyclans.controller.LanguageController.getLocalizedList;
import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanSettingsGUI extends GUI {

    private final Player player;
    private final Clan clan;
    private final ClansController clansController;
    private final GUI previous;
    private final ClansPlugin plugin;
    private final LogController logController;
    private final CurrenciesController currenciesController;

    public ClanSettingsGUI(Player player, Clan clan, ClansController clansController,
                           GUI previous, ClansPlugin plugin, LogController logController,
                           CurrenciesController currenciesController) {
        super(45, LanguageController.getLocalized("settings.menu.title"));
        this.player = player;
        this.clan = clan;
        this.clansController = clansController;
        this.previous = previous;
        this.plugin = plugin;
        this.logController = logController;
        this.currenciesController = currenciesController;

        addCloseAction((ignored) -> {
            clansController.updateClan(clan);
            if (previous != null) {
                previous.open(player);
                previous.refresh(player);
            }
        });

        init();
        fancyBackground();

        open(player);

    }

    private void init() {
        setIcon(11, kickTimeIcon());
        setIcon(13, nameIcon());
        setIcon(15, joinPriceIcon());
        setIcon(31, bannerIcon());
        setIcon(33, tagSettngsIcon());
    }


    // kick time

    private ItemStack kickTimeItem() {
        var item = new ItemStack(Material.BOOK);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("settings.menu.kickIcon.title")));
        var loreText = getLocalizedList("settings.menu.kickIcon.lore");
        var kickText = clan.getAutoKickTime() == -1 ? LanguageController.getLocalized("disabled") : DurationFormatUtils.formatDurationWords(clan.getAutoKickTime(), true, true);
        meta.lore(loreText.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                        .replace("{time}", kickText)
                )
        ).toList());

        item.setItemMeta(meta);
        return item;
    }


    private Icon kickTimeIcon() {
        var icon = new Icon(kickTimeItem(), (self, player) -> {
            self.itemStack = kickTimeItem();
        });
        icon.addLeftClickAction((player1 -> {
            if (!player.hasPermission("easyclans.settings.kick_time")) {
                player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }
            setForceClose(true);
            new AbstractChatUtil(player, (event) -> {
                try {
                    int value = Integer.parseInt(event.message());
                    if (value < -1) {
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        return;
                    }
                    logController.addLog(new Log("kickTime:" + value, player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));

                    clan.setAutoKickTime(value);
                    player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                } catch (NumberFormatException e) {
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() -> {
                setForceClose(false);
                open(player);
                refresh(player);
            });
        }));

        icon.addRightClickAction(player1 -> {
            if (!player.hasPermission("easyclans.settings.kick_time")) {
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                return;
            }
            clan.setAutoKickTime(7 * 24 * 60 * 60 * 1000); // 7 days
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            refresh(player);
        });

        return icon;
    }


    // join price

    private ItemStack joinPriceItem() {
        var item = new ItemStack(Material.BOOK);

        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("settings.menu.priceIcon.title")));

        var loreText = getLocalizedList("settings.menu.priceIcon.lore");
        meta.lore(loreText.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                        .replace("{price:money}", Formatter.formatMoney(clan.getJoinMoneyPrice()))
                )
        ).toList());
        item.setItemMeta(meta);

        return item;
    }


    private Icon joinPriceIcon() {
        var icon = new Icon(joinPriceItem(), (self, player) -> {
            self.itemStack = joinPriceItem();
        });

        icon.addLeftClickAction((player1 -> {
            if (!player.hasPermission("easyclans.settings.join_price")) {
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                return;
            }
            setForceClose(true);
            new AbstractChatUtil(player, (event) -> {
                try {
                    double value = Double.parseDouble(event.message());
                    if (value < 0) {
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        return;
                    }
                    clan.setJoinMoneyPrice(value);
                    player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    logController.addLog(new Log("joinPrice:" + value, player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
                } catch (NumberFormatException e) {
                    player.sendMessage(ClansPlugin.MM.deserialize(
                            LanguageController.getLocalized("invalid_amount")
                    ));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() -> {
                setForceClose(false);
                open(player);
                refresh(player);
            });
        }));


        icon.addRightClickAction(player1 -> {
            if (!player.hasPermission("easyclans.settings.join_price")) {
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                return;
            }
            clan.setJoinMoneyPrice(10000.0);
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            refresh(player);
        });


        return icon;
    }


    // name
    private ItemStack nameItem() {
        var item = new ItemStack(Material.BOOK);

        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("settings.menu.nameIcon.title")));

        var loreText = getLocalizedList("settings.menu.nameIcon.lore");
        meta.lore(loreText.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                        .replace("{name}", clan.getName())
                        .replace("{price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.name.change_price.money")))
                )
        ).toList());
        item.setItemMeta(meta);

        return item;
    }


    private Icon nameIcon() {
        var icon = new Icon(nameItem(), (self, player) -> {
            self.itemStack = nameItem();
        });

        icon.addLeftClickAction((player1 -> {
            setForceClose(true);
            new AbstractChatUtil(player, (event) -> {
                if (event.message().isBlank() || event.message().isEmpty() || event.message().length() < 3) {
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }
                var stripped = event.message().trim().replace(" ", "_");
                if(stripped.length() < plugin.getConfig().getInt("clan.min_name_length")
                        || stripped.length() > plugin.getConfig().getInt("clan.max_name_length")){
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }

                for(var clan : clansController.getClans()){
                    if(clan.getName().equalsIgnoreCase(stripped)){
                        player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.banner.invalid_name")));
                        return;
                    }
                }

                if (currenciesController.getProvider("Vault").getValue(player) >= plugin.getConfig().getDouble("clan.name.change_price.money")) {
                    currenciesController.getProvider("Vault").removeValue(player, plugin.getConfig().getDouble("clan.name.change_price.money"));
                    //ClansPlugin.Economy.withdrawPlayer(player, plugin.getConfig().getDouble("clan.name.change_price.money"));
                    var oldName = clan.getName();
                    clan.setName(stripped);
                    player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    logController.addLog(new Log("name:" + clan.getName() + ":" + oldName, player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
                } else {
                    player.sendMessage(ClansPlugin.MM.deserialize(
                            LanguageController.getLocalized("not_enough_money")
                                    .replace("{price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.name.change_price.money")))
                    ));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() -> {
                setForceClose(false);
                open(player);
                refresh(player);
            });
        }));


        icon.addRightClickAction(player1 -> {
            // add money check maybe?
            new ConfirmGUI(player, (confirm) -> {
                clan.setName(player.getName());
                player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                open(player);
                refresh(player);
            }, (cancel) -> {
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                open(player);
                refresh(player);
            }, LanguageController.getLocalized("settings.menu.confirm_name_reset"));


        });


        return icon;
    }


    // display name


    // banner
    private ItemStack bannerItem() {
        var item = clan.getBanner().clone();
        var meta = item.getItemMeta();

        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("settings.menu.bannerIcon.title")));

        var loreText = getLocalizedList("settings.menu.bannerIcon.lore");
        meta.lore(loreText.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                        .replace("{buy_price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.banner.buy_price.money")))
                        .replace("{change_price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.banner.change_price.money")))

                )
        ).toList());
        item.setItemMeta(meta);

        return item;
    }


    private Icon bannerIcon() {
        var icon = new Icon(bannerItem(), (self, player) -> {
            self.itemStack = bannerItem();
        });

        icon.addLeftClickAction((player1 -> {

            if (currenciesController.getProvider("Vault").getValue(player) >= plugin.getConfig().getDouble("clan.banner.buy_price.money")) {
                currenciesController.getProvider("Vault").removeValue(player, plugin.getConfig().getDouble("clan.banner.buy_price.money"));
                //ClansPlugin.Economy.withdrawPlayer(player, plugin.getConfig().getDouble("clan.banner.buy_price.money"));
                PlayerUtils.giveItem(player, clan.getBanner().clone(), true);
            } else {
                player.sendMessage(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("not_enough_money")
                                .replace("{price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.banner.buy_price.money")))
                ));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
            }

        }));


        icon.addRightClickAction(player1 -> {
            // DISABLED
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
                                .replace("{tag}", clan.getTag()))
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
            setForceClose(true);
            new AbstractChatUtil(player, (meow) -> {
                var stripped = meow.message().replace(" ", "_").strip().trim();
                if(stripped.length() > plugin.getConfig().getInt("clan.tag_max_length")
                        || stripped.length() < plugin.getConfig().getInt("clan.tag_min_length")){
                    // not good
                    player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.tag.invalid")));
                    return;
                }

                if(currenciesController.getProvider("Vault").getValue(player) < plugin.getConfig().getDouble("clan.tag.change_price.money")){
                    player.sendMessage(ClansPlugin.MM.deserialize(
                            LanguageController.getLocalized("not_enough_money")
                                    .replace("{price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.tag.change_price.money")))
                    ));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
                currenciesController.getProvider("Vault").removeValue(player, plugin.getConfig().getDouble("clan.tag.change_price.money"));
                var oldTag = clan.getTag();
                clan.setTag(stripped);
                logController.addLog(new Log("tag:" + clan.getTag() + ":" + oldTag, player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
            }, plugin).setOnClose(() -> {
                setForceClose(false);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    open(player);
                    refresh(player);
                }, 5L);
            });
            player.closeInventory();

        });


        icon.addRightClickAction((player) -> {
            clan.setTag(String.valueOf(clan.getName().charAt(0)) + clan.getName().charAt(1));
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            refresh(player);
        });

        return icon;
    }


}
