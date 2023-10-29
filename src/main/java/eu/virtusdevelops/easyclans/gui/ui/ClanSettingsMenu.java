package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.UserPermissions;
import eu.virtusdevelops.easyclans.utils.AbstractChatUtil;
import eu.virtusdevelops.easyclans.utils.BannerUtils;
import eu.virtusdevelops.easyclans.utils.ItemUtils;
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
    private final PlayerController playerController;
    private final CurrenciesController currenciesController;
    private final RequestsController requestsController;
    private final InvitesController invitesController;
    private final LogController logController;
    private final ClansPlugin plugin;


    public ClanSettingsMenu(Player player, Clan clan, ClansController clansController, PlayerController playerController, CurrenciesController currenciesController,
                            RequestsController requestsController, InvitesController invitesController, LogController logController, ClansPlugin plugin) {
        super(player, 54, LanguageController.getLocalized("clan_settings_menu.title"));
        this.clansController = clansController;
        this.playerController = playerController;
        this.currenciesController = currenciesController;
        this.requestsController = requestsController;
        this.invitesController = invitesController;
        this.logController = logController;
        this.plugin = plugin;
        this.cPlayer = playerController.getPlayer(player.getUniqueId());
        this.clan = clan;


        init();
        fancyBackground();
        open();
    }


    private void init() {

        addIcon(11, joinPriceIcon());
        addIcon(13, nameIcon());
        addIcon(15, bannerIcon());
        addIcon(29, kickTimeIcon());
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
                .map(it -> ClansPlugin.MM.deserialize(it).decoration(TextDecoration.ITALIC, false)).toList());
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
            var newBanner = item.clone();
            newBanner = ItemUtils.removeEnchants(newBanner);
            newBanner = ItemUtils.strip(newBanner);
            clan.setBanner(newBanner);
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            refresh();
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
            new AbstractChatUtil(target, (event) -> {
                try {
                    double price = Double.parseDouble(event.message());
                    if (price < 0) {
                        target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.join_price_item.invalid_message")));
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        return;
                    }
                    clan.setJoinMoneyPrice(price);
                    target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
                } catch (NumberFormatException e) {
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.join_price_item.invalid_message")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() -> {
                open();
                refresh();
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
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.kicktime_item.message")));
            new AbstractChatUtil(target, (event) -> {
                try {
                    int time = Integer.parseInt(event.message());
                    if (time < -1) {
                        target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.kicktime_item.invalid_message")));
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        return;
                    }
                    clan.setAutoKickTime(time == 0 ? -1 : time * 1000);
                    target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
                } catch (NumberFormatException e) {
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.kicktime_item.invalid_message")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() -> {
                open();
                refresh();
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


            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.message")));
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
                clan.setTag(clanTag);
                target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));

            }, plugin).setOnClose(() -> {
                open();
                refresh();
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
            new AbstractChatUtil(target, (event) -> {
                var name = event.message();
                var stripped = name.replace(" ", "_").strip().trim();
                if (stripped.length() > plugin.getConfig().getInt("clan.display_name_max_length")
                        || stripped.length() < plugin.getConfig().getInt("clan.display_name_min_length")) {
                    // not good
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.invalid_display_name")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }

                clan.setDisplayName(stripped);
                target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));

            }, plugin).setOnClose(() -> {
                open();
                refresh();
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
            new AbstractChatUtil(target, (event) -> {
                var name = event.message();
                var stripped = name.replace(" ", "_").strip().trim();
                if (stripped.length() > plugin.getConfig().getInt("clan.tag_max_length")
                        || stripped.length() < plugin.getConfig().getInt("clan.tag_min_length")) {
                    // not good
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.invalid_tag")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }

                clan.setTag(stripped);
                target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));

            }, plugin).setOnClose(() -> {
                open();
                refresh();
            });
        });

        return icon;
    }
    // </editor-fold>

}
