package eu.virtusdevelops.easyclans.controller;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.ClanTrophy;
import eu.virtusdevelops.easyclans.models.Trophy;
import eu.virtusdevelops.easyclans.storage.SQLStorage;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TropyController {
    private ClansPlugin plugin;
    private List<Trophy> trophyList;

    public TropyController(ClansPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    private void init(){
        plugin.getLogger().info("Loading all trophies...");
        //trophyList = sqlStorage.loadAllTrophies();
    }

    public Trophy createTrophy(String name, String title, String description, long startDate, long endDate) {
        Trophy trophy = new Trophy(name, title, description, startDate, endDate);
        // TODO: insert into DB
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            /*if(!sqlStorage.saveTrophy(trophy)){
                sqlStorage.saveTrophy(trophy);
            }*/
        });
        trophyList.add(trophy);
        return trophy;
    }

    public void deleteTrophy(Trophy trophy){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            /*for(ClanTrophy clanTrophy : trophy.getOrganizedTrophies().values()){
                sqlStorage.removeClanFromTrophy(clanTrophy);
            }
            sqlStorage.deleteTropyh(trophy);*/
        });
        trophyList.remove(trophy);
    }

    public void updateTrophy(Trophy trophy){
        /*Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sqlStorage.updateTrophy(trophy);
        });*/
    }




    public boolean removeClanFromTrophy(Trophy trophy, Clan clan){
        /*var cTrophy = trophy.getTrophy(clan);
        if(cTrophy != null){
            trophy.getOrganizedTrophies().remove(cTrophy.getRanking());
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                sqlStorage.removeClanFromTrophy(cTrophy);
            });
            return true;
        }*/
        return false;
    }

    public boolean addClanToTrophy(Trophy trophy, Clan clan, int ranking, long achieveDate){
        /*var cTrophy = trophy.getTrophy(clan);
        if (cTrophy == null)
            cTrophy = trophy.getTrophy(ranking);

        if(cTrophy == null){
            var newTrophy = new ClanTrophy(clan.getId(), trophy.getId(), ranking, achieveDate);
            trophy.addTrophy(newTrophy);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                sqlStorage.addClanToTrophy(newTrophy);
            });
            return true;
        }*/
        return false;
    }

    public Trophy getTrophy(String name){
        for(Trophy trophy: trophyList){
            if(trophy.getName().equals(name)){
                return trophy;
            }
        }
        return null;
    }

    public Trophy getTrophy(UUID id){
        for(Trophy trophy: trophyList){
            if(trophy.getId().equals(id)){
                return trophy;
            }
        }
        return null;
    }

    public List<Trophy> getTrophyList() {
        return trophyList;
    }
}
