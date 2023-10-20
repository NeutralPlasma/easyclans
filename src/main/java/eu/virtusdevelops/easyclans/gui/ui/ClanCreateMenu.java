package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.UserPermissions;
import eu.virtusdevelops.easyclans.utils.BannerUtils;
import eu.virtusdevelops.easyclans.utils.ItemUtils;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanCreateMenu extends GUI {
    private final CPlayer cPlayer;
    private final ClansController clansController;
    private final PlayerController playerController;
    private final CurrenciesController currenciesController;
    private final RequestsController requestsController;
    private final InvitesController invitesController;
    private final LogController logController;
    private final ClansPlugin plugin;

    // clan specific
    private ItemStack clanBanner = new ItemStack(Material.BLACK_BANNER);
    private String clanName;
    private String clanDisplayName;
    private String clanTag;

    public ClanCreateMenu(Player player, ClansController clansController, PlayerController playerController, CurrenciesController currenciesController,
                    RequestsController requestsController, InvitesController invitesController, LogController logController, ClansPlugin plugin){
        super(player, 54, LanguageController.getLocalized("clan_menu.title"));
        this.clansController = clansController;
        this.playerController = playerController;
        this.currenciesController = currenciesController;
        this.requestsController = requestsController;
        this.invitesController = invitesController;
        this.logController = logController;
        this.plugin = plugin;
        this.cPlayer = playerController.getPlayer(player.getUniqueId());

        this.clanName = player.getName();
        this.clanDisplayName = "DISPLAY_" + player.getName();
        clanTag = "" + clanName.charAt(0) + clanName.charAt(1);

        init();
        fancyBackground();
        open();
    }

    private void init(){

        addIcon(16, bannerIcon());
    }

    // <editor-fold desc="Banner icon">
    private void randomizeBanner(){
        clanBanner = BannerUtils.generateRandomBanner();
    }
    private ItemStack bannerItem(){
        var item = clanBanner.clone();
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_create_menu.banner_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("clan_create_menu.banner_item.lore").stream()
                .map(it -> ClansPlugin.MM.deserialize(it).decoration(TextDecoration.ITALIC, false)).toList());
        item.setItemMeta(meta);


        return item;
    }
    private Icon bannerIcon(){
        var icon = new Icon(bannerItem(), (self, target) -> {
            self.itemStack = bannerItem();
        });

        /*icon.setVisibilityCondition((target, self) ->
                target.getUniqueId().equals(clan.getOwner())
                        || target.hasPermission("easyclans.admin.settings")
                        || (cPlayer.hasPermission(UserPermissions.CLAN_SETTINGS) && cPlayer.getClanID() == clan.getId())
        );*/
        icon.addShiftLeftClickAction((target) -> {
            // open settings menu
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            randomizeBanner();
            refresh();
        });
        icon.addShiftRightClickAction((target) -> {
            // open settings menu
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            randomizeBanner();
            refresh();
        });
        icon.addDragItemAction((target, item) -> {
            if (!item.getType().toString().endsWith("BANNER")) {
                target.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }
            var newBanner = item.clone();
            newBanner = ItemUtils.removeEnchants(newBanner);
            newBanner = ItemUtils.strip(newBanner);
            clanBanner = newBanner;
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            refresh();
        });

        return icon;
    }
    // </editor-fold>


}
