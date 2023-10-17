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
    VIEW_CLAN_INFO("")

    ;

    private String description;

    UserPermissions(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
