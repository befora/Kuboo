package com.sethchhim.kuboo_remote.client

import android.util.Base64
import com.sethchhim.kuboo_remote.model.Login
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class BasicAuthInterceptor(login: Login) : Interceptor {

    private val credentials: String = Credentials.basic(login.username, login.password)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val base = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        val authenticatedRequest = request.newBuilder()
                .header("Authorization", base).build()
        return chain.proceed(authenticatedRequest)
    }

}