package com.sethchhim.kuboo_remote.task

import com.sethchhim.kuboo_remote.client.OkHttpHelper

class Task_RemoteTlsCipherSuite(val okHttpHelper: OkHttpHelper) {

    fun getTlsCipherSuite() = okHttpHelper.getTlsCipherSuite()

}