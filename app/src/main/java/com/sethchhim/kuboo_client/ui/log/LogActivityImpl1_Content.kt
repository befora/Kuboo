package com.sethchhim.kuboo_client.ui.log

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.sethchhim.kuboo_client.data.enum.LogType
import com.sethchhim.kuboo_client.data.model.Log
import com.sethchhim.kuboo_client.ui.log.adapter.LogAdapter

@SuppressLint("Registered")
open class LogActivityImpl1_Content : LogActivityImpl0_View() {

    private val logList = mutableListOf<Log>()

    protected fun populateContent() {
        viewModel.getLogList().observe(this, Observer { result ->
            result?.let {
                logList.addAll(it)
                recyclerView.adapter = LogAdapter(logList)
                recyclerView.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
                textView.setContent(logList)
            }
        })
    }

    fun updateContent() {
        logList.apply {
            val uiResult = filter { it.logType == LogType.UI.value }
            val localResult = filter { it.logType == LogType.LOCAL.value }
            val networkResult = filter { it.logType == LogType.REMOTE.value }

            val filteredList = mutableListOf<Log>()
                    .apply {
                        if (checkBoxUi.isChecked) addAll(uiResult)
                        if (checkBoxLocal.isChecked) addAll(localResult)
                        if (checkBoxNetwork.isChecked) addAll(networkResult)
                    }
                    .filter { if (checkBoxError.isChecked) it.isError else true }
                    .sortedBy { it.autoId }
            (recyclerView.adapter as LogAdapter).setNewData(filteredList)
            recyclerView.scrollToPosition(filteredList.size - 1)
            textView.setContent(filteredList)
        }
    }

    private fun TextView.setContent(logList: List<Log>) {
        val errorCount = logList.filter { it.isError }.size
        text = "Showing ${logList.size} items with $errorCount errors!"
    }

}
