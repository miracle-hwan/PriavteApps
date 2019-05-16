package com.miraclehwan.translate.view

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.miraclehwan.translate.R
import com.miraclehwan.translate.databinding.ActivityMainBinding
import com.miraclehwan.translate.viewmodel.TranslateViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var mBinding: ActivityMainBinding
    private val mViewModel by lazy { ViewModelProviders.of(this).get(TranslateViewModel::class.java) }
    private val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initLiveData()

        et_source_content.doAfterTextChanged { checkSourceTextAndTranslate() }
        spinner_source.onItemSelectedListener = this
        spinner_target.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.spinner_source -> {
                et_source_content.setText("")
                tv_target_content.text = ""
            }
            R.id.spinner_target -> {
                tv_target_content.text = ""
                checkSourceTextAndTranslate()
            }
        }
    }

    private fun initLiveData() {
        mViewModel.mTranslateResultLiveData.observe(this, Observer { result ->
            tv_target_content.text = result
        })
    }

    private fun checkSourceTextAndTranslate() {
        if (et_source_content.toString().length == 0) {
            tv_target_content.text = ""
            return
        }

        val sourceLanguage = spinner_source.getSelectedItem().toString()
        val targetLanguage = spinner_target.getSelectedItem().toString()
        if (sourceLanguage.equals(targetLanguage)) {
            return
        }

        mHandler.apply {
            removeCallbacksAndMessages(null)
            postDelayed(
                { mViewModel.doTranslate(sourceLanguage, targetLanguage, et_source_content.text.toString()) },
                1000
            )
        }
    }
}