package eu.virtusdevelops.easyclans.providers;

import org.bukkit.OfflinePlayer;

public interface Provider<T> {

    ProviderType type();
    boolean setup();
    String getPluginName();
    String getVersion();
    T getValue(OfflinePlayer player);
    boolean setValue(OfflinePlayer player, T value);
    boolean removeValue(OfflinePlayer player, T value);
    boolean addValue(OfflinePlayer player, T value);

}
