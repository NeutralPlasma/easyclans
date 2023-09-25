package net.astrona.easyclans.models;


import java.util.UUID;

public record Log(String log, UUID player, int clan, LogType type) {



}

