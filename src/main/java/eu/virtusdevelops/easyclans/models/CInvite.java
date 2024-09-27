package eu.virtusdevelops.easyclans.models;

import java.util.UUID;

public record CInvite(UUID inviteId, UUID clanId, UUID playerUuid, long expireTime, long createdTime) {
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        return currentTime >= createdTime && currentTime <= expireTime;
    }
}
