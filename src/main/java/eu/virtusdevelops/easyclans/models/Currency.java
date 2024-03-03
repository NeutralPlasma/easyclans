package eu.virtusdevelops.easyclans.models;

import java.util.UUID;

public class Currency {
    private UUID id;
    private double value;
    private String name;
    public UUID clanId;

    public Currency(UUID id, double value, String name, UUID clanId) {
        this.id = id;
        this.value = value;
        this.name = name;
        this.clanId = clanId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void addValue(double value){
        this.value+=value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getClanId() {
        return clanId;
    }

    public UUID getId() {
        return id;
    }


    @Override
    public String toString() {
        return getClass().getName()
                + "[amount:" + value
                + ";id:" + id
                + ";clanId:" + clanId
                + ";name:" + name
                + "]";
    }
}
