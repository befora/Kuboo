package com.sethchhim.kuboo_client.ui.main.downloads.custom

import android.content.Context
import android.support.design.widget.TabLayout
import android.util.AttributeSet
import com.sethchhim.kuboo_client.R

class DownloadsTabLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : TabLayout(context, attrs, defStyleAttr) {

    init {
        addTab(newTab(), 0)
        addTab(newTab(), 1)
        getTabAt(0)?.text = context.getString(R.string.downloads_pause_all)
        getTabAt(1)?.text = context.getString(R.string.downloads_resume_all)
        getTabAt(0)?.setIcon(R.drawable.ic_pause_white_24dp)
        getTabAt(1)?.setIcon(R.drawable.ic_play_arrow_white_24dp)
    }

}