package eu.virtusdevelops.easyclans.providers;

import org.bukkit.OfflinePlayer;

public interface Provider {

    ProviderType type();
    boolean setup();
    String getPluginName();
    String getVersion();
    double getValue(OfflinePlayer player);
    boolean setValue(OfflinePlayer player, double value);
    boolean removeValue(OfflinePlayer player, double value);
    boolean addValue(OfflinePlayer player, double value);

    int getIntValue(OfflinePlayer player);
    boolean setIntValue(OfflinePlayer player, int value);
    boolean removeIntValue(OfflinePlayer player, int value);
    boolean addIntValue(OfflinePlayer player, int value);
}
