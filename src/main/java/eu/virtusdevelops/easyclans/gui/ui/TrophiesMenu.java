package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.ClansController;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.controller.PlayerController;
import eu.virtusdevelops.easyclans.controller.TropyController;
import eu.virtusdevelops.easyclans.gui.AsyncPaginator;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.gui.actions.AsyncReturnTask;
import eu.virtusdevelops.easyclans.models.Trophy;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;


public class TrophiesMenu extends AsyncPaginator {
    private ClansPlugin plugin;
    private TropyController tropyController;
    private ClansController clansController;
    private PlayerController playerController;

    public TrophiesMenu(Player player, ClansPlugin plugin, TropyController tropyController, ClansController clansController, PlayerController playerController) {
        super(player, plugin, 54, LanguageController.getLocalized("trophies_menu.title"), List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        ));
        this.plugin = plugin;
        this.tropyController = tropyController;
        this.clansController = clansController;
        this.playerController = playerController;
        setup();
        init();
    }


    private void setup() {

        setFetchPageTask(new AsyncReturnTask<>() {
            @Override
            public List<Icon> fetchPageData(int page, int perPage) {
                int size = tropyController.getTrophyList().size();
                //if(size < page*perPage + perPage) return new ArrayList<>();
                List<Icon> icons = new ArrayList<>();
                for (int i = 0; i < perPage; i++) {
                    var index = i + (page * perPage);
                    if (index >= size) break;
                    icons.add(createTropyIcon(tropyController.getTrophyList().get(index)));
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
                return tropyController.getTrophyList().size();
            }

            @Override
            public Integer fetchData() {
                return tropyController.getTrophyList().size();
            }
        });

        addIcon(44, trophyCreateIcon());

    }

    private ItemStack createTropyItem(Trophy trophy) {
        ItemStack itemStack = new ItemStack(Material.HONEYCOMB);
        var organized = trophy.getOrganizedTrophies();
        var meta = itemStack.getItemMeta();


// name
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("trophies_menu.trophy_item.title")
                                .replace("{title}", trophy.getTitle())
                ).decoration(TextDecoration.ITALIC, false)
        );


        var clan1 = !organized.isEmpty() ? clansController.getClan(organized.get(0).getClanID()) : null;
        var clan2 = organized.size() > 1 ? clansController.getClan(organized.get(1).getClanID()) : null;
        var clan3 = organized.size() > 2 ? clansController.getClan(organized.get(2).getClanID()) : null;


        meta.lore(LanguageController.getLocalizedList("trophies_menu.trophy_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{1}", clan1 != null ? clan1.getName() : LanguageController.getLocalized("trophies_menu.trophy_item.no_clan"))
                .replace("{2}", clan2 != null ? clan2.getName() : LanguageController.getLocalized("trophies_menu.trophy_item.no_clan"))
                .replace("{3}", clan3 != null ? clan3.getName() : LanguageController.getLocalized("trophies_menu.trophy_item.no_clan"))
                .replace("{description}", trophy.getDescription())
                .replace("{title}", trophy.getTitle())
        )).toList());



        itemStack.setItemMeta(meta);


        return itemStack;
    }

    private Icon createTropyIcon(Trophy trophy) {
        var icon = new Icon(createTropyItem(trophy));

        icon.addClickAction((target) -> {
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            // trophy overview menu
            new TrophyDetailsMenu(target, trophy, plugin, tropyController, clansController, playerController);
        });


        return icon;
    }


    private ItemStack trophyCreateItem(){
        var item = new ItemStack(Material.BOOK);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("trophies_menu.create_trophy_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("trophies_menu.create_trophy_item.lore").stream().map(ClansPlugin.MM::deserialize).toList());
        item.setItemMeta(meta);


        return item;
    }

    private Icon trophyCreateIcon(){
        var icon = new Icon(trophyCreateItem());

        icon.setVisibilityCondition((target, self) -> {
            return target.hasPermission("easyclans.trophies.create");
        });

        icon.addClickAction((target) -> {
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            // open trophy creation menu
            new TrophyCreateMenu(target, plugin, tropyController, playerController, clansController);
        });


        return icon;
    }

}
