package eu.virtusdevelops.easyclans.controller;

import eu.virtusdevelops.easyclans.providers.ExperienceProvider;
import eu.virtusdevelops.easyclans.providers.VaultProvider;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.providers.Provider;
import eu.virtusdevelops.easyclans.providers.VotingPluginProvider;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class CurrenciesController {
    private final ClansPlugin plugin;
    private final Map<String, Provider> currencyProviders = new HashMap<>();

    public CurrenciesController(ClansPlugin plugin){
        this.plugin = plugin;
        load();
    }

    private void load(){
        var pm = Bukkit.getPluginManager();
        if(pm.isPluginEnabled("VotingPlugin") && plugin.getConfig().getBoolean("currency.VotingPlugin.enabled")){
            var provider = new VotingPluginProvider();
            if(provider.setup())
                addProvider(provider, "VotingPlugin");
        }
        if(pm.isPluginEnabled("Vault") && plugin.getConfig().getBoolean("currency.Vault.enabled")){
            var provider = new VaultProvider();
            if(provider.setup())
                addProvider(provider, "Vault");
        }
        if(plugin.getConfig().getBoolean("currency.Experience.enabled")){
            var provider = new ExperienceProvider();
            if(provider.setup())
                addProvider(provider, "Experience");
        }

    }

    public void addProvider(Provider provider, String name){
        currencyProviders.put(name, provider);
    }

    public Provider getProvider(String name){
        if(currencyProviders.containsKey(name))
            return currencyProviders.get(name);
        return null;
    }

    public Map<String, Provider> getCurrencyProviders() {
        return currencyProviders;
    }
}
