package com.sethchhim.kuboo_client.ui.preview

import android.os.Bundle
import butterknife.ButterKnife
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.Extensions.showDelayed
import com.sethchhim.kuboo_client.R

class PreviewActivity : PreviewActivityImpl1_Content() {

    override fun onCreate(savedInstanceState: Bundle?) {
        forceOrientationSetting()
        supportPostponeEnterTransition()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preview_layout_base)
        ButterKnife.bind(this)

        title = currentBook.title
        imageView.transitionName = transitionUrl

        textView.typeface = systemUtil.robotoCondensedRegular
        textView.text = currentBook.content

        fab.setOnClickListener { onClickedFab() }

        imageView.loadImage()

        preloadCurrentPage()
    }

    override fun onResume() {
        super.onResume()
        fab.showDelayed()
        textView.fadeVisible()
    }

}