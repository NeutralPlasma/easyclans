package eu.virtusdevelops.easyclans.models;

import java.util.*;

public class Trophy {
    private UUID id;
    private String title, description;
    private long startDate, endDate;
    private List<ClanTrophy> clansData = new ArrayList<>();

    public Trophy(String title, String description, long startDate, long endDate) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Trophy(UUID id,String title, String description, long startDate, long endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public List<ClanTrophy> getOrganizedTrophies(){
        return clansData.stream().sorted(Comparator.comparingInt(ClanTrophy::getRanking)).toList();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public List<ClanTrophy> getClansData() {
        return clansData;
    }

    public void addTrophy(ClanTrophy trophy){
        clansData.add(trophy);
    }

    public ClanTrophy getTrophy(Clan clan){
        for(var cTrophy : clansData){
            if(cTrophy.getClanID().equals(clan.getId()))
                return cTrophy;
        }
        return null;
    }
}
