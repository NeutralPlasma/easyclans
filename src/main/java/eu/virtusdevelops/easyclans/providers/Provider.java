package eu.virtusdevelops.easyclans.providers;

import org.bukkit.OfflinePlayer;

public interface Provider {

    ProviderType type();
    boolean setup();
    String getPluginName();
    double getValue(OfflinePlayer player);
    void setValue(OfflinePlayer player, double value);
    void removeValue(OfflinePlayer player, double value);
    void addValue(OfflinePlayer player, double value);

    int getIntValue(OfflinePlayer player);
    void setIntValue(OfflinePlayer player, int value);
    void removeIntValue(OfflinePlayer player, int value);
    void addIntValue(OfflinePlayer player, int value);
}
