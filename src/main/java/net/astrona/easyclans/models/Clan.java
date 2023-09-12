package net.astrona.easyclans.models;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * TODO:
 * bank (money)
 * owner (multiple owners? or is it transfer?)
 * auto money distribute (to members every week or set time...),
 * members (well its list of members and their last active times blabla)
 * rewards (like clan rewards for being best clan and so on)?
 * auto kick (kick members who haven't been active for some time...)
 * join price (price for member to join the clan)
 * clan symbol (character .... also changable)
 * customizable flag (that can be purchased to be changed, like an actuall flag)
 */

public class Clan {
    private int id;
    private CPlayer owner;
    private String name;
    private String displayName;
    private int autoKickTime;
    private int joinPointsPrice;
    private int joinMoneyPrice;
    private int autoPayOutTime;
    private double autoPayOutPercentage;
    private ItemStack banner;
    private double bank;
    private String tag;
    private List<CPlayer> members;
    private boolean active;

    public Clan(int id, CPlayer owner, String name, String displayName, int autoKickTime, int joinPointsPrice,
                int joinMoneyPrice, int autoPayOutTime, double autoPayOutPercentage, ItemStack banner, double bank,
                String tag, List<CPlayer> members, boolean active) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.displayName = displayName;
        this.autoKickTime = autoKickTime;
        this.joinPointsPrice = joinPointsPrice;
        this.joinMoneyPrice = joinMoneyPrice;
        this.autoPayOutTime = autoPayOutTime;
        this.autoPayOutPercentage = autoPayOutPercentage;
        this.banner = banner;
        this.bank = bank;
        this.tag = tag;
        this.members = members;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CPlayer getOwner() {
        return owner;
    }

    public void setOwner(CPlayer owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public int getJoinMoneyPrice() {
        return joinMoneyPrice;
    }

    public void setJoinMoneyPrice(int joinMoneyPrice) {
        this.joinMoneyPrice = joinMoneyPrice;
    }

    public int getAutoPayOutTime() {
        return autoPayOutTime;
    }

    public void setAutoPayOutTime(int autoPayOutTime) {
        this.autoPayOutTime = autoPayOutTime;
    }

    public double getAutoPayOutPercentage() {
        return autoPayOutPercentage;
    }

    public void setAutoPayOutPercentage(double autoPayOutPercentage) {
        this.autoPayOutPercentage = autoPayOutPercentage;
    }

    public ItemStack getBanner() {
        return banner;
    }

    public void setBanner(ItemStack banner) {
        this.banner = banner;
    }

    public double getBank() {
        return bank;
    }

    public void setBank(double bank) {
        this.bank = bank;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<CPlayer> getMembers() {
        return members;
    }

    public void setMembers(List<CPlayer> members) {
        this.members = members;
    }
}
