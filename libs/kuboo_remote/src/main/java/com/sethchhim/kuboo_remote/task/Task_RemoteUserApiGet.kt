package com.sethchhim.kuboo_remote.task

import androidx.lifecycle.MutableLiveData
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import okhttp3.Call
import okhttp3.ResponseBody
import org.json.JSONObject
import timber.log.Timber

class Task_RemoteUserApiGet(kubooRemote: KubooRemote, login: Login, book: Book) : Task_RemoteUserApiBase(kubooRemote, login, book) {

    internal val liveData = MutableLiveData<Book>()
    private val startTime = System.currentTimeMillis()

    init {
        kubooRemote.networkIO.execute {
            try {
                val call = okHttpHelper.getCall(login, stringUrl, javaClass.simpleName)
                val response = call.execute()
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    handleResult(responseBody)
                } else {
                    when (response.code()) {
                        401 -> handleAuthentication(call)
                        else -> {
                            Timber.e("code[${response.code()}] message[${response.message()}] title[${book.title}] stringUrl[$stringUrl]")
                            kubooRemote.mainThread.execute { liveData.value = null }
                        }
                    }
                }
                response.close()
            } catch (e: Exception) {
                Timber.e("message[$e] title[${book.title}] stringUrl[$stringUrl]")
                kubooRemote.mainThread.execute { liveData.value = null }
            }
        }
    }

    private fun handleResult(responseBody: ResponseBody) {
        val result = responseBody.string()
        if (result.isEmpty()) {
            Timber.d("UserApiData has no data: title[${book.title}] stringUrl[$stringUrl]")
            kubooRemote.mainThread.execute { liveData.value = null }
        } else {
            val jsonObject = JSONObject(result)

            val mark = jsonObject.getMark()
            if (mark[0].isNotEmpty()) book.currentPage = mark[0].toInt()

            val isFinished = jsonObject.getIsFinished()
            book.isFinished = isFinished

            val elapsedTime = System.currentTimeMillis() - startTime
            Timber.d("UserApi get is successful: title[${book.title}] savedPage[${book.currentPage} of ${book.totalPages}] savedPosition[] isFinished[${book.isFinished}] stringUrl[$stringUrl] time[$elapsedTime milliseconds]")
            kubooRemote.mainThread.execute { liveData.value = book }
        }
    }

    private fun handleAuthentication(call: Call) = kubooRemote.mainThread.execute {
        Task_Authenticate(kubooRemote, login).liveData.observeForever { result ->
            if (result == true) {
                val secondCall = call.clone()
                secondCall.retry()
            }
        }
    }

    private fun Call.retry() = kubooRemote.networkIO.execute {
        try {
            val secondResponse = execute()
            val secondResponseBody = secondResponse.body()
            if (secondResponse.isSuccessful && secondResponseBody != null) {
                handleResult(secondResponseBody)
            } else {
                Timber.e("code[${secondResponse.code()}] message[${secondResponse.message()}] title[${book.title}] stringUrl[$stringUrl] secondAttempt[true]")
                kubooRemote.mainThread.execute { liveData.value = null }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e("message[$e] secondAttempt[true]")
        }
    }

    /**
     * The following line is an example of the remote bookmark api for formatting purposes:
     * "mark" : "15#0.0"
     *
     * Note1: First value before the # symbol is page number, Second value after the # symbol is scroll position.
     * Note2: If page position is not saved, the # symbol will be missing.
     */
    private fun JSONObject.getMark(): List<String> {
        val bookmarkPage: String
        val bookmarkScrollPosition: String

        val markResult = getString("mark")
        when (markResult.contains("#")) {
            true -> {
                val resultPoundPosition = markResult.indexOf("#")
                val resultPageNumber = markResult.substring(0, resultPoundPosition)
                val resultPagePosition = markResult.substring(resultPoundPosition + 1, markResult.lastIndex)

                bookmarkPage = resultPageNumber
                bookmarkScrollPosition = resultPagePosition
            }
            false -> {
                when (markResult == "null") {
                    true -> {
                        bookmarkPage = "0"
                        Timber.e("UserApi bookmark page return literal string null! There was probably an error while saving.")
                    }
                    false -> bookmarkPage = markResult
                }
                bookmarkScrollPosition = "0.0"
            }
        }
        return listOf(bookmarkPage, bookmarkScrollPosition)
    }

    /**
     * The following line is an example of the remote isFinish api for formatting purposes:
     *  "isFinished" : false
     */
    private fun JSONObject.getIsFinished() = getBoolean("isFinished")

}