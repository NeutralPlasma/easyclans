package net.astrona.easyclans.controller;

import net.astrona.easyclans.models.Clan;

import java.util.HashMap;
import java.util.Map;

public class ClansController {
    private final Map<Integer, Clan> clans;

    public ClansController() {
        this.clans = new HashMap<>();
    }

    /**
     * Retrieves a clan object by its id.
     *
     * @param id the id of the clan you want to retrieve.
     * @return the clan object associated with the provided id, or null if
     *         no clan with the given id is found.
     */
    public Clan getClan(int id) {
        return clans.get(id);
    }
}
