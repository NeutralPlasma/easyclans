package eu.virtusdevelops.easyclans.models;

public class Currency {
    private int id;
    private double value;
    private String name;
    public int clanId;

    public Currency(int id, double value, String name, int clanId) {
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

    public int getClanId() {
        return clanId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
