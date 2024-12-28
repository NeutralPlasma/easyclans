package eu.virtusdevelops.easyclans;

import eu.virtusdevelops.easyclans.commands.CommandsRegister;
import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.dao.*;
import eu.virtusdevelops.easyclans.gui.Handler;
import eu.virtusdevelops.easyclans.listener.PlayerChatListener;
import eu.virtusdevelops.easyclans.listener.PlayerConnectionListener;
import eu.virtusdevelops.easyclans.listener.PlayerDamageListener;
import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.storage.mysql.CInviteMysql;
import eu.virtusdevelops.easyclans.storage.mysql.CPlayerMysql;
import me.clip.placeholderapi.PlaceholderAPI;
import eu.virtusdevelops.easyclans.storage.SQLStorage;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.kyori.adventure.text.Component.text;

public class ClansPlugin extends JavaPlugin {


    private BukkitAudiences bukkitAudiences;
    private MinecraftHelp<CommandSender> minecraftHelp;

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


    private static ExecutorService executor;

    private BukkitTask bgTask;
    private boolean inited = false;
    public final static MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        LanguageController.loadLocals(this);
        executor = Executors.newFixedThreadPool(2);
        sqlStorage = new SQLStorage(this);
        logController = new LogController(sqlStorage.getLogDao());

        currenciesController = new CurrenciesController(this, sqlStorage.getCurrencyDao());
        ranksController = new RanksController(this, this.getLogger());
        playerController = new PlayerController(this, sqlStorage.getcPlayerDao(), ranksController);
        clansController = new ClansController(this, playerController, currenciesController, ranksController,
                sqlStorage.getClanDao(), logController);
        requestsController = new RequestsController(sqlStorage.getcRequestDao());
        invitesController = new InvitesController(this);
        tropyController = new TropyController(this);

        this.registerListeners();
        this.registerCommands();
        this.registerGUI();
        guiHandler.init();

        bgTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {

            requestsController.cleanExpired();
            clansController.processClans();

        }, 100L, getConfig().getLong("clan.update_interval"));

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new PlaceholderController(this, playerController, clansController).register();
        }

        ranksController.loadRankMultipliers();



        this.inited = true;
    }

    @Override
    public void onDisable() {
        // check if database initied, then force save

        // check executorservice and clear it


        if (inited) {
            bgTask.cancel();
            guiHandler.disable();
        }
        executor.shutdown();
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        connectionListener = new PlayerConnectionListener(this, playerController, ranksController);
        pluginManager.registerEvents(connectionListener, this);
        pluginManager.registerEvents(new PlayerChatListener(this.getConfig(), playerController, clansController), this);
        pluginManager.registerEvents(new PlayerDamageListener(playerController, clansController), this);
    }

    private void registerCommands() {
        //getCommand("clans").setExecutor(new ClansCommand(playerController, clansController, requestsController, this, logController, currenciesController, invitesController, ranksController, sqlStorage));
        //getCommand("trophy").setExecutor(new TrophyCommand(this, clansController, playerController, tropyController));


        final PaperCommandManager<CommandSender> manager = new PaperCommandManager<>(
                this,
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        );

        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            // Register Brigadier mappings for rich completions
            manager.registerBrigadier();
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            // Use Paper async completions API (see Javadoc for why we don't use this with Brigadier)
            manager.registerAsynchronousCompletions();
        }




        this.bukkitAudiences = BukkitAudiences.create(this);

        MinecraftExceptionHandler.create(this.bukkitAudiences::sender)
                .defaultHandlers()
                .decorator(component -> text()
                    .append(text("[", NamedTextColor.DARK_GRAY))
                    .append(text("Clans", NamedTextColor.GOLD))
                    .append(text("] ", NamedTextColor.DARK_GRAY))
                    .append(component).build()
                )
                .registerTo(manager);


        this.minecraftHelp = MinecraftHelp.<CommandSender>builder()
                .commandManager(manager)
                .audienceProvider(bukkitAudiences::sender)
                .commandPrefix("/clan help")
//                .messageProvider(MinecraftHelp.captionMessageProvider(
//                        manager.captionRegistry(),
//                        ComponentCaptionFormatter.miniMessage()
//                ))
                .build();
        //manager.captionRegistry().registerProvider(MinecraftHelp.defaultCaptionsProvider());


        new CommandsRegister(this, manager);

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


    public PlayerController getPlayerController() {
        return playerController;
    }

    public ClansController getClansController() {
        return clansController;
    }

    public RequestsController getRequestsController() {
        return requestsController;
    }

    public LogController getLogController() {
        return logController;
    }

    public SQLStorage getSqlStorage() {
        return sqlStorage;
    }

    public CurrenciesController getCurrenciesController() {
        return currenciesController;
    }

    public InvitesController getInvitesController() {
        return invitesController;
    }

    public TropyController getTropyController() {
        return tropyController;
    }

    public RanksController getRanksController() {
        return ranksController;
    }

    public MinecraftHelp<CommandSender> getMinecraftHelp() {
        return minecraftHelp;
    }

    public static ExecutorService getExecutor(){
        return executor;
    }
}