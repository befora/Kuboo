package com.sethchhim.kuboo_remote.task

import com.sethchhim.kuboo_remote.client.OkHttpHelper

class Task_RemoteIsConnectionEncrypted(val okHttpHelper: OkHttpHelper) {

    fun isConnectionEncrypted() = okHttpHelper.getTlsCipherSuite().isNotEmpty()

}