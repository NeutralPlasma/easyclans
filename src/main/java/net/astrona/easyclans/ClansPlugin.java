package net.astrona.easyclans;

import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.gui.Handler;
import net.astrona.easyclans.listener.PlayerConnectionListener;
import net.astrona.easyclans.storage.SQLStorage;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ClansPlugin extends JavaPlugin {
    private Handler guiHandler;
    public final static MiniMessage MM = MiniMessage.miniMessage();
    @Override
    public void onEnable() {
        SQLStorage sqlStorage = new SQLStorage(this);
        PlayerController playerController = new PlayerController(this, sqlStorage);

        this.registerListeners(playerController);
        this.registerGUI();
    }

    private void registerListeners(PlayerController playerController) {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerConnectionListener(this, playerController), this);
    }

    private void registerGUI(){
        guiHandler = new Handler(this);
    }
}