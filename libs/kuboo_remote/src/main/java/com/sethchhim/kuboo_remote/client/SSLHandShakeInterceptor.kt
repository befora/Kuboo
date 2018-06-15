package com.sethchhim.kuboo_remote.client

import okhttp3.Interceptor
import okhttp3.Response

class SSLHandshakeInterceptor : Interceptor {

    var tlsCipherSuite = ""

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        setTlsAndCipherSuiteInfo(response)
        return response
    }

    private fun setTlsAndCipherSuiteInfo(response: Response?) {
        if (response != null) {
            val handshake = response.handshake()
            if (handshake != null) {
                tlsCipherSuite = " ${handshake.tlsVersion()}\n ${handshake.cipherSuite()}\n"
                tlsCipherSuite = tlsCipherSuite.replace("_", " ")
            } else {
                tlsCipherSuite = ""
            }
        }
    }

}