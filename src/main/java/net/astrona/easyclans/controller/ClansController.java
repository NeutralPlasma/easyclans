package net.astrona.easyclans.controller;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.storage.SQLStorage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ClansController {
    private final Map<Integer, Clan> clans;
    private final ClansPlugin plugin;
    private final SQLStorage sqlStorage;

    public ClansController(ClansPlugin plugin, SQLStorage sqlStorage) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        this.clans = new HashMap<>();
    }

    private void loadClans() {
        for (var clan : sqlStorage.getAllClans()) {
            // TODO: get clan members and add them to cache
            this.clans.put(clan.getId(), clan);
        }
    }


    private void addClan(Clan clan) {
        clans.put(clan.getId(), clan);
    }

    /**
     * Adds a new clan to the clan list.
     *
     * @param owner                the UUID of the player who owns the clan.
     * @param name                 the name of the clan.
     * @param displayName          the display name of the clan.
     * @param autoKickTime         the auto-kick time for inactive members.
     * @param joinPointsPrice      the points price for joining the clan.
     * @param joinMoneyPrice       the money price for joining the clan.
     * @param autoPayOutTime       the auto-payout time for clan bank.
     * @param autoPayOutPercentage the auto-payout percentage for clan bank.
     * @param banner               the banner item for the clan.
     * @param bank                 the initial bank balance of the clan.
     * @param tag                  the tag associated with the clan.
     * @param members              the list of UUIDs of clan members.
     */
    public Clan createClan(UUID owner, String name, String displayName, int autoKickTime,
                           int joinPointsPrice, int joinMoneyPrice, int autoPayOutTime, double autoPayOutPercentage,
                           ItemStack banner, double bank, String tag, List<UUID> members) {

        Clan clan = new Clan(-1, owner, name, displayName, autoKickTime, joinPointsPrice, joinMoneyPrice,
                autoPayOutTime, autoPayOutPercentage, banner, bank, tag, members, System.currentTimeMillis());

        // TODO: database insert and update id
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            clan.setId(-1);
        });


        addClan(clan);
        return clan;
    }

    /**
     * Retrieves a clan object by its id.
     *
     * @param id the id of the clan you want to retrieve.
     * @return the clan object associated with the provided id, or null if
     *         no clan with the given id is found.
     */
    public Clan getClan(int id) {
        if (clans.containsKey(id))
            return clans.get(id);
        return null;
    }

    public List<Clan> getClans() {
        return clans.values().stream().toList();
    }
}
