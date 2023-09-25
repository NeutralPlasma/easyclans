package net.astrona.easyclans.controller;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.models.Log;
import net.astrona.easyclans.storage.SQLStorage;
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
