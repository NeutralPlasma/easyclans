package eu.virtusdevelops.easyclans.models;

import java.util.UUID;

public class CRequest {
    private UUID requestId;
    private UUID clanId;
    private UUID playerUuid;
    private long expireTime;
    private long createdTime;

    public CRequest(UUID requestId, UUID clanId, UUID playerUuid, long expireTime, long createdTime) {
        this.requestId = requestId;
        this.clanId = clanId;
        this.playerUuid = playerUuid;
        this.expireTime = expireTime;
        this.createdTime = createdTime;
    }


    public UUID getId() {
        return requestId;
    }

    public UUID getClanId() {
        return clanId;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public boolean isValid(){
        return expireTime >= System.currentTimeMillis();
    }

    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + "[clanId:" + clanId
                + ";userId:" + playerUuid
                + ";id:" + requestId
                + ";createDate:" + createdTime
                + ";expireDate:" + expireTime
                + "]";

    }
}
