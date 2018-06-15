package com.sethchhim.kuboo_client.ui.state

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sethchhim.kuboo_client.R
import dagger.android.support.DaggerFragment

class LoadingFragment : DaggerFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_layout_loading, container, false)
    }

}