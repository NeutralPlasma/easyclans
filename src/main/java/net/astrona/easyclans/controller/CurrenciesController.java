package net.astrona.easyclans.controller;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.providers.Provider;
import net.astrona.easyclans.providers.VaultProvider;
import net.astrona.easyclans.providers.VotingPluginProvider;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class CurrenciesController {
    private ClansPlugin plugin;
    private Map<String, Provider> currencyProviders = new HashMap<>();

    public CurrenciesController(ClansPlugin plugin){
        this.plugin = plugin;
        load();
    }

    private void load(){
        var pm = Bukkit.getPluginManager();
        if(pm.isPluginEnabled("VotingPlugin")){
            var provider = new VotingPluginProvider();
            if(provider.setup())
                addProvider(provider, "VotingPlugin");
        }
        if(pm.isPluginEnabled("Vault")){
            var provider = new VaultProvider();
            if(provider.setup())
                addProvider(provider, "Vault");
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
