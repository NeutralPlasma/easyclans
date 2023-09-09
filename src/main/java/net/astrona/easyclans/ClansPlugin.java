package net.astrona.easyclans;

import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.listener.PlayerConnectionListener;
import net.astrona.easyclans.storage.SQLStorage;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ClansPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();



        SQLStorage sqlStorage = new SQLStorage(this);
        PlayerController playerController = new PlayerController(this, sqlStorage);




        this.listening(pluginManager, playerController);
    }

    private void listening(PluginManager pluginManager, PlayerController playerController) {
        pluginManager.registerEvents(new PlayerConnectionListener(this, playerController), this);
    }
}