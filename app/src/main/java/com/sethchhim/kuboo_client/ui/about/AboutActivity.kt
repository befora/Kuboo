package com.sethchhim.kuboo_client.ui.about

import android.os.Bundle
import android.support.v4.view.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.about.adapter.AboutPagerAdapter
import com.sethchhim.kuboo_client.ui.base.BaseActivity
import me.relex.circleindicator.CircleIndicator

class AboutActivity : BaseActivity() {

    @BindView(R.id.about_content_viewPager) lateinit var aboutViewPager: ViewPager
    @BindView(R.id.about_content_circleIndicator) lateinit var aboutViewPagerIndicator: CircleIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forceOrientationSetting()
        setFullScreen()
        setContentView(R.layout.about_content)
        ButterKnife.bind(this)

        aboutViewPager.adapter = AboutPagerAdapter(this)
        aboutViewPagerIndicator.setViewPager(aboutViewPager)
    }

}