package eu.virtusdevelops.easyclans.models;

import java.util.UUID;

public class ClanTrophy {
    private UUID id, clanID, trophyID;
    private int ranking;
    private long achievedDate;

    public ClanTrophy(UUID id, UUID clanID, UUID trophyID, int ranking, long achievedDate) {
        this.id = id;
        this.clanID = clanID;
        this.ranking = ranking;
        this.trophyID = trophyID;
        this.achievedDate = achievedDate;
    }

    public ClanTrophy(UUID clanID, UUID trophyID, int ranking, long achievedDate) {
        this.id = UUID.randomUUID();
        this.clanID = clanID;
        this.ranking = ranking;
        this.trophyID = trophyID;
        this.achievedDate = achievedDate;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClanID() {
        return clanID;
    }

    public void setClanID(UUID clanID) {
        this.clanID = clanID;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public long getAchievedDate() {
        return achievedDate;
    }

    public void setAchievedDate(long achievedDate) {
        this.achievedDate = achievedDate;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTrophyID() {
        return trophyID;
    }

    public void setTrophyID(UUID trophyID) {
        this.trophyID = trophyID;
    }
}
