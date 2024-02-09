package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.UserPermissions;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanMenu extends GUI {
    private final CPlayer cPlayer;
    private final Clan clan;
    private final ClansController clansController;
    private final PlayerController playerController;
    private final CurrenciesController currenciesController;
    private final RequestsController requestsController;
    private final InvitesController invitesController;
    private final RanksController ranksController;
    private final LogController logController;
    private final ClansPlugin plugin;

    public ClanMenu(Player player, Clan clan, ClansController clansController, PlayerController playerController, CurrenciesController currenciesController,
                    RequestsController requestsController, InvitesController invitesController, LogController logController, RanksController ranksController, ClansPlugin plugin){
        super(player, 54, LanguageController.getLocalized("clan_menu.title"));

        this.clan = clan;
        this.clansController = clansController;
        this.playerController = playerController;
        this.currenciesController = currenciesController;
        this.requestsController = requestsController;
        this.invitesController = invitesController;
        this.logController = logController;
        this.ranksController = ranksController;
        this.plugin = plugin;
        this.cPlayer = playerController.getPlayer(player.getUniqueId());

        init();
        fancyBackground();
        open();
    }

    private void init(){
        addIcon(31, settingsIcon());
        addIcon(29, membersIcon());
        addIcon(33, requestsIcon());
        addIcon(15, invitesIcon());
        addIcon(11, bankIcon());
        addIcon(13, infoIcon());
    }

    // <editor-fold desc="Settings icon">
    private ItemStack settingsItem(){
        var item = new ItemStack(Material.ANVIL);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                LanguageController.getLocalized("clan_menu.settings_item.title")
                        .replace("{clan}", clan.getName())
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("clan_menu.settings_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{clan}", clan.getName())
        )).toList());
        item.setItemMeta(meta);


        return item;
    }
    private Icon settingsIcon(){
        var icon = new Icon(settingsItem(), (self, target) -> {
            self.itemStack = settingsItem();
        });

        icon.setVisibilityCondition((target, self) ->
                target.getUniqueId().equals(clan.getOwner())
                        || target.hasPermission("easyclans.admin.settings")
                        || (cPlayer.hasPermission(UserPermissions.CLAN_SETTINGS) && cPlayer.getClanID().equals(clan.getId()))
        );

        icon.addClickAction((target) -> {
            // open settings menu
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            new ClanSettingsMenu(target, clan, clansController,playerController, currenciesController, requestsController, invitesController, logController, plugin, this);
        });

        return icon;
    }
    // </editor-fold>

    // <editor-fold desc="Members icon">
    private ItemStack membersItem(){
        var item = new ItemStack(Material.PLAYER_HEAD);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_menu.members_item.title")
                                .replace("{clan}", clan.getName())
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("clan_menu.members_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{clan}", clan.getName())
        )).toList());
        item.setItemMeta(meta);


        return item;
    }
    private Icon membersIcon(){
        var icon = new Icon(membersItem(), (self, target) -> {
            self.itemStack = membersItem();
        });

        icon.setVisibilityCondition((target, self) ->
                target.getUniqueId().equals(clan.getOwner())
                        || target.hasPermission("easyclans.admin.members")
                        || (cPlayer.hasPermission(UserPermissions.VIEW_MEMBERS) && cPlayer.getClanID() == clan.getId())
        );

        icon.addClickAction((target) -> {
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            new MembersMenu(
                    player,
                    clan,
                    clansController,
                    playerController,
                    currenciesController,
                    requestsController,
                    invitesController,
                    logController,
                    ranksController,
                    plugin,
                    this
            );
        });

        return icon;
    }
    // </editor-fold>


    // <editor-fold desc="Requests icon">
    private ItemStack requestsItem(){
        var item = new ItemStack(Material.BOOK);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_menu.requests_item.title")
                                .replace("{clan}", clan.getName())
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("clan_menu.requests_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{clan}", clan.getName())
        )).toList());
        item.setItemMeta(meta);


        return item;
    }
    private Icon requestsIcon(){
        var icon = new Icon(requestsItem(), (self, target) -> {
            self.itemStack = requestsItem();
        });

        icon.setVisibilityCondition((target, self) ->
                target.getUniqueId().equals(clan.getOwner())
                        || target.hasPermission("easyclans.admin.requests")
                        || (cPlayer.hasPermission(UserPermissions.VIEW_REQUESTS) && cPlayer.getClanID().equals(clan.getId()))
        );

        icon.addClickAction((target) -> {
            // open requests menu
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            new RequestsMenu(player, clan, clansController, playerController, currenciesController, requestsController, invitesController, logController, plugin, this);
        });

        return icon;
    }
    // </editor-fold>

    // <editor-fold desc="Invites icon">
    private ItemStack invitesItem(){
        var item = new ItemStack(Material.BOOK);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_menu.invites_item.title")
                                .replace("{clan}", clan.getName())
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("clan_menu.invites_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{clan}", clan.getName())
        )).toList());
        item.setItemMeta(meta);


        return item;
    }
    private Icon invitesIcon(){
        var icon = new Icon(invitesItem(), (self, target) -> {
            self.itemStack = invitesItem();
        });

        icon.setVisibilityCondition((target, self) ->
                target.getUniqueId().equals(clan.getOwner())
                        || target.hasPermission("easyclans.admin.invites")
                        || (cPlayer.hasPermission(UserPermissions.VIEW_INVITES) && cPlayer.getClanID().equals(clan.getId()))
        );

        icon.addClickAction((target) -> {
            // open invites menu
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
        });

        return icon;
    }
    // </editor-fold>

    // <editor-fold desc="Invites icon">
    private ItemStack bankItem(){
        var item = new ItemStack(Material.SUNFLOWER);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_menu.bank_item.title")
                                .replace("{clan}", clan.getName())
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("clan_menu.bank_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{clan}", clan.getName())
        )).toList());
        item.setItemMeta(meta);


        return item;
    }
    private Icon bankIcon(){
        var icon = new Icon(bankItem(), (self, target) -> {
            self.itemStack = bankItem();
        });

        icon.setVisibilityCondition((target, self) ->
                target.getUniqueId().equals(clan.getOwner())
                        || target.hasPermission("easyclans.admin.bank")
                        || (cPlayer.hasPermission(UserPermissions.VIEW_BANK) && cPlayer.getClanID().equals(clan.getId()))
        );

        icon.addClickAction((target) -> {
            // open bank menu
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            new ClanBankMenu(player, clan, clansController, playerController, currenciesController, requestsController, invitesController, logController, plugin, this);
        });

        return icon;
    }
    // </editor-fold>


    // <editor-fold desc="Invites icon">
    private ItemStack infoItem(){
        var item = clan.getBanner().clone();
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_menu.info_item.title")
                                .replace("{clan}", clan.getName())
                                .replace("{display_name}", clan.getDisplayName())
                                .replace("{tag}", clan.getTag())
                                .replace("{interest_clan}", new DecimalFormat("#.#####").format(clan.getInterestRate()))
                                .replace("{interest_members}", new DecimalFormat("#.#####").format(clan.getActualInterestRate()))
                                .replace("{interest_total}", new DecimalFormat("#.#####").format(clan.getInterestRate() + clan.getActualInterestRate()))
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("clan_menu.info_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{clan}", clan.getName())
                .replace("{display_name}", clan.getDisplayName())
                .replace("{tag}", clan.getTag())
                .replace("{interest_clan}", new DecimalFormat("#.#####").format(clan.getInterestRate()))
                .replace("{interest_members}", new DecimalFormat("#.#####").format(clan.getActualInterestRate()))
                .replace("{interest_total}", new DecimalFormat("#.#####").format(clan.getInterestRate() + clan.getActualInterestRate()))
        ).decoration(TextDecoration.ITALIC, false)).toList());
        item.setItemMeta(meta);

        return item;
    }
    private Icon infoIcon(){
        var icon = new Icon(infoItem(), (self, target) -> {
            self.itemStack = infoItem();
        });

        icon.setVisibilityCondition((target, self) ->
                target.getUniqueId().equals(clan.getOwner())
                        || target.hasPermission("easyclans.admin.info")
                        || (cPlayer.hasPermission(UserPermissions.VIEW_CLAN_INFO) && cPlayer.getClanID().equals(clan.getId()))
        );

        icon.addClickAction((target) -> {
            refresh();
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
        });

        return icon;
    }
    // </editor-fold>

}
