package eu.virtusdevelops.easyclans.models;

import java.util.UUID;

public class ClanTrophy {
    private UUID id, clanID;
    private int ranking;
    private long achievedDate;

    public ClanTrophy(UUID id, UUID clanID, int ranking, long achievedDate) {
        this.id = id;
        this.clanID = clanID;
        this.ranking = ranking;
        this.achievedDate = achievedDate;
    }

    public ClanTrophy(UUID clanID, int ranking, long achievedDate) {
        this.id = UUID.randomUUID();
        this.clanID = clanID;
        this.ranking = ranking;
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
}
