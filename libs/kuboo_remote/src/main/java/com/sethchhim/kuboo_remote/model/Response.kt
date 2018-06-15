package com.sethchhim.kuboo_remote.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class Response(val code: Int, val message: String, val isSuccessful: Boolean) : Parcelable