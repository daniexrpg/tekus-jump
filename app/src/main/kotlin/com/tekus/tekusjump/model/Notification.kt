package com.tekus.tekusjump.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Jose Daniel on 21/01/2017.
 */
class Notification {

    @SerializedName("NotificationId")
    var id: Int = 0

    @SerializedName("Date")
    var date: String = ""

    @SerializedName("Duration")
    var duration: Int = 0

    fun Notification(id: Int, date: String, duration: Int) {
        this.id = id
        this.date = date
        this.duration = duration
    }
}