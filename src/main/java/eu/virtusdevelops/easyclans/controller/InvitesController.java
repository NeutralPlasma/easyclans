package eu.virtusdevelops.easyclans.controller;

import eu.virtusdevelops.easyclans.models.CInvite;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.storage.SQLStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InvitesController {
    private final Map<UUID, CInvite> invites;
    private final ClansPlugin plugin;

    public InvitesController(ClansPlugin plugin) {
        this.plugin = plugin;
        this.invites = new HashMap<>();
    }

    /**
     * Adds a new invite to the invitation list.
     *
     * @param clanId      the ID of the clan associated with the invite.
     * @param playerUuid  the UUID of the player who is invited.
     * @param expireTime  the expiration time of the invite.
     * @param createdTime the creation time of the invite.
     * @return the newly created invite object.
     */
    public CInvite addInvite(UUID clanId, UUID playerUuid, long expireTime, long createdTime) {
        CInvite cInvite = new CInvite(UUID.randomUUID(), clanId, playerUuid, expireTime, createdTime);

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
