package com.sethchhim.kuboo_client.ui.reader.comic

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.reader.comic.adapter.ReaderComicAdapter

@SuppressLint("Registered")
open class ReaderComicActivityImpl5_Navigator : ReaderComicActivityImpl4_Content() {

    override fun goToFirstPage() {
        viewPager.currentItem = 0
    }

    override fun goToLastPage() {
        viewPager.adapter?.let {
            viewPager.currentItem = it.count - 1
        }
    }

    override fun goToPreviousPage() {
        val isScrolled = scrollToPreviousPosition()
        if (!isScrolled) {
            when (viewPager.currentItem == 0) {
                true -> onSwipeOutOfBoundsStart()
                false -> viewPager.currentItem = viewPager.currentItem - 1
            }
        }
    }

    override fun goToNextPage() {
        val isScrolled = scrollToNextPosition()
        if (!isScrolled) {
            viewPager.adapter?.let {
                when (viewPager.currentItem == it.count - 1) {
                    true -> onSwipeOutOfBoundsEnd()
                    false -> viewPager.currentItem = viewPager.currentItem + 1
                }
            }
        }
    }

    private fun scrollToPreviousPosition(): Boolean {
        val currentFragment = (viewPager.adapter as ReaderComicAdapter).mCurrentFragment
        when (currentFragment) {
            is ReaderComicFragmentImpl1_Single -> {
                val imageView1 = currentFragment.imageView
                if (imageView1.zoomOut()) return true
                return when (systemUtil.isOrientationPortrait()) {
                    true -> when (Settings.RTL) {
                        true -> imageView1.scrollToRight()
                        false -> imageView1.scrollToLeft()
                    }
                    false -> imageView1.scrollToTop()
                }
            }
            is ReaderComicFragmentImpl2_Dual -> {
                val firstImageView = when (Settings.RTL) {
                    true -> currentFragment.imageView1
                    false -> currentFragment.imageView2
                }
                val secondImageView = when (Settings.RTL) {
                    true -> currentFragment.imageView2
                    false -> currentFragment.imageView1
                }
                if (firstImageView.zoomOut()) return true
                when (systemUtil.isOrientationPortrait()) {
                    true -> if (firstImageView.scrollToLeft()) return true
                    false -> if (firstImageView.scrollToTop()) return true
                }
                if (secondImageView.zoomOut()) return true
                when (systemUtil.isOrientationPortrait()) {
                    true -> if (secondImageView.scrollToLeft()) return true
                    false -> if (secondImageView.scrollToTop()) return true
                }
            }
        }
        return false
    }

    private fun scrollToNextPosition(): Boolean {
        val currentFragment = (viewPager.adapter as ReaderComicAdapter).mCurrentFragment
        when (currentFragment) {
            is ReaderComicFragmentImpl1_Single -> {
                val imageView1 = currentFragment.imageView
                if (imageView1.zoomOut()) return true
                return when (systemUtil.isOrientationPortrait()) {
                    true -> when (Settings.RTL) {
                        true -> imageView1.scrollToLeft()
                        false -> imageView1.scrollToRight()
                    }
                    false -> imageView1.scrollToBottom()
                }
            }
            is ReaderComicFragmentImpl2_Dual -> {
                val firstImageView = when (Settings.RTL) {
                    true -> currentFragment.imageView2
                    false -> currentFragment.imageView1
                }
                val secondImageView = when (Settings.RTL) {
                    true -> currentFragment.imageView1
                    false -> currentFragment.imageView2
                }
                if (firstImageView.zoomOut()) return true
                when (systemUtil.isOrientationPortrait()) {
                    true -> if (firstImageView.scrollToRight()) return true
                    false -> if (firstImageView.scrollToBottom()) return true
                }
                if (secondImageView.zoomOut()) return true
                when (systemUtil.isOrientationPortrait()) {
                    true -> if (secondImageView.scrollToRight()) return true
                    false -> if (secondImageView.scrollToBottom()) return true
                }
            }
        }
        return false
    }

}