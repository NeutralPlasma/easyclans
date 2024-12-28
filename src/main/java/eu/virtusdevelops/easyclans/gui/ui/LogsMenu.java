package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.controller.LogController;
import eu.virtusdevelops.easyclans.controller.PlayerController;
import eu.virtusdevelops.easyclans.dao.LogDao;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.gui.actions.AsyncReturnTask;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.ClansController;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.gui.AsyncPaginator;
import eu.virtusdevelops.easyclans.storage.SQLStorage;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class LogsMenu extends AsyncPaginator {
    private final LogDao logDao;
    private final ClansController clansController;
    private final PlayerController playerController;

    private SimpleDateFormat sdf;
    public LogsMenu(Player player,
                    ClansPlugin plugin) {

        super(player, plugin, 54, "<Gray>Logs <dark_gray>[<gold>{page}<gray>/<gold>{pages}<dark_gray>] (<gold>{total}<dark_gray>)", List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        ));
        this.logDao = plugin.getSqlStorage().getLogDao();
        this.clansController = plugin.getClansController();
        this.playerController = plugin.getPlayerController();
        Locale loc = new Locale(plugin.getConfig().getString("language.language"), plugin.getConfig().getString("language.country"));
        sdf = new SimpleDateFormat(LanguageController.getLocalized("time_format"), loc);
        setupActions();
        init();
    }


    private void setupActions(){
        setFetchPageTask(new AsyncReturnTask<>() {
            @Override
            public List<Icon> fetchPageData(int page, int perPage) {
                // TODO
                var logs = logDao.getPlayerClanLogs(UUID.randomUUID(), player.getUniqueId(), page, perPage);
                List<Icon> logIcons = new ArrayList<>();
                for(Log log : logs){
                    var material = Material.PAPER;
                    switch (log.type()){
                        case DEPOSIT, MONEY_ADD, WITHDRAW, MONEY_REMOVE: {
                            material = Material.SUNFLOWER;
                            break;
                        }
                        case INTEREST_ADD, INTEREST_RESET:{
                            material = Material.EMERALD;
                            break;
                        }
                    }



                    var item = new ItemStack(material);
                    var meta = item.getItemMeta();
                    meta.displayName(ClansPlugin.MM.deserialize("<gold>" + log.type()));
                    var lore = new ArrayList<Component>();
                    var clan = log.clan() != null ? clansController.getClan(log.clan()) != null ? clansController.getClan(log.clan()).getName() : "DELETED" : "UNKNOWN";

                    lore.add(ClansPlugin.MM.deserialize("<gray>-------------------"));
                    lore.add(ClansPlugin.MM.deserialize("<gray>Clan: <yellow>" + clan));
                    lore.add(ClansPlugin.MM.deserialize("<gray>Player: <yellow>" + (log.player() != null ? playerController.getPlayer(log.player()).getName() : "<dark_gray>---")));
                    lore.add(ClansPlugin.MM.deserialize("<gray>-------------------"));
                    switch(log.type()){
                        case DEPOSIT, WITHDRAW, MONEY_ADD, MONEY_REMOVE:{
                            var splited = log.log().split(":");
                            lore.add(ClansPlugin.MM.deserialize("<gray>Action: <yellow>" + log.type()));
                            lore.add(ClansPlugin.MM.deserialize("<gray>Currency: <yellow>" + splited[1]));
                            lore.add(ClansPlugin.MM.deserialize("<gray>Amount: <yellow>" + splited[2]));
                            break;
                        }
                        case INTEREST_ADD:{
                            var splited = log.log().split(":");
                            lore.add(ClansPlugin.MM.deserialize("<gray>Action: <yellow>" + splited[1]));
                            lore.add(ClansPlugin.MM.deserialize("<gray>Amount: <yellow>" + splited[2]));
                            break;
                        }
                        case INTEREST_RESET:{
                            lore.add(ClansPlugin.MM.deserialize("<gray>Action: <yellow>Interest reset!" ));
                            break;
                        }
                        default:{
                            lore.add(ClansPlugin.MM.deserialize("<gray>Action:  <yellow>" + log.log()));
                        }
                    }
                    lore.add(ClansPlugin.MM.deserialize("<gray>-------------------"));
                    lore.add(ClansPlugin.MM.deserialize("<gray>Date: <yellow>" + sdf.format(log.timeStamp())));
                    meta.lore(lore);

                    item.setItemMeta(meta);
                    var icon = new Icon(item);
                    icon.addClickAction((d) -> {});
                    logIcons.add(icon);
                }
                return logIcons;
            }

            @Override
            public List<Icon> fetchData() {
                return null;
            }
        });

        setGetItemsCountTask(new AsyncReturnTask<Integer>() {
            @Override
            public Integer fetchPageData(int page, int perPage) {
                return 0; //sqlStorage.getLogsCount(null, player.getUniqueId());
            }

            @Override
            public Integer fetchData() {
                return 0;//sqlStorage.getLogsCount(null, player.getUniqueId());
            }
        });
    }
}
