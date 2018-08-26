package com.sethchhim.kuboo_client.data.model

import android.annotation.SuppressLint
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.sethchhim.kuboo_client.data.enum.LogType
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
@SuppressLint("ParcelCreator")
data class Log(
        @PrimaryKey(autoGenerate = true) var autoId: Int = 0,
        var currentTimeMilliseconds: Long = System.currentTimeMillis(),
        var logType: Int = LogType.UNKNOWN.value,
        var message: String = "",
        var isError: Boolean = false,
        @Ignore var ignored: String? = null) : Parcelable {

    internal fun getLogTypeItem() = when (logType) {
        1 -> LogType.UI
        2 -> LogType.LOCAL
        3 -> LogType.REMOTE
        else -> LogType.UNKNOWN
    }

    fun getCurrentTimeString() = currentTimeMilliseconds.toString()

    fun getLogTypeString() = when (logType) {
        1 -> "UI"
        2 -> "LOCAL"
        3 -> "NETWORK"
        else -> "UNKNOWN"
    }

}