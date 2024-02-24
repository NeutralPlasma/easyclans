package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.models.*;
import eu.virtusdevelops.easyclans.utils.*;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.file.attribute.UserPrincipalLookupService;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanSettingsMenu extends GUI {
    private final CPlayer cPlayer;
    private final Clan clan;
    private final ClansController clansController;
    private final CurrenciesController currenciesController;
    private final LogController logController;
    private final ClansPlugin plugin;
    private final GUI previousUI;


    public ClanSettingsMenu(Player player, Clan clan, ClansPlugin plugin, GUI previusUI) {
        super(player, 54, LanguageController.getLocalized("clan_settings_menu.title"));
        this.clansController = plugin.getClansController();
        this.currenciesController = plugin.getCurrenciesController();
        this.logController = plugin.getLogController();
        this.plugin = plugin;
        this.cPlayer =  plugin.getPlayerController().getPlayer(player.getUniqueId());
        this.clan = clan;
        this.previousUI = previusUI;


        init();
        fancyBackground();
        open();
    }


    private void init() {

        addIcon(11, joinPriceIcon());
        addIcon(13, nameIcon());
        addIcon(15, bannerIcon());
        addIcon(29, kickTimeIcon());
        addIcon(31, pvpIcon());

        if(previousUI != null)
            addCloseAction((target) -> {
                previousUI.open();
            });
    }


    // <editor-fold desc="Banner icon">
    private void randomizeBanner() {
        clan.setBanner(BannerUtils.generateRandomBanner());
    }

    private ItemStack bannerItem() {
        var item = clan.getBanner().clone();
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_settings_menu.banner_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("clan_settings_menu.banner_item.lore").stream()
                .map(it -> ClansPlugin.MM.deserialize(it
                        .replace("{price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.banner.buy_price.money")))
                        .replace("{change_price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.banner_change_price")))
                ).decoration(TextDecoration.ITALIC, false)).toList());
        item.setItemMeta(meta);


        return item;
    }

    private Icon bannerIcon() {
        var icon = new Icon(bannerItem(), (self, target) -> {
            self.itemStack = bannerItem();
        });
        icon.addShiftLeftClickAction((target) -> {
            if (!target.hasPermission("easyclans.edit.banner")
                    ||
                    (!target.getUniqueId().equals(clan.getOwner())
                            &&
                            !cPlayer.hasPermission(UserPermissions.CHANGE_BANNER)
                    )) {
                target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            randomizeBanner();
            clansController.updateClan(clan);
            logController.addLog(new Log("banner:randomized", player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
            refresh();
        });
        icon.addShiftRightClickAction((target) -> {
            if (!target.hasPermission("easyclans.edit.banner")
                    || (!target.getUniqueId().equals(clan.getOwner())
                    && !cPlayer.hasPermission(UserPermissions.CHANGE_BANNER)
            )) {
                target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            randomizeBanner();
            clansController.updateClan(clan);
            logController.addLog(new Log("banner:randomized", player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
            refresh();
        });
        icon.addDragItemAction((target, item) -> {
            if (!target.hasPermission("easyclans.edit.banner")
                    || (!target.getUniqueId().equals(clan.getOwner())
                    && !cPlayer.hasPermission(UserPermissions.CHANGE_BANNER)
            )) {
                target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            if (!item.getType().toString().endsWith("BANNER")) {
                target.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            var provider = currenciesController.getProvider("Vault");
            if(provider.getValue(player) < plugin.getConfig().getDouble("clan.banner_change_price")){
                target.sendMessage(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("not_enough_money")
                                .replace("{price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.banner_change_price")))
                ));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }


            provider.removeValue(target, plugin.getConfig().getDouble("clan.banner_change_price"));
            var newBanner = item.clone();
            newBanner = ItemUtils.removeEnchants(newBanner);
            newBanner = ItemUtils.strip(newBanner);
            clan.setBanner(newBanner);
            clansController.updateClan(clan);
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            logController.addLog(new Log("banner:updated", player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
            refresh();
        });


        icon.addRightClickAction((target) -> {
            if (!target.hasPermission("easyclans.buy.banner")
                    || (!target.getUniqueId().equals(clan.getOwner())
                    && !cPlayer.hasPermission(UserPermissions.BUY_BANNER)
            )) {
                target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }
            var provider = currenciesController.getProvider("Vault");
            var required = plugin.getConfig().getDouble("clan.banner.buy_price.money"); // TODO in future make payments possible with any provider
            if(provider.getValue(target) < required){
                target.sendMessage(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("currencies.not_enough")
                                .replace("{price}", Formatter.formatMoney(required))
                                .replace("{currency}", plugin.getConfig().getString("currency.Vault.symbol"))
                ));
                target.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }
            provider.removeValue(target, required);
            var banner = clan.getBanner().clone();
            PlayerUtils.giveItem(target, banner, true);
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));

        });

        return icon;
    }
    // </editor-fold>

    // <editor-fold desc="Join price icon">
    private ItemStack joinPriceItem() {
        var item = new ItemStack(Material.BOOK);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_settings_menu.join_price_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("clan_settings_menu.join_price_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{price}", String.valueOf(clan.getJoinMoneyPrice()))
        )).toList());
        item.setItemMeta(meta);


        return item;
    }

    private Icon joinPriceIcon() {
        var icon = new Icon(joinPriceItem(), (self, target) -> {
            self.itemStack = joinPriceItem();
        });


        icon.addClickAction((target) -> {

            if (!target.hasPermission("easyclans.edit.join_price")
                    || (!target.getUniqueId().equals(clan.getOwner())
                    && !cPlayer.hasPermission(UserPermissions.EDIT_JOIN_PRICE)
            )) {
                target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.join_price_item.message")));
            setForceClose(true);
            new AbstractChatUtil(target, (event) -> {
                try {
                    double price = Double.parseDouble(event.message());
                    if (price < 0) {
                        target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.join_price_item.invalid_message")));
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        return;
                    }
                    logController.addLog(new Log("join_price:" + clan.getJoinMoneyPrice() + ":" + price, player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
                    clan.setJoinMoneyPrice(price);
                    target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
                } catch (NumberFormatException e) {
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.join_price_item.invalid_message")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() -> {
                open();
                refresh();
                setForceClose(false);
            });

        });

        return icon;
    }
    // </editor-fold>

    // <editor-fold desc="Kick icon">
    private ItemStack kickTimeItem() {
        var item = new ItemStack(Material.ANVIL);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_settings_menu.kicktime_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );
        var kickTime = clan.getAutoKickTime();
        var kickText = kickTime <= 0 ? LanguageController.getLocalized("disabled") : DurationFormatUtils.formatDurationWords(kickTime, true, true);

        meta.lore(LanguageController.getLocalizedList("clan_settings_menu.kicktime_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{time}", kickText)
        )).toList());
        item.setItemMeta(meta);


        return item;
    }

    private Icon kickTimeIcon() {
        var icon = new Icon(kickTimeItem(), (self, target) -> {
            self.itemStack = kickTimeItem();
        });

        icon.addClickAction((target) -> {

            if (!target.hasPermission("easyclans.edit.inactive_kick")
                    || (!target.getUniqueId().equals(clan.getOwner())
                    && !cPlayer.hasPermission(UserPermissions.EDIT_INACTIVE_KICK)
            )) {
                target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_settings_menu.kicktime_item.message")));
            setForceClose(true);
            new AbstractChatUtil(target, (event) -> {
                try {
                    int time = Integer.parseInt(event.message());
                    if (time < -1) {
                        target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_settings_menu.kicktime_item.invalid_message")));
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        return;
                    }
                    logController.addLog(new Log("kick_time:" + clan.getAutoKickTime() + ":" + time, player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
                    clan.setAutoKickTime(time == 0 ? -1 : time == -1 ? time : time*1000);
                    target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
                } catch (NumberFormatException e) {
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_settings_menu.kicktime_item.invalid_message")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() -> {
                open();
                refresh();
                setForceClose(false);
            });

        });

        return icon;
    }
    // </editor-fold>

    // <editor-fold desc="Name icon">
    private ItemStack nameItem() {
        var item = new ItemStack(Material.WRITABLE_BOOK);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_settings_menu.name_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );

        meta.lore(LanguageController.getLocalizedList("clan_settings_menu.name_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{name}", clan.getName())
                .replace("{display_name}", clan.getDisplayName())
                .replace("{tag}", clan.getTag())
                .replace("{name_change_price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.name_change_price")))
                .replace("{display_name_change_price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.display_name_change_price")))
                .replace("{tag_change_price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.tag_change_price")))
        )).toList());
        item.setItemMeta(meta);


        return item;
    }

    private Icon nameIcon() {
        var icon = new Icon(nameItem(), (self, target) -> {
            self.itemStack = nameItem();
        });

        /*icon.setVisibilityCondition((target, self) ->
                target.hasPermission("easyclans.edit.clan_name")
                    && (target.getUniqueId().equals(clan.getOwner())
                        || cPlayer.hasPermission(UserPermissions.EDIT_CLAN_NAME)
                        )
        );*/



        icon.addLeftClickAction((target) -> {
            if (!target.hasPermission("easyclans.edit.clan_name")
                    || (!target.getUniqueId().equals(clan.getOwner())
                    && !cPlayer.hasPermission(UserPermissions.EDIT_CLAN_NAME)
            )) {
                target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            var provider = currenciesController.getProvider("Vault");
            if(provider.getValue(player) < plugin.getConfig().getDouble("clan.name_change_price")){
                target.sendMessage(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("not_enough_money")
                                .replace("{price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.name_change_price")))
                ));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }




            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.message")));
            setForceClose(true);
            new AbstractChatUtil(target, (event) -> {
                var name = event.message();
                var stripped = name.replace(" ", "_").strip().trim();
                if (stripped.length() > plugin.getConfig().getInt("clan.max_name_length")
                        || stripped.length() < plugin.getConfig().getInt("clan.min_name_length")) {
                    // not good
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.invalid_name")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }
                if (clansController.getClan(stripped) != null) {
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.invalid_name")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }
                clan.setName(stripped);
                var clanTag = "";
                for (int i = 0; i < plugin.getConfig().getInt("clan.tag_max_length"); i++) {
                    if (clan.getName().length() > i)

                        clanTag += String.valueOf(clan.getName().charAt(i));
                    else
                        clanTag += String.valueOf(clan.getName().charAt(0));
                }
                provider.removeValue(target, plugin.getConfig().getDouble("clan.name_change_price"));
                clan.setTag(clanTag);
                logController.addLog(new Log("name:" + stripped, player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
                logController.addLog(new Log("tag:" + clanTag, player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
                clansController.updateClan(clan);
                target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));

            }, plugin).setOnClose(() -> {
                open();
                refresh();
                setForceClose(false);
            });
        });

        icon.addRightClickAction((target) -> {

            if (!target.hasPermission("easyclans.edit.clan_display_name")
                    || (!target.getUniqueId().equals(clan.getOwner())
                    && !cPlayer.hasPermission(UserPermissions.EDIT_CLAN_DISPLAY_NAME)
            )) {
                target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.display_name_message")));
            setForceClose(true);
            new AbstractChatUtil(target, (event) -> {

                var provider = currenciesController.getProvider("Vault");
                if(provider.getValue(player) < plugin.getConfig().getDouble("clan.display_name_change_price")){
                    target.sendMessage(ClansPlugin.MM.deserialize(
                            LanguageController.getLocalized("not_enough_money")
                                    .replace("{price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.display_name_change_price")))
                    ));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }

                var name = event.message();
                var stripped = name.replace(" ", "_").strip().trim();
                if (stripped.length() > plugin.getConfig().getInt("clan.display_name_max_length")
                        || stripped.length() < plugin.getConfig().getInt("clan.display_name_min_length")) {
                    // not good
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.invalid_display_name")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }
                provider.removeValue(target, plugin.getConfig().getDouble("clan.display_name_change_price"));
                clan.setDisplayName(stripped);
                clansController.updateClan(clan);
                logController.addLog(new Log("displayname:" + stripped, player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
                target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));

            }, plugin).setOnClose(() -> {
                open();
                refresh();
                setForceClose(true);
            });
        });

        icon.addShiftLeftClickAction((target) -> {

            if (!target.hasPermission("easyclans.edit.clan_tag")
                    || (!target.getUniqueId().equals(clan.getOwner())
                    && !cPlayer.hasPermission(UserPermissions.EDIT_CLAN_TAG)
            )) {
                target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.tag_message")));
            setForceClose(true);
            new AbstractChatUtil(target, (event) -> {

                var provider = currenciesController.getProvider("Vault");
                if(provider.getValue(player) < plugin.getConfig().getDouble("clan.tag_change_price")){
                    target.sendMessage(ClansPlugin.MM.deserialize(
                            LanguageController.getLocalized("not_enough_money")
                                    .replace("{price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.tag_change_price")))
                    ));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }

                var name = event.message();
                var stripped = name.replace(" ", "_").strip().trim();
                if (stripped.length() > plugin.getConfig().getInt("clan.tag_max_length")
                        || stripped.length() < plugin.getConfig().getInt("clan.tag_min_length")) {
                    // not good
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.invalid_tag")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }
                provider.removeValue(target, plugin.getConfig().getDouble("clan.tag_change_price"));
                clan.setTag(stripped);
                clansController.updateClan(clan);
                logController.addLog(new Log("tag:" + stripped, player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
                target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));

            }, plugin).setOnClose(() -> {
                open();
                refresh();
                setForceClose(false);
            });
        });

        return icon;
    }
    // </editor-fold>

    // <editor-fold desc="Pvp icon">
    private ItemStack pvpItem() {
        var material = clan.isPvpEnabled() ? Material.DIAMOND_SWORD : Material.SHIELD;
        var item = new ItemStack(material);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_settings_menu.pvp_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );
        var text = clan.isPvpEnabled() ? LanguageController.getLocalized("enabled") : LanguageController.getLocalized("disabled");

        meta.lore(LanguageController.getLocalizedList("clan_settings_menu.pvp_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{status}", text)
        )).toList());
        item.setItemMeta(meta);


        return item;
    }

    private Icon pvpIcon() {
        var icon = new Icon(pvpItem(), (self, target) -> {
            self.itemStack = pvpItem();
        });

        icon.addClickAction((target) -> {

            if (!target.hasPermission("easyclans.edit.pvp")
                    || (!target.getUniqueId().equals(clan.getOwner())
                    && !cPlayer.hasPermission(UserPermissions.TOGGLE_PVP)
            )) {
                target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));

            logController.addLog(new Log("pvp:" + clan.isPvpEnabled() + ":" + !clan.isPvpEnabled(), player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
            clan.setPvpEnabled(!clan.isPvpEnabled());
            var text = clan.isPvpEnabled() ? LanguageController.getLocalized("enabled") : LanguageController.getLocalized("disabled");
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_settings_menu.pvp_item.message")
                    .replace("{status}", text)
            ));
            logController.addLog(new Log("pvp:" + clan.isPvpEnabled(), player.getUniqueId(), clan.getId(), LogType.CLAN_SETTING_CHANGED));
            clansController.updateClan(clan);
            refresh();
        });

        return icon;
    }
    // </editor-fold>
}
