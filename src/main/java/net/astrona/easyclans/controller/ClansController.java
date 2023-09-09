package net.astrona.easyclans.controller;

import net.astrona.easyclans.models.Clan;

import java.util.HashMap;
import java.util.Map;

public class ClansController {
    private final Map<String, Clan> clans;

    public ClansController() {
        this.clans = new HashMap<>();
    }
}
