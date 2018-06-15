package com.sethchhim.kuboo_client.ui.preview

import android.annotation.SuppressLint
import android.support.design.widget.FloatingActionButton
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.model.ReadData
import com.sethchhim.kuboo_client.ui.base.BaseActivity
import com.sethchhim.kuboo_client.ui.base.custom.OnLoadCallback
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

@SuppressLint("Registered")
open class PreviewActivityImpl0_View : BaseActivity(), OnLoadCallback {

    @BindView(R.id.preview_layout_base_floatingActionButton) lateinit var fab: FloatingActionButton
    @BindView(R.id.preview_layout_base_imageView) lateinit var imageView: ImageView
    @BindView(R.id.preview_layout_base_textView) lateinit var textView: TextView

    override fun onFinishLoad() = fab.show()

    protected fun onClickedFab() {
        launch(UI) {
            fab.hide()
            delay(300, TimeUnit.MILLISECONDS)
            try {
                startReader(ReadData(book = currentBook,
                        sharedElement = imageView,
                        onLoadCallback = this@PreviewActivityImpl0_View))
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
    }

}