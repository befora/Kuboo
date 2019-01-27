package com.sethchhim.kuboo_client.ui.log

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.widget.CheckBox
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.base.BaseActivity

@SuppressLint("Registered")
open class LogActivityImpl0_View : BaseActivity() {

    @BindView(R.id.log_layout_base_recyclerView) lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    @BindView(R.id.log_layout_base_textView) lateinit var textView: TextView
    @BindView(R.id.log_layout_base_checkBox1) lateinit var checkBoxUi: CheckBox
    @BindView(R.id.log_layout_base_checkBox2) lateinit var checkBoxLocal: CheckBox
    @BindView(R.id.log_layout_base_checkBox3) lateinit var checkBoxNetwork: CheckBox
    @BindView(R.id.log_layout_base_checkBox4) lateinit var checkBoxError: CheckBox

    protected fun initUi() {
        setContentView(R.layout.log_layout_base)
        ButterKnife.bind(this)

        checkBoxUi.isChecked = true
        checkBoxLocal.isChecked = true
        checkBoxNetwork.isChecked = true
        checkBoxError.isChecked = false
    }

}