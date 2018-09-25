package com.sethchhim.kuboo_client.ui.main.home.custom

/**
 * Copyright (C) 2018 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.animation.Interpolator
import jp.wasabeef.recyclerview.animators.BaseItemAnimator

class RecentAnimator : BaseItemAnimator {

    constructor()

    constructor(interpolator: Interpolator) {
        mInterpolator = interpolator
    }

    override fun animateRemoveImpl(holder: RecyclerView.ViewHolder) {
        ViewCompat.animate(holder.itemView)
                .translationY(holder.itemView.height.toFloat())
                .alpha(0f)
                .setDuration(removeDuration)
                .setInterpolator(mInterpolator)
                .setListener(DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder))
                .start()
    }

    override fun preAnimateAddImpl(holder: RecyclerView.ViewHolder?) {
        ViewCompat.setTranslationX(holder!!.itemView, (-holder.itemView.rootView.width).toFloat())
    }

    override fun animateAddImpl(holder: RecyclerView.ViewHolder) {
        ViewCompat.animate(holder.itemView)
                .translationX(0f)
                .setDuration(addDuration)
                .setInterpolator(mInterpolator)
                .setListener(DefaultAddVpaListener(holder))
                .setStartDelay(getAddDelay(holder))
                .start()
    }
}
