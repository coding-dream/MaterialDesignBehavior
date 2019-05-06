package com.wenky.design.module.intercept.v2;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

/**
 * Created by wl on 2019/4/29.
 * 此Demo与V1版的根本区别在于V1处理的是CoordinatorLayout自己的onTouchEvent事件。
 * 而V2处理的是两种事件【自己的onTouchEvent】 + 【内部ScrollView向上分发的事件】。
 */
public class InterceptBehaviorActivityV2 extends BaseActivity {

    @Override
    public void initView() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_intercept_v2;
    }
}