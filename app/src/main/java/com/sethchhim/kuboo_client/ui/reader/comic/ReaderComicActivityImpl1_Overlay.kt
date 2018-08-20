package com.sethchhim.kuboo_client.ui.reader.comic

import android.annotation.SuppressLint
import android.widget.PopupMenu
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_local.model.ComicInfo
import org.jetbrains.anko.sdk25.coroutines.onClick

@SuppressLint("Registered")
open class ReaderComicActivityImpl1_Overlay : ReaderComicActivityImpl0_View() {

    protected fun setOverlayChapterButton() {
        val comicInfo = when (isLocal) {
            true -> viewModel.getLocalComicInfo()
            false -> ComicInfo() //TODO ubooquity server does not support comic info yet
        }
        if (comicInfo.containsBookmarks()) {
            val popupMenu = PopupMenu(this, overlayChapterButton)
            comicInfo.bookmarks.forEachIndexed { index, pair ->
                popupMenu.menu.add(pair.second)
                popupMenu.menu.getItem(index).setOnMenuItemClickListener {
                    viewPager.currentItem = pair.first
                    return@setOnMenuItemClickListener true
                }
            }
            overlayChapterButton.onClick {
                popupMenu.show()
            }
            overlayChapterButton.visible()
        } else {
            overlayChapterButton.gone()
        }
    }

}