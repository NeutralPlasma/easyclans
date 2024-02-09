package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.ClansController;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.controller.PlayerController;
import eu.virtusdevelops.easyclans.controller.TropyController;
import eu.virtusdevelops.easyclans.gui.AsyncPaginator;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.Trophy;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AddClanToTrophyMenu extends AsyncPaginator {

    private TropyController tropyController;
    private ClansController clansController;
    private PlayerController playerController;
    private Trophy trophy;
    private final SimpleDateFormat sdf;

    private Clan currentSelectedClan;
    private int selectedPosition = 1;


    public AddClanToTrophyMenu(Player player, Trophy trophy, ClansPlugin plugin, TropyController tropyController, ClansController clansController, PlayerController playerController) {
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

    private void setup(){
        currentSelectedClan = !clansController.getClans().isEmpty() ? clansController.getClans().get(0) : null;
    }

}
