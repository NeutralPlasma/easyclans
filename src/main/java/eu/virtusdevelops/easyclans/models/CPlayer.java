package eu.virtusdevelops.easyclans.models;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CPlayer {
    private String name, rank;
    private UUID uuid;
    private int clanId;
    private long lastActive, joinClanDate;
    private boolean isActive;
    private boolean inClubChat = false;
    private List<UserPermissions> userPermissionsList;
    private List<Notification> unreadNotifications;

    public CPlayer(UUID uuid, int clanId, long lastActive,
                   long joinClanDate, String name, String rank) {
        this.uuid = uuid;
        this.clanId = clanId;
        this.lastActive = lastActive;
        this.joinClanDate = joinClanDate;
        this.name = name;
        this.rank = rank;
        this.isActive = false;
        this.unreadNotifications = new ArrayList<>();
        userPermissionsList = new ArrayList<>();
    }

    public CPlayer(UUID uuid, int clanId, long lastActive, long joinClanDate,
                   String name, String rank, List<UserPermissions> permissions) {
        this.uuid = uuid;
        this.clanId = clanId;
        this.lastActive = lastActive;
        this.joinClanDate = joinClanDate;
        this.name = name;
        this.rank = rank;
        this.isActive = false;
        this.unreadNotifications = new ArrayList<>();
        userPermissionsList = permissions;
    }

    public boolean isInClubChat() {
        return inClubChat;
    }

    public void setInClubChat(boolean inClubChat) {
        this.inClubChat = inClubChat;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getClanID() {
        return clanId;
    }

    public void setClanID(int clan_id) {
        this.clanId = clan_id;
        if(clan_id == -1){
            inClubChat = false;
        }
    }

    public void removeFromClan() {
        this.userPermissionsList.clear();
        this.clanId = -1;
    }

    public long getLastActive() {
        return lastActive;
    }

    public void setLastActive(long lastActive) {
        this.lastActive = lastActive;
    }

    public long getJoinClanDate() {
        return joinClanDate;
    }

    public void setJoinClanDate(long joinClanDate) {
        this.joinClanDate = joinClanDate;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void addPermission(UserPermissions permission){
        this.userPermissionsList.add(permission);
    }
    public void removePermission(UserPermissions permission){
        this.userPermissionsList.remove(permission);
    }

    public void setUserPermissionsList(List<UserPermissions> userPermissionsList) {
        this.userPermissionsList = userPermissionsList;
    }

    public List<UserPermissions> getUserPermissionsList() {
        return userPermissionsList;
    }

    public boolean hasPermission(UserPermissions permission){
        return userPermissionsList.contains(permission); // TODO!
    }


    public List<Notification> getUnreadNotifications() {
        return unreadNotifications;
    }

    public void setUnreadNotifications(List<Notification> unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }

    public void addNotification(Notification notification){
        this.unreadNotifications.add(notification);
    }
}
