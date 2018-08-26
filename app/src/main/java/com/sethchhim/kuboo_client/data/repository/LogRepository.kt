package com.sethchhim.kuboo_client.data.repository

import com.sethchhim.kuboo_client.data.task.log.Task_LogGetAll

class LogRepository {

    internal fun getLogList() = Task_LogGetAll().liveData

}