package eu.virtusdevelops.easyclans.models;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Clan {
    private UUID id;
    private UUID owner;
    private String name;
    private String displayName;
    private int autoKickTime;
    private int joinPointsPrice;
    private double joinMoneyPrice;
    private ItemStack banner;
    private double interestRate;
    private String tag;
    private List<UUID> members;
    private List<Trophy> trophies;
    private List<Currency> currencies;
    private long createdOn;
    private boolean pvpEnabled;

    // local stuff
    private double tempInterestRate = 0.0, actualInterestRate;


    public Clan(UUID owner, String name, String displayName, int autoKickTime, int joinPointsPrice,
                double joinMoneyPrice, ItemStack banner, double interestRate, String tag, List<UUID> members, boolean pvpEnabled, long createdOn) {
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.name = name;
        this.displayName = displayName;
        this.autoKickTime = autoKickTime;
        this.joinPointsPrice = joinPointsPrice;
        this.joinMoneyPrice = joinMoneyPrice;
        this.interestRate = interestRate;
        this.banner = banner;
        this.tag = tag;
        this.members = members;
        this.pvpEnabled = pvpEnabled;
        this.createdOn = createdOn;
        this.currencies = new ArrayList<>();
        this.trophies = new ArrayList<>();
    }

    public Clan(UUID id, UUID owner, String name, String displayName, int autoKickTime, int joinPointsPrice,
                double joinMoneyPrice, ItemStack banner, double interestRate, String tag, List<UUID> members, boolean pvpEnabled, long createdOn) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.displayName = displayName;
        this.autoKickTime = autoKickTime;
        this.joinPointsPrice = joinPointsPrice;
        this.joinMoneyPrice = joinMoneyPrice;
        this.interestRate = interestRate;
        this.banner = banner;
        this.tag = tag;
        this.members = members;
        this.pvpEnabled = pvpEnabled;
        this.createdOn = createdOn;
        this.currencies = new ArrayList<>();
        this.trophies = new ArrayList<>();
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public UUID getId() {
        return id;
    }


    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name.replace("\\<", "<");
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName.replace("\\<", "<");
    }

    public int getAutoKickTime() {
        return autoKickTime;
    }

    public void setAutoKickTime(int autoKickTime) {
        this.autoKickTime = autoKickTime;
    }

    public int getJoinPointsPrice() {
        return joinPointsPrice;
    }

    public void setJoinPointsPrice(int joinPointsPrice) {
        this.joinPointsPrice = joinPointsPrice;
    }

    public double getJoinMoneyPrice() {
        return joinMoneyPrice;
    }

    public void setJoinMoneyPrice(double joinMoneyPrice) {
        this.joinMoneyPrice = joinMoneyPrice;
    }

    public ItemStack getBanner() {
        return banner;
    }

    public void setBanner(ItemStack banner) {
        this.banner = banner;
    }

    //public double getBank() {
    //    return bank;
    //}

    //public void setBank(double bank) {
    //    this.bank = bank;
    //}

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID uuid){
        members.add(uuid);
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public void setMembers(List<UUID> members) {
        this.members = members;
    }

    public void addCurrency(Currency currency) {
        this.currencies.add(currency);
    }

    public Currency getCurrency(String name) {
        for (var currency : currencies) {
            if (currency.getName().equals(name))
                return currency;
        }
        return null;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }


    public void updateActualInterestRate(){
        this.actualInterestRate = tempInterestRate;
    }

    public double getActualInterestRate() {
        return actualInterestRate;
    }

    public double getTempInterestRate() {
        return tempInterestRate;
    }
    public void addTempInterestRate(double interestRate){
        this.tempInterestRate+=interestRate;
    }

    public void resetTempInterestRate(){
        this.tempInterestRate = 0.0;
    }

    public boolean isPvpEnabled() {
        return pvpEnabled;
    }

    public void setPvpEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }
}
