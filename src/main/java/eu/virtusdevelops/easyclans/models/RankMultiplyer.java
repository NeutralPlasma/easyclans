package eu.virtusdevelops.easyclans.models;

public class RankMultiplyer {
    private double multiplier;
    private String name;
    private int priority;


    public RankMultiplyer(String name, double multiplier, int priority){
        this.name = name;
        this.multiplier = multiplier;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
