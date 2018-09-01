package com.sethchhim.kuboo_client.data.task.download

import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase

class Task_DownloadGetLiveData : Task_LocalBase() {

    internal val liveData = appDatabaseDao.getAllBookDownloadLiveData()

}