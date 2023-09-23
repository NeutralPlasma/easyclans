package net.astrona.easyclans.controller;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.storage.SQLStorage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ClansController {
    private PlayerController playerController;
    private final Map<Integer, Clan> clans;
    private final ClansPlugin plugin;
    private final SQLStorage sqlStorage;

    public ClansController(ClansPlugin plugin, SQLStorage sqlStorage, PlayerController playerController) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        this.playerController = playerController;
        this.clans = new HashMap<>();

        loadClans();
    }

    private void loadClans() {
        for (var clan : sqlStorage.getAllClans()) {
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
     * @param banner               the banner item for the clan.
     * @param bank                 the initial bank balance of the clan.
     * @param interestRate         the interest rate
     * @param tag                  the tag associated with the clan.
     * @param members              the list of UUIDs of clan members.
     */
    public Clan createClan(UUID owner, String name, String displayName, int autoKickTime,
                           int joinPointsPrice, double joinMoneyPrice,
                           ItemStack banner, double bank, double interestRate, String tag, List<UUID> members) {

        Clan clan = new Clan(-1, owner, name, displayName, autoKickTime, joinPointsPrice, joinMoneyPrice,
                banner, bank, interestRate, tag, members, System.currentTimeMillis());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sqlStorage.saveClan(clan); // clan gets new ID when this is executed.
            // TODO: update player sent new clan id.
            var player = playerController.getPlayer(clan.getOwner());
            player.setClanID(clan.getId());
            playerController.updatePlayer(player);
            addClan(clan);
        });
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
