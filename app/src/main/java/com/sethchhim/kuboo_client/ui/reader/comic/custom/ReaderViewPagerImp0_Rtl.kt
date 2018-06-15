package com.sethchhim.kuboo_client.ui.reader.comic.custom

import android.content.Context
import android.util.AttributeSet
import com.booking.rtlviewpager.RtlViewPager
import com.sethchhim.kuboo_client.Settings

open class ReaderViewPagerImp0_Rtl(context: Context?, attrs: AttributeSet?) : RtlViewPager(context, attrs) {

    override fun isRtl() = Settings.RTL

}