package com.sethchhim.kuboo_client.ui.log

import android.os.Bundle
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange

class LogActivity : LogActivityImpl1_Content() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUi()
        initListeners()
        populateContent()
    }

    private fun initListeners() {
        checkBoxUi.onCheckedChange { _, _ -> updateContent() }
        checkBoxLocal.onCheckedChange { _, _ -> updateContent() }
        checkBoxNetwork.onCheckedChange { _, _ -> updateContent() }
        checkBoxError.onCheckedChange { _, _ -> updateContent() }
    }


}


