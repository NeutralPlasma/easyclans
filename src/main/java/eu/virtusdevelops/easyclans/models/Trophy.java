package eu.virtusdevelops.easyclans.models;

import java.util.*;

public class Trophy {
    private UUID id;
    private String name, title, description;
    private long startDate, endDate;
    private TreeMap<Integer, ClanTrophy> clansData = new TreeMap<>();

    public Trophy(String name, String title, String description, long startDate, long endDate) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Trophy(UUID id, String name, String title, String description, long startDate, long endDate) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Map<Integer, ClanTrophy> getOrganizedTrophies(){
        return clansData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    public void addTrophy(ClanTrophy trophy){
        clansData.put(trophy.getRanking(), trophy);
    }

    public ClanTrophy getTrophy(int position){
        return clansData.getOrDefault(position, null);
    }

    public ClanTrophy getTrophy(Clan clan){
        for(var cTrophy : clansData.values()){
            if(cTrophy.getClanID().equals(clan.getId()))
                return cTrophy;
        }
        return null;
    }
}
