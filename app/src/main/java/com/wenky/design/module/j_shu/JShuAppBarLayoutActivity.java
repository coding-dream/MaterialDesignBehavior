package com.wenky.design.module.j_shu;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

/**
 * Created by wl on 2019/2/27.

 * 这个布局和AppBarLayoutActivity完全一样，说明 AppBarLayout（layout_scrollFlags） + NestedScrollView/RecycleView（appbar_scrolling_view_behavior） 是一个超强组合。
 */
public class JShuAppBarLayoutActivity extends BaseActivity {

    @Override
    public void initView() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_jianshu_appbar_layout;
    }
}
