package eu.virtusdevelops.easyclans.controller;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.UserPermissions;
import eu.virtusdevelops.easyclans.storage.SQLStorage;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerController {
    private final Map<UUID, CPlayer> players;
    private List<UserPermissions> permissions = new ArrayList<>();
    private final ClansPlugin plugin;
    private final SQLStorage sqlStorage;

    public PlayerController(ClansPlugin plugin, SQLStorage sqlStorage) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        this.players = new HashMap<>();
        init();
    }

    private void init() {
        var lPlayers = sqlStorage.getAllPlayers();
        for (CPlayer cplayer : lPlayers) {
            players.put(cplayer.getUuid(), cplayer);
        }

        // load default perms
        var list = plugin.getConfig().getStringList("default_permissions");
        permissions.addAll(list.stream().map(UserPermissions::valueOf).toList());
    }

    /**
     * Adds a new player to the player cache.
     *
     * @param player the player object
     */
    private void addPlayer(CPlayer player) {
        players.put(player.getUuid(), player);
    }


    public CPlayer createPlayer(Player player) {
        User user = ClansPlugin.Ranks.getPlayerAdapter(Player.class).getUser(player);
        CPlayer cPlayer = new CPlayer(player.getUniqueId(),
                null,
                System.currentTimeMillis(),
                0,
                player.getName(),
                user.getPrimaryGroup()
        );
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sqlStorage.insertPlayer(cPlayer);
        });
        addPlayer(cPlayer);
        return cPlayer;
    }

    public void addDefaultPermissions(CPlayer cPlayer){
        for(var perm: permissions){
            if(!cPlayer.hasPermission(perm))
                cPlayer.addPermission(perm);
        }
    }

    public void setDefaultPermissions(CPlayer cPlayer){
        cPlayer.getUserPermissionsList().clear();
        for(var perm: permissions){
            cPlayer.addPermission(perm);
        }
    }


    /**
     * Removes a player from the player cache.
     *
     * @param uuid the UUID of the player to remove.
     */
    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    /**
     * Retrieves a cached player object based on their UUID.
     *
     * @param uuid the id of the player
     * @return the cached player object associated with the provided UUID,
     *         or null if no player with the given UUID is found in the cache.
     */
    public CPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }


    /**
     * Updates player in database
     *
     * @param cPlayer the clan player
     */
    public void updatePlayer(CPlayer cPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sqlStorage.updatePlayer(cPlayer);
        });
    }

    public List<CPlayer> getPlayers() {
        return players.values().stream().toList();
    }


    public List<CPlayer> getClanPlayers(UUID clan_id) {
        List<CPlayer> playerss = new ArrayList<>();
        for (CPlayer player : players.values()) {
            if (player.getClanID() == clan_id) {
                playerss.add(player);
            }
        }
        return playerss;
    }
}
