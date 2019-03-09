package com.sethchhim.kuboo_client.ui.about.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sethchhim.kuboo_client.*
import com.sethchhim.kuboo_client.Settings.UBOOQUITY_VERSION
import com.sethchhim.kuboo_client.data.model.License
import com.sethchhim.kuboo_client.util.SystemUtil
import org.jetbrains.anko.layoutInflater
import java.text.SimpleDateFormat
import javax.inject.Inject

class AboutPagerAdapter(val context: Context) : androidx.viewpager.widget.PagerAdapter() {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var systemUtil: SystemUtil

    private val faqAdapter by lazy { FaqAdapter(context) }
    private val licenses by lazy { Licenses() }

    var width = systemUtil.getSystemWidth()

    init {
        if (systemUtil.isOrientationPortrait()) {
            if (width >= 1280) width = 1280
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int) = when (position) {
        0 -> getPage0(container)
        1 -> getPage1(container)
        2 -> getPage2(container)
        3 -> getPage3(container)
        4 -> getPage4(container)
        else -> View(context)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as View)

    override fun getCount() = 5

    override fun isViewFromObject(view: View, `object`: Any) = view === `object`

    private fun getPage0(container: ViewGroup): View {
        val itemView = context.layoutInflater.inflate(R.layout.about_info, container, false)
        val textVersion = itemView.findViewById<TextView>(R.id.textAboutVersion)!!
        val mTextVersion = "App Version: " + systemUtil.getVersionName()
        textVersion.text = mTextVersion
        if (systemUtil.isOrientationPortrait()) {
            textVersion.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 50).toFloat())
        } else {
            textVersion.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 70).toFloat())
        }

        val textBuildDate = itemView.findViewById<TextView>(R.id.textAboutBuildDate)!!
        val dateFormat = SimpleDateFormat("MMM dd yyyy HH:mm:ss")
        val mTextBuildDate = "Build Date: " + dateFormat.format(BuildConfig.TIMESTAMP)
        textBuildDate.text = mTextBuildDate
        if (systemUtil.isOrientationPortrait()) {
            textBuildDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 50).toFloat())
        } else {
            textBuildDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 70).toFloat())
        }

        val imageLogo = itemView.findViewById<ImageView>(R.id.image_logo)!!
        Glide.with(context)
                .load(R.drawable.app_feature_graphic)
                .into(imageLogo)

        val textInfo = itemView.findViewById<TextView>(R.id.textAboutInfo)!!
        textInfo.typeface = systemUtil.robotoCondensedRegular
        textInfo.text = context.resources.getString(R.string.about_info, UBOOQUITY_VERSION)
        if (systemUtil.isOrientationPortrait()) {
            textInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 25).toFloat())
        } else {
            textInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 45).toFloat())
        }

        val textDisclaimer = itemView.findViewById<TextView>(R.id.textAboutDisclaimer)!!
        textDisclaimer.typeface = systemUtil.robotoCondensedItalic
        textDisclaimer.text = context.getString(R.string.about_app_disclaimer)
        if (systemUtil.isOrientationPortrait()) {
            textDisclaimer.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 25).toFloat())
        } else {
            textDisclaimer.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 45).toFloat())
        }

        val githubImageView = itemView.findViewById<ImageView>(R.id.image_github)!!
        githubImageView.setOnClickListener { systemUtil.openLink(Constants.GITHUB_URL) }

        Glide.with(context)
                .load(R.drawable.github_badge)
                .into(githubImageView)

        container.addView(itemView)
        return itemView
    }


    private fun getPage1(container: ViewGroup): View {
        val itemView = context.layoutInflater.inflate(R.layout.about_faq, container, false)
        val recyclerView = itemView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.about_faq_recyclerView)
        recyclerView.adapter = faqAdapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        container.addView(itemView)
        return itemView
    }

    private fun getPage2(container: ViewGroup): View {
        val itemView = context.layoutInflater.inflate(R.layout.about_changelog, container, false)
        container.addView(itemView)
        return itemView
    }

    private fun getPage3(container: ViewGroup): View {
        val itemView = context.layoutInflater.inflate(R.layout.about_developer, container, false)

        val imageDeveloper = itemView.findViewById<ImageView>(R.id.about_developer_imageView)!!
        Glide.with(context)
                .load(R.drawable.developer_icon)
                .into(imageDeveloper)

        val textAboutDeveloperName = itemView.findViewById<TextView>(R.id.about_developer_textView1)!!
        textAboutDeveloperName.setText(R.string.about_developer_name)
        textAboutDeveloperName.setTextSize(TypedValue.COMPLEX_UNIT_PX, when (systemUtil.isOrientationPortrait()) {
            true -> (width / 15).toFloat()
            false -> (width / 35).toFloat()
        })
        textAboutDeveloperName.typeface = systemUtil.robotoCondensedBold
        val textAboutDeveloperLink = itemView.findViewById<TextView>(R.id.about_developer_textView2)!!
        textAboutDeveloperLink.text = Constants.DEV_URL
        textAboutDeveloperLink.setTextColor(Color.BLUE)
        textAboutDeveloperLink.paintFlags = textAboutDeveloperLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        if (systemUtil.isOrientationPortrait()) {
            textAboutDeveloperLink.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 30).toFloat())
        } else {
            textAboutDeveloperLink.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 50).toFloat())
        }

        textAboutDeveloperLink.setOnClickListener {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.DEV_URL)))
        }

        val textAboutDeveloperMessage1 = itemView.findViewById<TextView>(R.id.about_developer_textView3)!!
        textAboutDeveloperMessage1.typeface = systemUtil.robotoCondensedRegular
        textAboutDeveloperMessage1.text = context.getString(R.string.about_develop_message)
        if (systemUtil.isOrientationPortrait()) {
            textAboutDeveloperMessage1.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 25).toFloat())
        } else {
            textAboutDeveloperMessage1.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 45).toFloat())
        }
        container.addView(itemView)
        return itemView
    }

    private fun getPage4(container: ViewGroup): View {
        val itemView = context.layoutInflater.inflate(R.layout.about_license, container, false)
        val recyclerView = itemView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.about_license_recyclerView)
        recyclerView.adapter = LicenseAdapter(context, getLicenseList())
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        container.addView(itemView)
        return itemView
    }

    private fun getLicenseList(): List<License> = mutableListOf<License>().apply {
        add(License("ArtifexSoftware/mupdf", licenses.MUPDF))
        add(License("CymChad/BaseRecyclerViewAdapterHelper", licenses.APACHE_V2))
        add(License("bumptech/glide", licenses.GLIDE))
        add(License("daimajia/NumberProgressBar", licenses.NUMBER_PROGRESS_BAR))
        add(License("diego-gomez-olvera/RtlViewPager", licenses.APACHE_V2))
        add(License("edmund-wagner/junrar", licenses.JUNRAR))
        add(License("gabrielemariotti/changeloglib", licenses.APACHE_V2))
        add(License("google/dagger", licenses.APACHE_V2))
        add(License("google/gson", licenses.APACHE_V2))
        add(License("google/opensans", licenses.APACHE_V2))
        add(License("JakeWharton/butterknife", licenses.APACHE_V2))
        add(License("JakeWharton/timber", licenses.APACHE_V2))
        add(License("Kotlin/anko", licenses.APACHE_V2))
        add(License("matrixxun/MaterialBadgeTextView", licenses.APACHE_V2))
        add(License("nkanaev/bubble", licenses.BUBBLE))
        add(License("ongakuer/CircleIndicator", licenses.APACHE_V2))
        add(License("psiegman/epublib", licenses.EPUBLIB))
        add(License("tonyofrancis/Fetch", licenses.APACHE_V2))
        add(License("varunest/SparkButton", licenses.APACHE_V2))
        add(License("wasabeef/recyclerview-animators", licenses.APACHE_V2))
        add(License("ybq/Android-SpinKit", licenses.ANDROID_SPINKIT))
    }

}