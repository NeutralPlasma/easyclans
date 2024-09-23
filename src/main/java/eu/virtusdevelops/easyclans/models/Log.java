package eu.virtusdevelops.easyclans.models;


import java.util.UUID;

public record Log(long id, String log, UUID player, UUID clan, LogType type, long timeStamp) {
    public Log(String log, UUID player, UUID clan, LogType type){
        this(-1L, log, player, clan, type, System.currentTimeMillis());
    }

    public Log(long id, String log, UUID player, UUID clan, LogType type){
        this(id, log, player, clan, type, System.currentTimeMillis());
    }

}

