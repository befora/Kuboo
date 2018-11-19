package com.sethchhim.kuboo_client.ui.log

import android.os.Bundle

class LogActivity : LogActivityImpl1_Content() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUi()
        initListeners()
        populateContent()
    }

    private fun initListeners() {
        checkBoxUi.setOnCheckedChangeListener { _, _ -> updateContent() }
        checkBoxLocal.setOnCheckedChangeListener { _, _ -> updateContent() }
        checkBoxNetwork.setOnCheckedChangeListener { _, _ -> updateContent() }
        checkBoxError.setOnCheckedChangeListener { _, _ -> updateContent() }
    }


}


