package com.sethchhim.kuboo_client.ui.reader.comic.custom

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ReaderViewPagerVerticalImpl(context: Context, attrs: AttributeSet) : ReaderViewPagerImpl1_Edge(context, attrs) {

    private val isLandscape = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    private var orientation = when (isLandscape) {
        true -> Orientation.Vertical
        false -> Orientation.Horizontal
    }

    private enum class Orientation { Horizontal, Vertical }

    init {
        if (isOrientationVertical()) setPageTransformer(true, VerticalPageTransformer())
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    override fun canScrollHorizontally(direction: Int) = false

    override fun canScrollVertically(direction: Int) = super.canScrollHorizontally(direction)

    override fun onInterceptTouchEvent(motionEvent: MotionEvent) = when (isOrientationVertical()) {
        true -> {
            val toIntercept = super.onInterceptTouchEvent(flipXY(motionEvent))
            flipXY(motionEvent)
            toIntercept
        }
        false -> super.onInterceptTouchEvent(motionEvent)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent) = when (isOrientationVertical()) {
        true -> {
            val toHandle = super.onTouchEvent(flipXY(motionEvent))
            flipXY(motionEvent)
            toHandle
        }
        false -> super.onTouchEvent(motionEvent)
    }

    private fun isOrientationVertical() = orientation == Orientation.Vertical

    private fun flipXY(motionEvent: MotionEvent): MotionEvent {
        val width = width.toFloat()
        val height = height.toFloat()
        val x = motionEvent.y / height * width
        val y = motionEvent.x / width * height
        motionEvent.setLocation(x, y)
        return motionEvent
    }

    private class VerticalPageTransformer : ViewPager.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            when {
                position < -1 -> view.alpha = 0f// This page is way off-screen to the left.
                position <= 1 -> {
                    view.alpha = 1f
                    // Counteract the default slide transition
                    view.translationX = view.width * -position
                    // set Y position to swipe in from top
                    val yPosition = position * view.height
                    view.translationY = yPosition
                }
                else -> view.alpha = 0f // This page is way off-screen to the right.
            }
        }
    }

}