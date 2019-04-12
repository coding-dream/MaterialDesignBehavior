package com.wenky.design.base

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.FrameLayout
import butterknife.ButterKnife
import butterknife.Unbinder

/**
 * Created by wl on 2018/11/26.
 */
abstract class BaseActivity : AppCompatActivity() {

    var isAlive = false

    private var unBinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        isAlive = true

        super.onCreate(savedInstanceState)
        val viewGroup = FrameLayout(this)
        viewGroup.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        if (getLayoutId() != 0) {
            val contentView = LayoutInflater.from(this).inflate(getLayoutId(), null)
            viewGroup.addView(contentView)
        }
        setContentView(viewGroup)
        unBinder = ButterKnife.bind(this)
        initView()
    }

    abstract fun initView()

    abstract fun getLayoutId(): Int

    override fun onDestroy() {
        super.onDestroy()
        isAlive = false
        unBinder?.unbind()
    }

    fun startActivity(clazz: Class<*>) {
        val intent = Intent()
        intent.setClass(this, clazz)
        startActivity(intent)
    }
}
