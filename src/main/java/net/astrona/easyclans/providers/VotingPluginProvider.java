package net.astrona.easyclans.providers;

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
        return "VotingPlugin -> <gray>" + VotingPluginMain.plugin.getVersion();
    }

    @Override
    public double getValue(OfflinePlayer player) {
        return plugin.getUser(player.getUniqueId()).getPoints();
    }

    @Override
    public void setValue(OfflinePlayer player, double value) {
        plugin.getUser(player.getUniqueId()).setPoints((int) value);
    }

    @Override
    public void removeValue(OfflinePlayer player, double value) {
        plugin.getUser(player.getUniqueId()).removePoints((int) value);
    }

    @Override
    public void addValue(OfflinePlayer player, double value) {
        plugin.getUser(player.getUniqueId()).addPoints((int) value);
    }

    @Override
    public int getIntValue(OfflinePlayer player) {
        return plugin.getUser(player.getUniqueId()).getPoints();
    }

    @Override
    public void setIntValue(OfflinePlayer player, int value) {
        plugin.getUser(player.getUniqueId()).setPoints(value);
    }

    @Override
    public void removeIntValue(OfflinePlayer player, int value) {
        plugin.getUser(player.getUniqueId()).removePoints(value);
    }

    @Override
    public void addIntValue(OfflinePlayer player, int value) {
        plugin.getUser(player.getUniqueId()).addPoints(value);
    }
}
