package eu.virtusdevelops.easyclans.controller;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.ClanTrophy;
import eu.virtusdevelops.easyclans.models.Trophy;
import eu.virtusdevelops.easyclans.storage.SQLStorage;
import org.bukkit.Bukkit;

import java.util.List;

public class TropyController {
    private ClansPlugin plugin;
    private SQLStorage sqlStorage;
    private List<Trophy> trophyList;

    public TropyController(ClansPlugin plugin, SQLStorage sqlStorage) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        init();
    }

    private void init(){
        // sql load all trophies...
    }

    public Trophy createTrophy(String title, String description, long startDate, long endDate) {
        Trophy trophy = new Trophy(title, description, startDate, endDate);
        // TODO: insert into DB
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if(!sqlStorage.saveTrophy(trophy)){
                sqlStorage.saveTrophy(trophy);
            }
        });
        return trophy;
    }

    public void updateTrophy(Trophy trophy){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if(!sqlStorage.updateTrophy(trophy)){
                sqlStorage.updateTrophy(trophy);
            }
        });
    }

    public void deleteTrophy(Trophy trophy){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

        });
    }


    public boolean removeClanFromTrophy(Trophy trophy, Clan clan){
        var cTrophy = trophy.getTrophy(clan);
        if(cTrophy != null){
            trophy.getClansData().remove(cTrophy);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                sqlStorage.removeClanFromTrophy(cTrophy);
            });
            return true;
        }
        return false;
    }

    public boolean addClanToTrophy(Trophy trophy, Clan clan, int ranking, long achieveDate){
        var cTrophy = trophy.getTrophy(clan);
        if(cTrophy == null){
            var newTrophy = new ClanTrophy(clan.getId(), ranking, achieveDate);
            trophy.addTrophy(newTrophy);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                sqlStorage.addClanToTrophy(newTrophy);
            });
            return true;
        }
        return false;
    }
}
