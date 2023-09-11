package net.astrona.easyclans;

import net.astrona.easyclans.commands.ClansCommand;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.gui.Handler;
import net.astrona.easyclans.listener.PlayerConnectionListener;
import net.astrona.easyclans.storage.SQLStorage;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ClansPlugin extends JavaPlugin {
    private Handler guiHandler;
    private PlayerController playerController;
    private ClansController clansController;
    private SQLStorage sqlStorage;
    public final static MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        sqlStorage = new SQLStorage(this);

        playerController = new PlayerController(this, sqlStorage);
        clansController = new ClansController(this, sqlStorage);

        this.registerListeners();
        this.registerCommands();
        this.registerGUI();
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerConnectionListener(this, playerController), this);
    }

    private void registerCommands(){

        getCommand("clans").setExecutor(new ClansCommand(playerController, clansController, this));
    }

    private void registerGUI() {
        guiHandler = new Handler(this);
    }
}