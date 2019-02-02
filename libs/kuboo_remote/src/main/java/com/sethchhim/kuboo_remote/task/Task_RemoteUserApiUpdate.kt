package com.sethchhim.kuboo_remote.task

import androidx.lifecycle.MutableLiveData
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import timber.log.Timber

class Task_RemoteUserApiUpdate(kubooRemote: KubooRemote, login: Login, list: List<Book>) {

    internal val liveData = MutableLiveData<Boolean>()

    private val listSize = list.size
    private var resultSize = 0

    init {
        kubooRemote.mainThread.execute {
            list.forEach { book ->
                Task_RemoteUserApiPut(kubooRemote, login, book).liveData.observeForever {
                    resultSize += 1
                    Timber.d("size: $resultSize")
                    if (resultSize == listSize) {
                        Timber.d("UserApi successfully updated list: size[$listSize]")
                        liveData.value = true
                    }
                }
            }
        }
    }
}