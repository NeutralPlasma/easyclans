package eu.virtusdevelops.easyclans.providers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
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
    public String getVersion(){
        return Bukkit.getPluginManager().getPlugin("Vault").getPluginMeta().getVersion();
    }

    @Override
    public double getValue(OfflinePlayer player) {
        return Economy.getBalance(player);
    }

    @Override
    public boolean setValue(OfflinePlayer player, double value) {
        return Economy.withdrawPlayer(player, getValue(player)).transactionSuccess();
    }

    @Override
    public boolean removeValue(OfflinePlayer player, double value) {
        return Economy.withdrawPlayer(player, value).transactionSuccess();
    }

    @Override
    public boolean addValue(OfflinePlayer player, double value) {
        return Economy.depositPlayer(player, value).transactionSuccess();
    }

    @Override
    public int getIntValue(OfflinePlayer player) {
        return (int) Economy.getBalance(player);
    }

    @Override
    public boolean setIntValue(OfflinePlayer player, int value) {
        return Economy.withdrawPlayer(player, getValue(player)).transactionSuccess();
    }

    @Override
    public boolean removeIntValue(OfflinePlayer player, int value) {
        return Economy.withdrawPlayer(player, value).transactionSuccess();
    }

    @Override
    public boolean addIntValue(OfflinePlayer player, int value) {
        return Economy.depositPlayer(player, value).transactionSuccess();
    }
}
