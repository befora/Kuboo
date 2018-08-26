package com.sethchhim.kuboo_client.util

import com.sethchhim.kuboo_client.Extensions.isEven
import com.sethchhim.kuboo_client.data.AppDatabaseDao
import com.sethchhim.kuboo_client.data.enum.LogType
import com.sethchhim.kuboo_client.data.model.Log

class LogUtil(private val appExecutors: AppExecutors, private val appDatabaseDao: AppDatabaseDao) {

    internal fun local(init: Log.() -> Unit) = Log().apply {
        logType = LogType.LOCAL.value
        init()
        addToDatabase(this)
    }

    internal fun remote(init: Log.() -> Unit) = Log().apply {
        logType = LogType.REMOTE.value
        init()
        addToDatabase(this)
    }

    internal fun ui(init: Log.() -> Unit) = Log().apply {
        logType = LogType.UI.value
        init()
        addToDatabase(this)
    }

    internal fun addMockLogData() {
        for (index in 0..1000) {
            ui { message = "Ui event is triggered." }
            local { message = "Local database was written." }
            remote { message = "Remote action was triggered." }
            if (index > 990 && index.isEven()) remote {
                message = "Remote action failed!"
                isError = true
            }
        }
    }

    private fun addToDatabase(log: Log) = appExecutors.diskIO.execute {
        appDatabaseDao.insertLog(log)
        appDatabaseDao.deleteLogAtLimit()
    }
}