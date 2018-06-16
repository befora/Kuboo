package com.sethchhim.kuboo_remote.task

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.util.Settings.isDebugOkHttp
import okhttp3.FormBody
import okhttp3.Response
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
import org.jsoup.Jsoup
import timber.log.Timber

class Task_Authenticate(val kubooRemote: KubooRemote, login: Login) {

    private val okHttpHelper = kubooRemote.okHttpHelper

    internal val liveData = MutableLiveData<Boolean>()

    init {
        kubooRemote.networkIO.execute {
            val stringUrl = getLoginPage(login)
            try {
                val loginCall = okHttpHelper.getCall(login, stringUrl, javaClass.simpleName)
                val response = loginCall.execute()
                val inputStream = response?.body()?.byteStream()
                val inputAsString = inputStream?.bufferedReader().use { it?.readText() }
                val doc = Jsoup.parse(inputAsString)
                val serverSalt = doc.select("#serversalt").`val`()
                val serverTime = doc.select("#servertime").`val`()
                val isSaltValid = serverSalt != null && serverSalt.isNotEmpty()
                if (isSaltValid) {
                    val hashPassword = login.password.encode(serverSalt).encode(serverTime)

                    if (isDebugOkHttp) {
                        Timber.d("Server salt found: $serverSalt")
                        Timber.d("Server time found: $serverTime")
                        Timber.d("Hash password generated: $hashPassword")
                    }

                    val hashResponse = postHash(login, serverTime, hashPassword)
                    if (hashResponse != null) {
                        val isSuccess = hashResponse.isSuccessful
                        if (isSuccess) {
                            kubooRemote.mainThread.execute { liveData.value = true }
                            if (isDebugOkHttp) {
                                val hashInputStream = hashResponse.body()?.byteStream()
                                val hashInputAsString = hashInputStream?.bufferedReader().use { it?.readText() }
                                Timber.d("Authentication successful: $hashInputAsString $stringUrl")
                            }
                        } else {
                            kubooRemote.mainThread.execute { liveData.value = false }
                            if (isDebugOkHttp) {
                                val reason = "${response.code()} ${response.message()}"
                                Timber.e("Authentication failed! $reason $stringUrl")
                            }
                        }
                    }
                }
                inputStream?.close()
                response.close()
            } catch (e: Exception) {
                Timber.e("message[${e.message}] url[$stringUrl]")
                kubooRemote.mainThread.execute { liveData.value = false }
            }
        }
    }

    private fun postHash(login: Login, serverTime: String, hashPassword: String): Response? {
        val stringUrl = getLoginPage(login)

        val formBody = FormBody.Builder()
                .add("servertime", serverTime)
                .add("login", login.username)
                .addEncoded("hash", hashPassword)
                .build()

        val call = okHttpHelper.postCall(login, stringUrl, formBody)
        return call.execute()
    }

    private fun getLoginPage(login: Login): String =
            login.server.replace("/opds-comics/", "").replace("/opds-books/", "")

    private fun String.encode(value: String): String = String(Hex.encodeHex(HmacUtils(HmacAlgorithms.HMAC_SHA_256, this).hmac(value)))

}


