package net.astrona.easyclans.models;

import java.util.UUID;

public record CRequest(int requestId, int clanId, UUID playerUuid, long expireTime, long createdTime) {
}
