package net.astrona.easyclans.controller;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.models.CInvite;
import net.astrona.easyclans.models.CRequest;
import net.astrona.easyclans.storage.SQLStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InvitesController {
    private final Map<Integer, CInvite> invites;
    private int count;
    private final ClansPlugin plugin;
    private final SQLStorage sqlStorage;

    public InvitesController(ClansPlugin plugin, SQLStorage sqlStorage) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        this.invites = new HashMap<>();
    }

    /**
     * Adds a new invite to the invitation list.
     *
     * @param clanId the ID of the clan associated with the invite.
     * @param playerUuid the UUID of the player who is invited.
     * @param expireTime the expiration time of the invite.
     * @param createdTime the creation time of the invite.
     * @return the newly created invite object.
     */
    public CInvite addInvite(int clanId, UUID playerUuid, long expireTime, long createdTime) {
        this.count = this.count + 1;
        CInvite cInvite = new CInvite(this.count, clanId, playerUuid, expireTime, createdTime);

        invites.put(cInvite.inviteId(), cInvite);
        return cInvite;
    }



    /**
     * Retrieves an Invite object by its ID.
     *
     * @param id the ID of the invite to retrieve.
     * @return the invite object associated with the provided ID, or {@code null} if no
     *         invite with the given ID is found.
     */
    public CInvite getInvite(int id) {
        return this.invites.get(id);
    }

}
