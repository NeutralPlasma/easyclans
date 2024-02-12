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
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import static org.bukkit.Bukkit.getServer;

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
    private PlayerConnectionListener connectionListener;
    private RanksController ranksController;

    private BukkitTask bgTask;
    private boolean inited = false;
    public final static MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        LanguageController.loadLocals(this);
        sqlStorage = new SQLStorage(this);
        logController = new LogController(sqlStorage, this);

        currenciesController = new CurrenciesController(this);
        ranksController = new RanksController(this);
        playerController = new PlayerController(this, sqlStorage, ranksController);
        clansController = new ClansController(this, sqlStorage, playerController, currenciesController, ranksController);
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

        ranksController.loadRankMultipliers();
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
        connectionListener = new PlayerConnectionListener(this, playerController, ranksController);
        pluginManager.registerEvents(connectionListener, this);
        pluginManager.registerEvents(new PlayerChatListener(this.getConfig(), playerController, clansController), this);
        pluginManager.registerEvents(new PlayerDamageListener(playerController, clansController), this);
    }

    private void registerCommands() {
        getCommand("clans").setExecutor(new ClansCommand(playerController, clansController, requestsController, this, logController, currenciesController, invitesController, ranksController, sqlStorage));
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
        ranksController.loadRankMultipliers();
        LanguageController.reload(this);
    }






}