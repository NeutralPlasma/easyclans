package eu.virtusdevelops.easyclans.gui.ui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.models.*;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class MemberSettingsMenu extends GUI {

    private final CPlayer cPlayer;
    private final CPlayer cTarget;
    private final Clan clan;
    private final ClansController clansController;
    private final PlayerController playerController;
    private final CurrenciesController currenciesController;
    private final RequestsController requestsController;
    private final InvitesController invitesController;
    private final LogController logController;
    private final ClansPlugin plugin;
    private final GUI previousUI;
    private final SimpleDateFormat sdf;

    public MemberSettingsMenu(Player player, Clan clan, CPlayer target, ClansController clansController, PlayerController playerController, CurrenciesController currenciesController,
                              RequestsController requestsController, InvitesController invitesController, LogController logController, ClansPlugin plugin, GUI previousUI){
        super(player, 27, LanguageController.getLocalized("member_menu.title").replace("{player}", target.getName()));

        this.clan = clan;
        this.clansController = clansController;
        this.playerController = playerController;
        this.currenciesController = currenciesController;
        this.requestsController = requestsController;
        this.invitesController = invitesController;
        this.logController = logController;
        this.plugin = plugin;
        this.cPlayer = playerController.getPlayer(player.getUniqueId());
        this.cTarget = target;
        this.previousUI = previousUI;
        Locale loc = new Locale(plugin.getConfig().getString("language.language"), plugin.getConfig().getString("language.country"));
        sdf = new SimpleDateFormat(LanguageController.getLocalized("time_format"), loc);

        init();
        fancyBackground();
        open();
    }


    private void init(){

        addIcon(13, createMemberIcon());
        addIcon(11, permissionSettingsIcon());
        addIcon(15, kickIcon());

        if(previousUI != null)
            addCloseAction((target) -> {
                previousUI.open();
            });
    }


    private ItemStack createMemberItem(){
        var item = new ItemStack(Material.PLAYER_HEAD);
        var meta = (SkullMeta) item.getItemMeta();

        var offPlayer = Bukkit.getOfflinePlayerIfCached(cTarget.getName());
        if(offPlayer != null){
            meta.setOwningPlayer(offPlayer);
        }else{
            GameProfile profile = new GameProfile(UUID.fromString("b475559e-5c77-4954-8fa4-7d50f54aaab3"),
                    "Unknown");
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZkNWJkZTk5NGUwYTY0N2FmMTgyMzY4MWE2MTNjMmJmYzNkOTczNmY4ODlkYmY4YzNiYmJhNWExM2Y4ZWQifX19"));
            Field profileField;
            try {
                profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        // name
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("member_menu.member_item.title")
                                .replace("{player}", cTarget.getName())
                ).decoration(TextDecoration.ITALIC, false)
        );

        if(cTarget.getUuid().equals(clan.getOwner()))
            meta.lore(format(LanguageController.getLocalizedList("member_menu.member_item.lore_owner"), cPlayer, clan.getAutoKickTime()));
        else{
            if(cTarget.isActive() || clan.getAutoKickTime() == -1){
                meta.lore(format(LanguageController.getLocalizedList("member_menu.member_item.lore_active"), cPlayer, clan.getAutoKickTime()));
            }else{
                meta.lore(format(LanguageController.getLocalizedList("member_menu.member_item.lore_inactive"), cPlayer, clan.getAutoKickTime()));
            }
        }

        item.setItemMeta(meta);
        return item;

    }
    private Icon createMemberIcon(){
        var icon = new Icon(createMemberItem(), (self, target) -> {
            self.itemStack = createMemberItem();
        });

        icon.addClickAction((target) -> {
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            refresh();
        });


        return icon;
    }



    // <editor-fold desc="Settings icon">
    private ItemStack permissionSettingsItem(){
        var item = new ItemStack(Material.ANVIL);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("member_menu.permissions_item.title")
                                .replace("{clan}", clan.getName())
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("member_menu.permissions_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{clan}", clan.getName())
        )).toList());
        item.setItemMeta(meta);


        return item;
    }
    private Icon permissionSettingsIcon(){
        var icon = new Icon(permissionSettingsItem(), (self, target) -> {
            self.itemStack = permissionSettingsItem();
        });

        icon.setVisibilityCondition((target, self) ->
                target.getUniqueId().equals(clan.getOwner())
                        || target.hasPermission("easyclans.admin.member_settings")
                        || (cPlayer.hasPermission(UserPermissions.EDIT_MEMBER_PERMISSIONS) && cPlayer.getClanID() == clan.getId())
        );

        icon.addClickAction((target) -> {
            // open permissions menu
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            new PermissionsMenu(target, cTarget, clan, clansController, playerController, currenciesController, requestsController, invitesController, logController, plugin, this);
        });

        return icon;
    }
    // </editor-fold>


    // <editor-fold desc="Settings icon">
    private ItemStack kickItem(){
        var item = new ItemStack(Material.DARK_OAK_DOOR);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("member_menu.kick_item.title")
                                .replace("{clan}", clan.getName())
                                .replace("{target}", cTarget.getName())
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("member_menu.kick_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{clan}", clan.getName())
                .replace("{target}", cTarget.getName())
        )).toList());
        item.setItemMeta(meta);


        return item;
    }
    private Icon kickIcon(){
        var icon = new Icon(kickItem(), (self, target) -> {
            self.itemStack = kickItem();
        });

        icon.setVisibilityCondition((target, self) ->
                !cTarget.getUuid().equals(clan.getOwner())
                        &&
                (
                    target.getUniqueId().equals(clan.getOwner())
                        || target.hasPermission("easyclans.admin.member_kick")
                        || (cPlayer.hasPermission(UserPermissions.KICK_MEMBER) && cPlayer.getClanID() == clan.getId())
                )
        );

        icon.addClickAction((target) -> {
            // open kick confirm menu
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));

            new ConfirmGUI(player, (ignored) -> {
                cTarget.removeFromClan();
                clan.getMembers().remove(cTarget.getUuid());
                playerController.updatePlayer(cTarget);
                player.sendMessage(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("members.kick.kick_success")
                                .replace("{player}", cTarget.getName())
                ));
                var bplayer = Bukkit.getPlayer(cTarget.getUuid());
                if(bplayer != null){
                    bplayer.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("members.kick.kicked")));
                }
                logController.addLog(new Log( cTarget.getUuid().toString(), player.getUniqueId(), clan.getId(), LogType.MEMBER_KICK));
                if(previousUI != null)
                    previousUI.open();
            }, (ignored) -> {
                if(previousUI != null)
                    previousUI.open();
            }, LanguageController.getLocalized("kick_confirm_menu.title").replace("{player}", cTarget.getName()));

        });

        return icon;
    }
    // </editor-fold>





    private List<Component> format(List<String> strings, CPlayer cPlayer, int kickTime) {
        List<Component> newlist = new ArrayList<>();
        var activeDate = new Date(cPlayer.getLastActive());
        var joinDate = new Date(cPlayer.getJoinClanDate());
        var timeDiff = Math.abs(kickTime - (System.currentTimeMillis() - activeDate.getTime()));
        double interest_player = plugin.getConfig().getDouble("rank_interest_value." + cPlayer.getRank());
        for (String string : strings) {
            newlist.add(
                    ClansPlugin.MM.deserialize(string
                            .replace("{active}", sdf.format(activeDate))
                            .replace("{joined}", sdf.format(joinDate))
                            .replace("{time_remaining}", DurationFormatUtils.formatDurationWords(timeDiff, true,false))
                            .replace("{status}", cPlayer.isActive() ?
                                    LanguageController.getLocalized("active") : LanguageController.getLocalized("inactive"))
                            .replace("{interest}", String.format("%.7f", interest_player))
                    ));
        }
        return newlist;
    }
}
