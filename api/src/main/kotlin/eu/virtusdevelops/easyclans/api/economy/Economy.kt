package eu.virtusdevelops.easyclans.api.economy

interface Economy<T> {

    val name: String

    fun get(): T

    fun set(value: T)

    fun reset()

    fun provider(): EconomyProvider<T>?

    fun serialize(): String

}