package com.sethchhim.kuboo_client.data.task.base

import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.data.AppDatabaseDao
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.util.FileUtil
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.util.AppExecutors
import javax.inject.Inject

open class Task_LocalBase {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var appDatabaseDao: AppDatabaseDao
    @Inject lateinit var executors: AppExecutors
    @Inject lateinit var fileUtil: FileUtil
    @Inject lateinit var systemUtil: SystemUtil
    @Inject lateinit var viewModel: ViewModel

}