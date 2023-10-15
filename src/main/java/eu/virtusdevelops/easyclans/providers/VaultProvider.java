package eu.virtusdevelops.easyclans.providers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

public class VaultProvider implements Provider{

    private net.milkbowl.vault.economy.Economy Economy = null;

    @Override
    public ProviderType type() {
        return ProviderType.DOUBLE;
    }

    @Override
    public boolean setup() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        Economy = rsp.getProvider();
        return Economy != null;
    }

    @Override
    public String getPluginName() {
        return "Vault";
    }

    @Override
    public double getValue(OfflinePlayer player) {
        return Economy.getBalance(player);
    }

    @Override
    public void setValue(OfflinePlayer player, double value) {
        Economy.withdrawPlayer(player, getValue(player));
    }

    @Override
    public void removeValue(OfflinePlayer player, double value) {
        Economy.withdrawPlayer(player, value);
    }

    @Override
    public void addValue(OfflinePlayer player, double value) {
        Economy.depositPlayer(player, value);
    }

    @Override
    public int getIntValue(OfflinePlayer player) {
        return (int) Economy.getBalance(player);
    }

    @Override
    public void setIntValue(OfflinePlayer player, int value) {
        Economy.withdrawPlayer(player, getValue(player));
    }

    @Override
    public void removeIntValue(OfflinePlayer player, int value) {
        Economy.withdrawPlayer(player, value);
    }

    @Override
    public void addIntValue(OfflinePlayer player, int value) {
        Economy.depositPlayer(player, value);
    }
}
