package eu.virtusdevelops.easyclans.controller;

import eu.virtusdevelops.easyclans.dao.LogDao;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.storage.SQLStorage;
import org.bukkit.Bukkit;

public class LogController {
    private final LogDao logDao;


    public LogController(LogDao logDao){
        this.logDao = logDao;
    }


    public void addLog(Log log){
        ClansPlugin.getExecutor().submit(() -> {
            logDao.save(log);
        });
    }
}
