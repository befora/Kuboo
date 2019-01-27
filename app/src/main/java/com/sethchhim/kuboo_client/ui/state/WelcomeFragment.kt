package com.sethchhim.kuboo_client.ui.state

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings.UBOOQUITY_VERSION
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.ui.main.MainActivity
import com.sethchhim.kuboo_client.util.SystemUtil
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.home_layout_welcome.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class WelcomeFragment : DaggerFragment() {

    @Inject lateinit var mainActivity: MainActivity
    @Inject lateinit var systemUtil: SystemUtil
    @Inject lateinit var viewModel: ViewModel

    @BindView(R.id.home_layout_welcome_floatingActionButton) lateinit var fab: FloatingActionButton

    val ABOUT_INFO = "• Powered by the love of reading. " +
            "\n \n• Free, No Ads, No Special Permissions." +
            "\n \n• Designed and tested for Ubooquity " + UBOOQUITY_VERSION + "." +
            "\n \n• Feedback and bug reports are welcome."
    val ABOUT_DISCLAIMER = ("\"Viewer+ for Ubooquity\" is an independent third party application not affiliated with Ubooquity in any manner.\n" +
            "\n\"Viewer+ for Ubooquity\" provides unrestricted access to the internet and is not responsible for the availability or content of these external sources." +
            "\n \n")
    val ABOUT_GOOGLE_DISCLAIMER = "• Google Play and the Google Play logo are trademarks of Google Inc."

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home_layout_welcome, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener { onClickedFab() }

        val isPortrait = systemUtil.isOrientationPortrait()
        val width = systemUtil.getSystemWidth()

        val stringAppVersion = "App Version: " + systemUtil.getVersionName()
        view.home_layout_welcome_textView1.text = stringAppVersion
        val versionOffset = if (isPortrait) 50 else 70
        view.home_layout_welcome_textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((width / versionOffset).toFloat()))

        val dateFormat = SimpleDateFormat("MMM dd yyyy HH:mm:ss")
        val stringBuildDate = "Build Date: " + dateFormat.format(Date())
        val buildDateOffset = if (isPortrait) 50 else 70
        view.home_layout_welcome_textView2.text = stringBuildDate
        view.home_layout_welcome_textView2.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((width / buildDateOffset).toFloat()))

        view.home_layout_welcome_textView3.text = ABOUT_INFO
        val infoOffset = if (isPortrait) 25 else 45
        view.home_layout_welcome_textView3.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((width / infoOffset).toFloat()))

        view.home_layout_welcome_textView4.text = ABOUT_DISCLAIMER
        val disclaimerOffset = if (isPortrait) 25 else 45
        view.home_layout_welcome_textView4.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((width / disclaimerOffset).toFloat()))
    }

    private fun onClickedFab() {
        when (viewModel.isLoginListEmpty()) {
            true -> mainActivity.showFragmentLoginEdit(login = null)
            false -> mainActivity.showFragmentLoginBrowser()
        }
    }

}