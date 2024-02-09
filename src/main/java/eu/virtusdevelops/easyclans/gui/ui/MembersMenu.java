package eu.virtusdevelops.easyclans.gui.ui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.AsyncPaginator;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.gui.actions.AsyncReturnTask;
import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.UserPermissions;
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

public class MembersMenu extends AsyncPaginator {
    private final CPlayer cPlayer;
    private final Clan clan;
    private final ClansController clansController;
    private final PlayerController playerController;
    private final CurrenciesController currenciesController;
    private final RequestsController requestsController;
    private final InvitesController invitesController;
    private final LogController logController;
    private final RanksController ranksController;
    private final ClansPlugin plugin;
    private final GUI previousUI;
    private final SimpleDateFormat sdf;

    public MembersMenu(Player player, Clan clan, ClansController clansController, PlayerController playerController, CurrenciesController currenciesController,
                       RequestsController requestsController, InvitesController invitesController, LogController logController, RanksController ranksController,
                       ClansPlugin plugin, GUI previousUI){
        super(player, plugin,54, LanguageController.getLocalized("members_menu.title"),List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        ));

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
        this.previousUI = previousUI;
        Locale loc = new Locale(plugin.getConfig().getString("language.language"), plugin.getConfig().getString("language.country"));
        sdf = new SimpleDateFormat(LanguageController.getLocalized("time_format"), loc);

        setup();
        init();
    }

    private void setup(){


        setFetchPageTask(new AsyncReturnTask<>() {
            @Override
            public List<Icon> fetchPageData(int page, int perPage) {
                int size = clan.getMembers().size();
                //if(size < page*perPage + perPage) return new ArrayList<>();
                List<Icon> icons = new ArrayList<>();
                for(int i = 0; i < perPage; i++){
                    var index = i + (page * perPage);
                    if(index >= size) break;
                    var cMember = playerController.getPlayer(clan.getMembers().get(index));
                    icons.add(createMemberIcon(cMember));
                }

                return icons;
            }

            @Override
            public List<Icon> fetchData() {
                return null;
            }
        });

        setGetItemsCountTask(new AsyncReturnTask<>() {
            @Override
            public Integer fetchPageData(int page, int perPage) {
                return clan.getMembers().size();
            }

            @Override
            public Integer fetchData() {
                return clan.getMembers().size();
            }
        });


        if(previousUI != null)
            addCloseAction((target) -> {
                previousUI.open();
            });
    }

    private ItemStack createMemberItem(CPlayer cTarget){
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
                        LanguageController.getLocalized("members_menu.member_item.title")
                                .replace("{player}", cTarget.getName())
                ).decoration(TextDecoration.ITALIC, false)
        );

        if(cTarget.getUuid().equals(clan.getOwner()))
            meta.lore(format(LanguageController.getLocalizedList("members_menu.member_item.lore_owner"), cTarget, clan.getAutoKickTime()));
        else{
            if(cTarget.isActive() || clan.getAutoKickTime() == -1){
                meta.lore(format(LanguageController.getLocalizedList("members_menu.member_item.lore_active"), cTarget, clan.getAutoKickTime()));
            }else{
                meta.lore(format(LanguageController.getLocalizedList("members_menu.member_item.lore_inactive"), cTarget, clan.getAutoKickTime()));
            }
        }

        item.setItemMeta(meta);
        return item;

    }

    private Icon createMemberIcon(CPlayer cTarget){
        var icon = new Icon(createMemberItem(cTarget));

        icon.addClickAction((target) -> {
            if(!target.getUniqueId().equals(clan.getOwner())
                    && !target.hasPermission("easyclans.admin.members")
                    && !(cPlayer.hasPermission(UserPermissions.EDIT_MEMBER) && cPlayer.getClanID() == clan.getId())){

                target.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            // open member settings menu
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            new MemberSettingsMenu(player, clan, cTarget, clansController, playerController, currenciesController, requestsController, invitesController, logController, plugin, this);
        });


        return icon;
    }





    private List<Component> format(List<String> strings, CPlayer cTarget, int kickTime) {
        List<Component> newlist = new ArrayList<>();
        var activeDate = new Date(cTarget.getLastActive());
        var joinDate = new Date(cTarget.getJoinClanDate());
        var timeDiff = Math.abs(kickTime - (System.currentTimeMillis() - activeDate.getTime()));
        double interest_player = ranksController.getRank(cTarget).getMultiplier();
        for (String string : strings) {
            newlist.add(
                    ClansPlugin.MM.deserialize(string
                            .replace("{active}", sdf.format(activeDate))
                            .replace("{joined}", sdf.format(joinDate))
                            .replace("{rank}", cTarget.getRank())
                            .replace("{time_remaining}", DurationFormatUtils.formatDurationWords(timeDiff, true,false))
                            .replace("{status}", cTarget.isActive() ?
                                    LanguageController.getLocalized("active") : LanguageController.getLocalized("inactive"))
                            .replace("{interest}", String.format("%.7f", interest_player))
                    ));
        }
        return newlist;
    }
}
