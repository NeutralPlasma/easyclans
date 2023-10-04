package net.astrona.easyclans.models;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Clan {
    private int id;
    private UUID owner;
    private String name;
    private String displayName;
    private int autoKickTime;
    private int joinPointsPrice;
    private double joinMoneyPrice;
    private ItemStack banner;
    private double bank, interestRate;
    private String tag;
    private List<UUID> members;
    private List<Currency> currencies;
    private long createdOn;

    public Clan(int id, UUID owner, String name, String displayName, int autoKickTime, int joinPointsPrice,
                double joinMoneyPrice, ItemStack banner, double bank, double interestRate, String tag, List<UUID> members, long createdOn) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.displayName = displayName;
        this.autoKickTime = autoKickTime;
        this.joinPointsPrice = joinPointsPrice;
        this.joinMoneyPrice = joinMoneyPrice;
        this.interestRate = interestRate;
        this.banner = banner;
        this.bank = bank;
        this.tag = tag;
        this.members = members;
        this.createdOn = createdOn;
        this.currencies = new ArrayList<>();
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
