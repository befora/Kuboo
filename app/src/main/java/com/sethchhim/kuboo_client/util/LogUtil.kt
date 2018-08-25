package com.sethchhim.kuboo_client.util

import com.sethchhim.kuboo_client.data.AppDatabaseDao
import com.sethchhim.kuboo_client.data.enum.LogType
import com.sethchhim.kuboo_client.data.model.Log

class LogUtil(private val appExecutors: AppExecutors, private val appDatabaseDao: AppDatabaseDao) {

    internal fun local(init: Log.() -> Unit) = Log().apply {
        logType = LogType.LOCAL.value
        init()
        appExecutors.diskIO.execute { appDatabaseDao.insertLog(this) }
    }

    internal fun network(init: Log.() -> Unit) = Log().apply {
        logType = LogType.NETWORK.value
        init()
        appExecutors.diskIO.execute { appDatabaseDao.insertLog(this) }
    }

    internal fun ui(init: Log.() -> Unit) = Log().apply {
        logType = LogType.UI.value
        init()
        appExecutors.diskIO.execute { appDatabaseDao.insertLog(this) }
    }

}