package eu.virtusdevelops.easyclans.gui.ui;

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
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class PermissionsMenu extends AsyncPaginator {
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

    public PermissionsMenu(Player player, CPlayer cTarget, Clan clan, ClansController clansController, PlayerController playerController, CurrenciesController currenciesController,
                           RequestsController requestsController, InvitesController invitesController, LogController logController, ClansPlugin plugin, GUI previousUI){
        super(player, plugin,54, LanguageController.getLocalized("permissions_menu.title").replace("{player}", cTarget.getName()), List.of(
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
        this.plugin = plugin;
        this.cPlayer = playerController.getPlayer(player.getUniqueId());
        this.previousUI = previousUI;
        this.cTarget = cTarget;
        Locale loc = new Locale(plugin.getConfig().getString("language.language"), plugin.getConfig().getString("language.country"));
        sdf = new SimpleDateFormat(LanguageController.getLocalized("time_format"), loc);

        setup();
        init();



    }


    private void setup(){
        setFetchPageTask(new AsyncReturnTask<>() {
            @Override
            public List<Icon> fetchPageData(int page, int perPage) {
                int size = UserPermissions.values().length;
                List<Icon> icons = new ArrayList<>();
                for(int i = 0; i < perPage; i++){
                    var index = i + (page * perPage);
                    if(index >= size) break;
                    icons.add(createPermissionIcon(UserPermissions.values()[index]));
                }

                return icons;
            }

            @Override
            public List<Icon> fetchData() {
                return null;
            }
        });

        setGetItemsCountTask(new AsyncReturnTask<Integer>() {
            @Override
            public Integer fetchPageData(int page, int perPage) {
                return UserPermissions.values().length;
            }

            @Override
            public Integer fetchData() {
                return UserPermissions.values().length;
            }
        });

        if(previousUI != null)
            addCloseAction((target) -> {
                previousUI.open();
            });
    }


    private ItemStack createPermissionItem(UserPermissions permission){
        Material mat;
        if(cTarget.hasPermission(permission)){
            mat = Material.BOOK;
        }else{
            mat = Material.PAPER;
        }
        var item = new ItemStack(mat);

        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("permissions_menu.permission_item.title")
                                .replace("{permission}", permission.name())
                                .replace("{status}", cTarget.hasPermission(permission) ? LanguageController.getLocalized("permissions_menu.has") : LanguageController.getLocalized("permissions_menu.doesnt_have"))
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("permissions_menu.permission_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{permission}", permission.name())
                .replace("{description}", permission.getDescription())
                .replace("{status}", cTarget.hasPermission(permission) ? LanguageController.getLocalized("permissions_menu.has") : LanguageController.getLocalized("permissions_menu.doesnt_have"))
        ).decoration(TextDecoration.ITALIC, false)).toList());
        item.setItemMeta(meta);



        return item;
    }


    private Icon createPermissionIcon(UserPermissions permission){
        var icon = new Icon(createPermissionItem(permission), (self, target)-> {
            self.itemStack = createPermissionItem(permission);
        });



        icon.addClickAction((target) -> {
            if(!target.getUniqueId().equals(clan.getOwner())
                    && !target.hasPermission("easyclans.admin.edit_member_permissions")
                    && !(cPlayer.hasPermission(UserPermissions.EDIT_MEMBER_PERMISSIONS) && cPlayer.getClanID() == clan.getId())){

                target.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }

            if(cTarget.hasPermission(permission)){
                cTarget.removePermission(permission);
            }else{
                cTarget.addPermission(permission);
            }
            playerController.updatePlayer(cTarget);
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));

            refresh();
        });

        return icon;
    }
}
