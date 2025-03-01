package eu.virtusdevelops.easyclans.providers;

import eu.virtusdevelops.easyclans.utils.Experience;
import org.bukkit.OfflinePlayer;

public class ExperienceProvider implements Provider<Integer>{
    @Override
    public ProviderType type() {
        return ProviderType.INT;
    }

    @Override
    public boolean setup() {
        return true;
    }

    @Override
    public String getPluginName() {
        return "Player Experience";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public Integer getValue(OfflinePlayer player) {
        if(!player.isOnline())
            return 0;
        var oplayer = player.getPlayer();
        if(oplayer == null)
            return 0;
        return Experience.getExp(oplayer);
    }

    @Override
    public boolean setValue(OfflinePlayer player, Integer value) {
        if(!player.isOnline())
            return false;
        var oplayer = player.getPlayer();
        if(oplayer == null)
            return false;

        Experience.setExp(oplayer, value);
        return true;
    }

    @Override
    public boolean removeValue(OfflinePlayer player, Integer value) {
        if(!player.isOnline())
            return false;
        var oplayer = player.getPlayer();
        if(oplayer == null)
            return false;


        Experience.addExp(oplayer, -value);
        return true;
    }

    @Override
    public boolean addValue(OfflinePlayer player, Integer value) {
        if(!player.isOnline())
            return false;
        var oplayer = player.getPlayer();
        if(oplayer == null)
            return false;
        Experience.addExp(oplayer, value);
        return true;
    }
}
