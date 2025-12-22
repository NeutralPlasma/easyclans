package eu.virtusdevelops.easyclans.api.notification

import eu.virtusdevelops.easyclans.api.Success
import java.util.Date
import java.util.concurrent.CompletionStage

interface Notification {
    val message: String
    val sentDate: Date
    val readDate: Date?

    val read: Boolean


    suspend fun markAsReadAsync(): Result<Success>
    fun markAsRead(): CompletionStage<Result<Success>>
}