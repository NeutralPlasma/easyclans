package eu.virtusdevelops.easyclans.api.economy

interface EconomyController {


    fun registerProvider(provider: EconomyProvider<Any>)

    fun unregisterProvider(provider: EconomyProvider<Any>)

    fun <T> getProvider(name: String, type: Class<T>): EconomyProvider<T>?

    fun providers(): Set<EconomyProvider<Any>>
}