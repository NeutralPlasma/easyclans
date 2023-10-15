package eu.virtusdevelops.easyclans.controller;

import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.storage.SQLStorage;
import org.bukkit.Bukkit;

public class LogController {

    private ClansPlugin plugin;
    private SQLStorage sqlStorage;


    public LogController(SQLStorage sqlStorage, ClansPlugin plugin){
        this.sqlStorage = sqlStorage;
        this.plugin = plugin;
    }


    public void addLog(Log log){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sqlStorage.addLog(log);
        });
    }
}
