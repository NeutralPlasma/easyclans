package eu.virtusdevelops.easyclans.providers;

import com.bencodez.votingplugin.VotingPluginMain;
import org.bukkit.OfflinePlayer;

public class VotingPluginProvider implements Provider{

    private VotingPluginMain plugin;

    @Override
    public boolean setup() {
        plugin = VotingPluginMain.getPlugin();
        return plugin != null;
    }
    @Override
    public ProviderType type() {
        return ProviderType.INT;
    }

    @Override
    public String getPluginName() {
        return "VotingPlugin";
    }

    @Override
    public String getVersion(){
        return VotingPluginMain.plugin.getVersion();
    }

    @Override
    public double getValue(OfflinePlayer player) {
        return plugin.getUser(player.getUniqueId()).getPoints();
    }

    @Override
    public boolean setValue(OfflinePlayer player, double value) {
        plugin.getUser(player.getUniqueId()).setPoints((int) value);
        return true;
    }

    @Override
    public boolean removeValue(OfflinePlayer player, double value) {
        plugin.getUser(player.getUniqueId()).removePoints((int) value);
        return true;
    }

    @Override
    public boolean addValue(OfflinePlayer player, double value) {
        plugin.getUser(player.getUniqueId()).addPoints((int) value);
        return true;
    }

    @Override
    public int getIntValue(OfflinePlayer player) {
        return plugin.getUser(player.getUniqueId()).getPoints();
    }

    @Override
    public boolean setIntValue(OfflinePlayer player, int value) {
        plugin.getUser(player.getUniqueId()).setPoints(value);
        return true;
    }

    @Override
    public boolean removeIntValue(OfflinePlayer player, int value) {
        plugin.getUser(player.getUniqueId()).removePoints(value);
        return true;
    }

    @Override
    public boolean addIntValue(OfflinePlayer player, int value) {
        plugin.getUser(player.getUniqueId()).addPoints(value);
        return true;
    }
}
