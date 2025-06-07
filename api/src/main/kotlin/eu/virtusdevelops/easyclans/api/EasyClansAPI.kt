package eu.virtusdevelops.easyclans.api

interface EasyClansAPI {

    companion object {
        private var implementation: EasyClansAPI? = null
        private var enabled = false


        fun get(): EasyClansAPI {
            if(!enabled || implementation == null) {
                throw IllegalStateException("EasyClansAPI is not enabled")
            }
            return implementation!!
        }

    }

}