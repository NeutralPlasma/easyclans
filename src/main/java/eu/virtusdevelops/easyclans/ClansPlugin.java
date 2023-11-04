package eu.virtusdevelops.easyclans;

import eu.virtusdevelops.easyclans.commands.ClansCommand;
import eu.virtusdevelops.easyclans.commands.TrophyCommand;
import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.Handler;
import eu.virtusdevelops.easyclans.listener.PlayerChatListener;
import eu.virtusdevelops.easyclans.listener.PlayerConnectionListener;
import eu.virtusdevelops.easyclans.listener.PlayerDamageListener;
import me.clip.placeholderapi.PlaceholderAPI;
import eu.virtusdevelops.easyclans.storage.SQLStorage;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class ClansPlugin extends JavaPlugin {
    private Handler guiHandler;
    private PlayerController playerController;
    private ClansController clansController;
    private RequestsController requestsController;
    private LogController logController;
    private SQLStorage sqlStorage;
    private CurrenciesController currenciesController;
    private InvitesController invitesController;
    private TropyController tropyController;

    private BukkitTask bgTask;
    private boolean inited = false;
    public final static MiniMessage MM = MiniMessage.miniMessage();
    //public static Economy Economy = null;
    public static LuckPerms Ranks = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        LanguageController.loadLocals(this);
        sqlStorage = new SQLStorage(this);
        //setupEconomy();
        setupLuckPerms();
        logController = new LogController(sqlStorage, this);

        currenciesController = new CurrenciesController(this);
        playerController = new PlayerController(this, sqlStorage);
        clansController = new ClansController(this, sqlStorage, playerController, currenciesController);
        requestsController = new RequestsController(this, sqlStorage);
        invitesController = new InvitesController(this, sqlStorage);
        tropyController = new TropyController(this, sqlStorage);

        this.registerListeners();
        this.registerCommands();
        this.registerGUI();

        bgTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {

            requestsController.cleanExpired();
            clansController.processClans();

        }, 100L, getConfig().getLong("clan.update_interval"));

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            PlaceholderAPI.registerExpansion(new PlaceholderController(
                    this, playerController, clansController, requestsController));
        }



        this.inited = true;
    }

    @Override
    public void onDisable() {
        if (inited) {
            bgTask.cancel();
        }
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerConnectionListener(this, playerController), this);
        pluginManager.registerEvents(new PlayerChatListener(this.getConfig(), playerController, clansController), this);
        pluginManager.registerEvents(new PlayerDamageListener(playerController, clansController), this);
    }

    private void registerCommands() {
        getCommand("clans").setExecutor(new ClansCommand(playerController, clansController, requestsController, this, logController, currenciesController, invitesController, sqlStorage));
        getCommand("trophy").setExecutor(new TrophyCommand(this, clansController, playerController, tropyController));
    }

    private void registerGUI() {
        guiHandler = new Handler(this);
    }

    public String getVersion(){
        return getPluginMeta().getVersion();
    }

    public void reload(){
        reloadConfig();
        LanguageController.reload(this);
    }

    /*private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        Economy = rsp.getProvider();
        return Economy != null;
    }*/

    private boolean setupLuckPerms(){
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        Ranks = provider.getProvider();
        return Ranks != null;
    }




}