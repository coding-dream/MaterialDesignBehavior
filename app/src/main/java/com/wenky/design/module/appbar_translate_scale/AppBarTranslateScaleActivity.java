package com.wenky.design.module.appbar_translate_scale;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

/**
 * 坑点注意：在CoordinatorLayout中加上android:fitsSystemWindows="true"引起 CollapsingToolbarLayout属性app:title="title_collapsing_toolbar"的位置偏移问题。
 * 另外注意：自定义AppBarTranslateScaleBehavior后，CollapsingToolbarLayout 即使没有设置android:minHeight="xxx" 属性，NestedScrollView也能全部显示，抽空研究下此原因。
 */
public class AppBarTranslateScaleActivity extends BaseActivity {

    @Override
    public void initView() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_appbar_translate_scale;
    }
}