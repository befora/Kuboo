package com.sethchhim.kuboo_remote.util

import android.util.Base64
import com.sethchhim.kuboo_remote.model.Login

object Authentication {

    internal fun getAuthorizationHeaderName() = "Authorization"

    internal fun getAuthorizationHeaderValue(login: Login) = "Basic " + Base64.encodeToString(("${login.username}:${login.password}").toByteArray(), Base64.NO_WRAP)

}