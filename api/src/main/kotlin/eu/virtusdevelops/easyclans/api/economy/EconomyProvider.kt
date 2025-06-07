package eu.virtusdevelops.easyclans.api.economy

import eu.virtusdevelops.easyclans.api.player.ClanPlayer

interface EconomyProvider<T> {

    /**
     * Retrieves the name of the economy provider.
     *
     * @return The name of the economy provider as a string.
     */
    fun name(): String

    /**
     * Adds the specified amount to the economy of the given clan player.
     *
     * @param amount The amount to be added.
     * @param clanPlayer The instance of the clan player for whom the amount is to be added.
     * @return True if the amount was successfully added, false otherwise.
     */
    fun add(amount: T, clanPlayer: ClanPlayer): Boolean

    /**
     * Removes the specified amount from the economy of the given clan player.
     *
     * @param amount The amount to be removed.
     * @param clanPlayer The instance of the clan player for whom the amount is to be removed.
     * @return True if the amount was successfully removed, false otherwise.
     */
    fun remove(amount: T, clanPlayer: ClanPlayer): Boolean


    /**
     * Sets the specified amount in the economy of the given clan player.
     *
     * @param amount The amount to be set.
     * @param clanPlayer The instance of the clan player for whom the amount is to be set.
     */
    fun set(amount: T, clanPlayer: ClanPlayer)

    /**
     * Retrieves the current economy value associated with the specified clan player.
     *
     * @param clanPlayer The instance of the clan player whose economy value is to be retrieved.
     * @return The current economy value of the specified clan player.
     */
    fun get(clanPlayer: ClanPlayer): T


    /**
     * Checks if the specified clan player has at least the given amount in their economy.
     *
     * @param amount The amount to check for.
     * @param clanPlayer The instance of the clan player whose economy is to be checked.
     * @return True if the clan player has at least the specified amount, false otherwise.
     */
    fun has(amount: T, clanPlayer: ClanPlayer): Boolean
}