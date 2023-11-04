package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.ClansController;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.controller.PlayerController;
import eu.virtusdevelops.easyclans.controller.TropyController;
import eu.virtusdevelops.easyclans.gui.AsyncPaginator;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.gui.actions.AsyncReturnTask;
import eu.virtusdevelops.easyclans.models.ClanTrophy;
import eu.virtusdevelops.easyclans.models.Trophy;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class TrophyDetailsMenu extends AsyncPaginator {

    private TropyController tropyController;
    private ClansController clansController;
    private PlayerController playerController;
    private Trophy trophy;
    private final SimpleDateFormat sdf;

    public TrophyDetailsMenu(Player player, Trophy trophy, ClansPlugin plugin, TropyController tropyController, ClansController clansController, PlayerController playerController) {
        super(player, plugin, 54,
                LanguageController.getLocalized("trophy_details_menu.title").replace("{trophy}",
                        trophy.getTitle()), List.of(
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        ));
        this.trophy = trophy;
        this.tropyController = tropyController;
        this.clansController = clansController;
        this.playerController = playerController;

        Locale loc = new Locale(plugin.getConfig().getString("language.language"), plugin.getConfig().getString("language.country"));
        sdf = new SimpleDateFormat(LanguageController.getLocalized("time_format"), loc);

        setup();
        init();
    }


    private void setup() {

        var organized = trophy.getOrganizedTrophies();
        setFetchPageTask(new AsyncReturnTask<>() {
            @Override
            public List<Icon> fetchPageData(int page, int perPage) {
                int size = organized.size();
                //if(size < page*perPage + perPage) return new ArrayList<>();
                List<Icon> icons = new ArrayList<>();
                for (int i = 0; i < perPage; i++) {
                    var index = i + (page * perPage);
                    if (index >= size) break;
                    icons.add(createClanIcon(organized.get(index)));
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
                return organized.size();
            }

            @Override
            public Integer fetchData() {
                return organized.size();
            }
        });


    }

    private ItemStack createClanItem(ClanTrophy clanTrophy) {
        var clan = clansController.getClan(clanTrophy.getClanID());
        var item = clan.getBanner().clone();
        var meta = item.getItemMeta();


        var date = sdf.format(new Date(clanTrophy.getAchievedDate()));

        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("trophy_details_menu.clan_item.title")
                                .replace("{clan}", clan.getName())
                                .replace("{position}", "" + clanTrophy.getRanking())
                                .replace("{date}", date)
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("trophy_details_menu.clan_item.lore").stream().map(it -> {
            return ClansPlugin.MM.deserialize(it
                    .replace("{clan}", clan.getName())
                    .replace("{position}", "" + clanTrophy.getRanking())
                    .replace("{date}", date)
            );
        }).toList());


        item.setItemMeta(meta);
        return item;
    }


    private Icon createClanIcon(ClanTrophy clanTrophy) {
        Icon icon = new Icon(createClanItem(clanTrophy));
        icon.addClickAction((target) -> {
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
        });

        return icon;
    }

}
