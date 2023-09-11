package net.astrona.easyclans.controller;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.models.CRequest;
import net.astrona.easyclans.storage.SQLStorage;

import java.util.HashMap;
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
    }

    /**
     * Adds a new request to the request list.
     *
     * @param clanId the ID of the clan associated with the request.
     * @param playerUuid the UUID of the player who made the request.
     * @param expireTime the expiration time of the request.
     * @param createdTime the creation time of the request.
     * @return the newly created request object.
     */
    public CRequest addRequest(int clanId, UUID playerUuid, long expireTime, long createdTime) {
        this.count = this.count + 1;
        CRequest cRequest = new CRequest(this.count, clanId, playerUuid, expireTime, createdTime);

        requests.put(cRequest.requestId(), cRequest);
        return cRequest;
    }

    /**
     * Checks if a request with the given ID is currently active.
     *
     * @param requestId the ID of the request to check.
     * @return {@code true} if the request is active (not expired), {@code false} otherwise.
     */
    public boolean isRequestActive(int requestId) {
        CRequest cRequest = this.getRequest(requestId);
        long currentTime = System.currentTimeMillis();

        return currentTime >= cRequest.createdTime() && currentTime <= cRequest.expireTime();
    }

    /**
     * Retrieves a request object by its ID.
     *
     * @param id the ID of the request to retrieve.
     * @return the request object associated with the provided ID, or {@code null} if no
     *         request with the given ID is found.
     */
    public CRequest getRequest(int id) {
        return this.requests.get(id);
    }

}
