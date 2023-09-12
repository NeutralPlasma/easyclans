package net.astrona.easyclans.controller;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.storage.SQLStorage;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClansController {
    private final Map<Integer, Clan> clans;
    private int count;
    private final ClansPlugin plugin;
    private final SQLStorage sqlStorage;

    public ClansController(ClansPlugin plugin, SQLStorage sqlStorage) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        this.clans = new HashMap<>();
    }

    /**
     * Adds a new clan to the clan list.
     *
     * @param owner the UUID of the player who owns the clan.
     * @param name the name of the clan.
     * @param displayName the display name of the clan.
     * @param autoKickTime the auto-kick time for inactive members.
     * @param joinPointsPrice the points price for joining the clan.
     * @param joinMoneyPrice the money price for joining the clan.
     * @param autoPayOutTime the auto-payout time for clan bank.
     * @param autoPayOutPercentage the auto-payout percentage for clan bank.
     * @param banner the banner item for the clan.
     * @param bank the initial bank balance of the clan.
     * @param tag the tag associated with the clan.
     * @param members the list of UUIDs of clan members.
     * @return the newly created clan object.
     */
    public Clan addClan(CPlayer owner, String name, String displayName, int autoKickTime,
                        int joinPointsPrice, int joinMoneyPrice, int autoPayOutTime, double autoPayOutPercentage,
                        ItemStack banner, double bank, String tag, List<CPlayer> members) {

        this.count = this.count + 1;
        Clan clan = new Clan(this.count, owner, name, displayName, autoKickTime, joinPointsPrice, joinMoneyPrice,
                autoPayOutTime, autoPayOutPercentage, banner, bank, tag, members, true);

        clans.put(clan.getId(), clan);
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
        return clans.get(id);
    }
}
