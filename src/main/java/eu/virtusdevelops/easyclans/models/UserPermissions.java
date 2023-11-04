package eu.virtusdevelops.easyclans.models;

public enum UserPermissions {
    ACCEPT_REQUEST(""),
    CLAN_SETTINGS(""),
    VIEW_MEMBERS(""),
    EDIT_MEMBER(""),
    EDIT_MEMBER_PERMISSIONS(""),
    KICK_MEMBER(""),
    VIEW_REQUESTS(""),
    VIEW_INVITES(""),
    VIEW_BANK(""),
    VIEW_CLAN_INFO(""),
    BANK_DEPOSIT(""),
    BANK_WITHDRAW(""),
    EDIT_CLAN_NAME(""),
    EDIT_CLAN_DISPLAY_NAME(""),
    EDIT_CLAN_TAG(""),
    EDIT_INACTIVE_KICK(""),
    EDIT_JOIN_PRICE(""),
    CHANGE_BANNER(""),
    TOGGLE_PVP("")

    ;

    private String description;

    UserPermissions(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
