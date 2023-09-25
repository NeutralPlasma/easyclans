package net.astrona.easyclans.controller;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.models.CRequest;
import net.astrona.easyclans.models.Log;
import net.astrona.easyclans.models.LogType;
import net.astrona.easyclans.storage.SQLStorage;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RequestsController {
    private final Map<Integer, CRequest> requests;
    private int count;
    private final ClansPlugin plugin;
    private final SQLStorage sqlStorage;

    public RequestsController(ClansPlugin plugin, SQLStorage sqlStorage) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        this.requests = new HashMap<>();
        init();
    }

    private void init() {
        for (var request : sqlStorage.getAllRequests()) {
            requests.put(request.getRequestId(), request);
        }
    }

    /**
     * Adds a new request to the request list.
     *
     * @param clanId      the ID of the clan associated with the request.
     * @param playerUuid  the UUID of the player who made the request.
     * @param expireTime  the expiration time of the request.
     * @param createdTime the creation time of the request.
     * @return the newly created request object.
     */
    public CRequest createRequest(int clanId, UUID playerUuid, long expireTime, long createdTime) {
        this.count = this.count + 1;
        CRequest cRequest = new CRequest(this.count, clanId, playerUuid, expireTime, createdTime);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sqlStorage.insertRequest(cRequest);
            requests.put(cRequest.getRequestId(), cRequest);
        });

        return cRequest;
    }

    public List<CRequest> getClanRequests(int clanId) {
        return requests.values().stream().filter(cRequest -> cRequest.getClanId() == clanId).toList();
    }

    public void deleteRequest(CRequest cRequest) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sqlStorage.deleteRequest(cRequest);
            requests.remove(cRequest.getRequestId());
        });
    }


    public void cleanExpired(){
        for(var request : requests.values()){
            if(request.getExpireTime() < System.currentTimeMillis()){
                sqlStorage.deleteRequest(request);
                // todo: make notification
                sqlStorage.addLog(new Log(String.valueOf(request.getExpireTime()), request.getPlayerUuid(), request.getClanId(), LogType.REQUEST_EXPIRED));
                requests.remove(request.getRequestId());
            }
        }
    }
}
