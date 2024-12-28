package eu.virtusdevelops.easyclans.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.dao.*;
import eu.virtusdevelops.easyclans.models.*;
import eu.virtusdevelops.easyclans.models.Currency;
import eu.virtusdevelops.easyclans.storage.mysql.*;
import eu.virtusdevelops.easyclans.storage.mysql.mappers.*;
import eu.virtusdevelops.easyclans.utils.Serialization;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SQLStorage {

    private final ClansPlugin plugin;
    private final Logger logger;
    private HikariDataSource dataSource;
    private static ExecutorService executor;




    private CInviteDao cInviteDao;
    private ClanDao clanDao;
    private CPlayerDao cPlayerDao;
    private CRequestDao cRequestDao;
    private CurrencyDao currencyDao;
    private LogDao logDao;
    private NotificationDao notificationDao;
    private TrophyDao trophyDao;

    private final CInviteMapperMysql cinviteMapper = new CInviteMapperMysql();
    private final CRequestMapperMysql crequestMapper = new CRequestMapperMysql();
    private final CPlayerMapperMysql cPlayerMapperMysql = new CPlayerMapperMysql();
    private final PermissionMapperMysql permissionMapperMysql = new PermissionMapperMysql();
    private final ClanMysqlMapper clanMysqlMapper = new ClanMysqlMapper();
    private final CurrencyMapperMysql currencyMapperMysql = new CurrencyMapperMysql();
    private final NotificationMapperMysql notificationMapperMysql = new NotificationMapperMysql();
    private final LogMapperMysql logMapperMysql = new LogMapperMysql();


    private boolean isMysql = false;


    public SQLStorage(ClansPlugin plugin) {
        this.logger = plugin.getLogger();
        this.plugin = plugin;

        ConfigurationSection dbSection = plugin.getConfig().getConfigurationSection("db");

        if (dbSection == null) {
            logger.severe("Database properties not found in 'config.yml'.");
            // disable plugin
            //plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }



        setupDatSource();
        setupDaos();
        initDaos();
    }


    private void setupDatSource(){
        HikariConfig config;
        if (isMysql) {
            config = setupMysqlDataSource();
        } else {
            config = setupH2DataSource();
        }
        config.setPoolName("EasyClansPlugin");

        this.dataSource = new HikariDataSource(config);
    }


    private HikariConfig setupMysqlDataSource(){

        String username = plugin.getConfig().getString("username");
        String password = plugin.getConfig().getString("password");
        String hostname = plugin.getConfig().getString("ip");
        int port = plugin.getConfig().getInt("port");
        String database = plugin.getConfig().getString("database");
        boolean SSL = plugin.getConfig().getBoolean("ssl");
        String path = plugin.getDataFolder().getPath();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        hikariConfig.addDataSourceProperty("user", username);
        hikariConfig.addDataSourceProperty("password", password);
        hikariConfig.addDataSourceProperty("useSSL", SSL);
        hikariConfig.addDataSourceProperty("databaseName", database);
        hikariConfig.addDataSourceProperty("serverName", hostname);
        hikariConfig.addDataSourceProperty("port", port);

        hikariConfig.setMaximumPoolSize(60000);
        hikariConfig.setMaximumPoolSize(10);

        return hikariConfig;
    }


    private HikariConfig setupH2DataSource(){
        var classLoader = loadH2Library();

        try{
            Thread.currentThread().setContextClassLoader(classLoader);
            var h2Driver = classLoader.loadClass("org.h2.Driver");
            if(h2Driver == null){
                throw new ClassNotFoundException("org.h2.Driver");
            }
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setMaximumPoolSize(60000);
            hikariConfig.setMaximumPoolSize(15);
            hikariConfig.setDriverClassName(h2Driver.getName());

            String path = plugin.getDataFolder().getAbsolutePath();
            hikariConfig.setJdbcUrl("jdbc:h2:" + path + "/db.h2");
            return hikariConfig;

        }catch (ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    private void setupDaos() {
        cInviteDao = new CInviteMysql(logger, dataSource, cinviteMapper);
        clanDao = new ClanMysql(logger, dataSource, clanMysqlMapper, currencyMapperMysql);
        cPlayerDao = new CPlayerMysql(logger, dataSource, cPlayerMapperMysql, permissionMapperMysql, notificationMapperMysql);
        cRequestDao = new CRequestMysql(logger, dataSource, crequestMapper);
        currencyDao = new CurrencyMysql(logger, dataSource, currencyMapperMysql);
        logDao = new LogMysql(logger, dataSource, logMapperMysql);
        notificationDao = new NotificationMysql();
        trophyDao = new TrophyMysql();
    }


    private void initDaos(){
        cPlayerDao.init();
        clanDao.init();
        currencyDao.init();
        logDao.init();
        notificationDao.init();
        trophyDao.init();
        cInviteDao.init();
        cRequestDao.init();
    }



    private IsolatedClassLoader loadH2Library() {
        String jarUrl = "https://repo1.maven.org/maven2/com/h2database/h2/2.3.232/h2-2.3.232.jar";

        // Get plugin folder and ensure the 'libs' subfolder exists
        Path pluginFolder = plugin.getDataFolder().toPath();
        Path libsFolder = pluginFolder.resolve("libs");
        if (!Files.exists(libsFolder)) {
            try {
                Files.createDirectories(libsFolder);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        // Check if the JAR file exists
        Path jarPath = libsFolder.resolve("h2-2.3.232.jar");
        if (!Files.exists(jarPath)) {
            // If not, download it
            try (InputStream in = new URL(jarUrl).openStream()) {
                Files.copy(in, jarPath);
                logger.info("Downloaded H2 Library");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            logger.info("H2 library already exists");
        }

        // Load the jar into the classloader dynamically
        return loadJarFile(jarPath.toString());
    }

    private IsolatedClassLoader loadJarFile(String jarFilePath) {
        try {
            Path path = Paths.get(jarFilePath);
            URL jarUrl = path.toUri().toURL();
            IsolatedClassLoader classLoader = new IsolatedClassLoader(
                    new URL[]{jarUrl}
            );
            return classLoader;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public CInviteDao getcInviteDao() {
        return cInviteDao;
    }

    public ClanDao getClanDao() {
        return clanDao;
    }

    public CPlayerDao getcPlayerDao() {
        return cPlayerDao;
    }

    public CRequestDao getcRequestDao() {
        return cRequestDao;
    }

    public CurrencyDao getCurrencyDao() {
        return currencyDao;
    }

    public LogDao getLogDao() {
        return logDao;
    }

    public NotificationDao getNotificationDao() {
        return notificationDao;
    }

    public TrophyDao getTrophyDao() {
        return trophyDao;
    }

}
