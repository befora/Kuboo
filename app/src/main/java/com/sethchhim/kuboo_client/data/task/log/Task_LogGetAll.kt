package com.sethchhim.kuboo_client.data.task.log

import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase

class Task_LogGetAll : Task_LocalBase() {

    internal val liveData = appDatabaseDao.getAllLog()

}