package eu.virtusdevelops.easyclans.models;

import java.util.UUID;

public class CRequest {
    private int requestId;
    private int clanId;
    private UUID playerUuid;
    private long expireTime;
    private long createdTime;

    public CRequest(int requestId, int clanId, UUID playerUuid, long expireTime, long createdTime) {
        this.requestId = requestId;
        this.clanId = clanId;
        this.playerUuid = playerUuid;
        this.expireTime = expireTime;
        this.createdTime = createdTime;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getClanId() {
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
