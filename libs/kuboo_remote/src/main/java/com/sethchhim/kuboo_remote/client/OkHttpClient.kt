package com.sethchhim.kuboo_remote.client

import android.content.Context
import com.sethchhim.kuboo_remote.util.Settings.CACHE_SIZE
import com.sethchhim.kuboo_remote.util.Settings.CONNECTION_TIMEOUT
import com.sethchhim.kuboo_remote.util.Settings.READ_TIMEOUT
import com.simplemented.okdelay.DelayInterceptor
import okhttp3.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class OkHttpClient(private val context: Context) : OkHttpClient() {

    internal val sslHandshakeInterceptor = SSLHandshakeInterceptor()

    private val isHttpLoggingEnabled = false
    private val isSimulateLag = false

    override fun cache(): Cache {
        val cacheFile = File("${context.cacheDir}okhttp_cache")
        if (!cacheFile.exists()) cacheFile.mkdir()
        return Cache(cacheFile, (CACHE_SIZE * 1024 * 1024).toLong())
    }

    override fun cookieJar() = cookieJar

    override fun connectTimeoutMillis() = CONNECTION_TIMEOUT

    override fun hostnameVerifier() = HostnameVerifier { _, _ -> true }

    override fun interceptors(): MutableList<Interceptor> {
        val interceptorList = mutableListOf<Interceptor>()
        interceptorList.add(sslHandshakeInterceptor)

        if (isHttpLoggingEnabled) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            interceptorList.add(httpLoggingInterceptor)
        }

        if (isSimulateLag) {
            val delayInterceptor = DelayInterceptor(2000L, TimeUnit.MILLISECONDS)
            interceptorList.add(delayInterceptor)
        }

        return interceptorList
    }

    override fun protocols() = mutableListOf(Protocol.HTTP_1_1)

    override fun readTimeoutMillis() = READ_TIMEOUT

    override fun sslSocketFactory(): SSLSocketFactory {
        try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustManagerArray, java.security.SecureRandom())
            return sslContext.socketFactory
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return super.sslSocketFactory()
    }

    private val cookieJar: CookieJar
        get() = object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
//                Timber.i("[AUTHENTICATE] Saving cookie: $url ${cookies.size}")
                cookieStore[url] = cookies
            }

            override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                cookieStore.forEach {
                    if (url.toString().contains(it.key.toString())) {
//                        Timber.i("[AUTHENTICATE] Loading domain cookie: ${it.key} ${it.value.size}")
                        return it.value
                    }
                }

                val cookies = cookieStore[url]
//                Timber.i("[AUTHENTICATE] Loading cookie: $url ${cookies?.size}")
                return cookies ?: ArrayList()
            }
        }

    private val cookieStore = HashMap<HttpUrl, MutableList<Cookie>>()

    private val trustManagerArray: Array<TrustManager>
        get() = arrayOf(x509TrustManager)

    private val x509TrustManager: X509TrustManager
        get() = object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {

            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }
        }

}





