package eu.virtusdevelops.easyclans.models;


import java.util.UUID;

public record Log(String log, UUID player, UUID clan, LogType type, long timeStamp) {
    public Log(String log, UUID player, UUID clan, LogType type){
        this(log, player, clan, type, System.currentTimeMillis());
    }


}

