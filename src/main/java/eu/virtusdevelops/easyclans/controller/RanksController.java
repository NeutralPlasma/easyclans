package eu.virtusdevelops.easyclans.controller;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.RankMultiplyer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RanksController {

    private final ClansPlugin plugin;
    private final List<RankMultiplyer> rankMultiplyerList = new ArrayList<>();
    private final RankMultiplyer nullMultiplier = new RankMultiplyer("null", 0.0, -1);

    public RanksController(ClansPlugin plugin) {
        this.plugin = plugin;
        loadRankMultipliers();
    }



    public void loadRankMultipliers(){
        rankMultiplyerList.clear();

        var section = plugin.getConfig().getConfigurationSection("rank_interest_value");

        for(String key : section.getKeys(false)){
            rankMultiplyerList.add(new RankMultiplyer(
                    key,
                    section.getDouble(key + ".value"),
                    section.getInt(key + ".priority")
            ));
        }
        rankMultiplyerList.sort((o1, o2) -> o2.getPriority()-o1.getPriority());
        plugin.getLogger().info("Loaded " + rankMultiplyerList.size() + " rank multipliers");
    }
    public RankMultiplyer parsePlayerRank(Player player){
        for(RankMultiplyer rank : rankMultiplyerList){
            if(player.hasPermission("group." + rank.getName())){
                return rank;
            }
        }
        return null;
    }

    public RankMultiplyer getRank(CPlayer cPlayer){
        for(RankMultiplyer rank : rankMultiplyerList){
            if(cPlayer.getRank().equals(rank.getName())){
                return rank;
            }
        }
        return nullMultiplier;
    }


}
