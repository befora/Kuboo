package com.sethchhim.kuboo_remote.model

import android.annotation.SuppressLint
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
@Entity(tableName = "Login")
@SuppressLint("ParcelCreator")
data class Login(
        @PrimaryKey(autoGenerate = true) var autoId: Int = 0,
        var id: Int = 0,
        var nickname: String = "",
        var server: String = "",
        var username: String = "",
        var password: String = "",
        var timeAccessed: Int = 0,
        @Ignore var ignored: String? = null) : Serializable, Parcelable {

    fun isEmpty(): Boolean {
        return server.isEmpty()
    }

    fun setTimeAccessed() {
        timeAccessed = (Date().time / 1000).toInt()
    }

}