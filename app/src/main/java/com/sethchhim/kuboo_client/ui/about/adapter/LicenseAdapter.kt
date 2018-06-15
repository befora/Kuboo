package com.sethchhim.kuboo_client.ui.about.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.model.License
import com.sethchhim.kuboo_client.util.DialogUtil
import org.jetbrains.anko.sdk25.coroutines.onClick
import javax.inject.Inject

class LicenseAdapter(val context: Context, val licenseList: List<License>) : BaseQuickAdapter<License, LicenseAdapter.LicenseHolder>(R.layout.about_item_license, licenseList) {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var dialogUtil: DialogUtil

    override fun convert(helper: LicenseHolder, item: License) {
        helper.library.text = item.name
        helper.license.onClick { showLicenseDialog(item) }
    }

    inner class LicenseHolder(view: View) : BaseViewHolder(view) {
        val library = view.findViewById<TextView>(R.id.about_item_license_textView0)!!
        val license = view.findViewById<TextView>(R.id.about_item_license_textView1)!!
    }

    private fun showLicenseDialog(item: License) = dialogUtil.getDialogLicense(context, item.license).apply {
        show()
        findViewById<TextView>(android.R.id.message)?.textSize = 9F
    }
}


