package net.astrona.easyclans.models;

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
 *
*/

public record Clan(int id, String name, String displayName) {
}
