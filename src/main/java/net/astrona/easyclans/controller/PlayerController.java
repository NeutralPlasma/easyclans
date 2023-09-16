package net.astrona.easyclans.controller;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.storage.SQLStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerController {
    private final Map<UUID, CPlayer> players;
    private final ClansPlugin plugin;
    private final SQLStorage sqlStorage;

    public PlayerController(ClansPlugin plugin, SQLStorage sqlStorage) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        this.players = new HashMap<>();
        init();
    }

    private void init(){
        var lPlayers = sqlStorage.getAllPlayers();
        for(CPlayer cplayer : lPlayers){
            players.put(cplayer.getUuid(), cplayer);
        }
    }

    /**
     * Adds a new player to the player cache.
     *
     * @param player the player object
     */
    private void addPlayer(CPlayer player) {
        players.put(player.getUuid(), player);
    }


    public CPlayer createPlayer(Player player){
        CPlayer cPlayer = new CPlayer(player.getUniqueId(),
                -1,
                System.currentTimeMillis(),
                0,
                player.getName()
        );

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sqlStorage.insertPlayer(cPlayer);
        });
        addPlayer(cPlayer);
        return cPlayer;
    }

    public void loadPlayer(Player player){
        if(!players.containsKey(player.getUniqueId())){
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                CPlayer cplayer = sqlStorage.getPlayer(player.getUniqueId());
                if(cplayer == null){
                    cplayer = new CPlayer(player.getUniqueId(),
                            -1,
                            System.currentTimeMillis(),
                            0,
                            player.getName()
                    );
                    cplayer.setActive(true);
                    sqlStorage.insertPlayer(cplayer);
                }
                addPlayer(cplayer);
            });
        }else{
            players.get(player.getUniqueId()).setActive(true);
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
     * @param cPlayer the player :3
     */
    public void updatePlayer(CPlayer cPlayer){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sqlStorage.updatePlayer(cPlayer);
        });
    }

    public List<CPlayer> getPlayers() {
        return players.values().stream().toList();
    }


    public List<CPlayer> getClanPlayers(int clan_id){
        List<CPlayer> playerss = new ArrayList<>();
        for(CPlayer player : players.values()){
            if(player.getClanID() == clan_id){
                playerss.add(player);
            }
        }
        return playerss;
    }
}
