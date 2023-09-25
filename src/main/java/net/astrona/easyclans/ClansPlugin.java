package net.astrona.easyclans;

import net.astrona.easyclans.commands.ClansCommand;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.LanguageController;
import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.controller.RequestsController;
import net.astrona.easyclans.gui.Handler;
import net.astrona.easyclans.listener.PlayerChatListener;
import net.astrona.easyclans.listener.PlayerConnectionListener;
import net.astrona.easyclans.storage.SQLStorage;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Language;

public class ClansPlugin extends JavaPlugin {
    private Handler guiHandler;
    private PlayerController playerController;
    private ClansController clansController;
    private RequestsController requestsController;
    private SQLStorage sqlStorage;
    private boolean inited = false;
    public final static MiniMessage MM = MiniMessage.miniMessage();
    public static Economy Economy = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        LanguageController.loadLocals(this);
        sqlStorage = new SQLStorage(this);
        setupEconomy();

        playerController = new PlayerController(this, sqlStorage);
        clansController = new ClansController(this, sqlStorage, playerController);
        requestsController = new RequestsController(this, sqlStorage);

        this.registerListeners();
        this.registerCommands();
        this.registerGUI();
        this.inited = true;
    }

    @Override
    public void onDisable() {
        if (inited) {

        }
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerConnectionListener(this, playerController), this);
        pluginManager.registerEvents(new PlayerChatListener(this.getConfig(), playerController, clansController), this);
    }

    private void registerCommands() {
        getCommand("clans").setExecutor(new ClansCommand(playerController, clansController, requestsController, this));
    }

    private void registerGUI() {
        guiHandler = new Handler(this);
    }

    private boolean setupEconomy() {
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


}