package net.astrona.easyclans.models;

import java.util.UUID;

public record CInvite(int inviteId, int clanId, UUID playerUuid, long expiredTime, long createdTime) {
}
