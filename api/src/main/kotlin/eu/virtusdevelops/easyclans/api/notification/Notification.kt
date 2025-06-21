package eu.virtusdevelops.easyclans.api.notification

import java.util.Date

interface Notification {
    fun message(): String
    fun date(): Date
}