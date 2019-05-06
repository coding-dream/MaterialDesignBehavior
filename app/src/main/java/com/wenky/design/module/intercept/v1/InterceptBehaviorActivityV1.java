package com.wenky.design.module.intercept.v1;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

/**
 * Created by wl on 2019/4/29.
 * 内部没有任何ScrollView的情况，主要处理自己的onTouchEvent事件，事件没有来自ScrollView的情况。
 * 案例：仅处理自己的onTouchEvent事件，不包含ScrollView的向上分发事件（被拦截或onTouchEvent = false向上传递）。
 */
public class InterceptBehaviorActivityV1 extends BaseActivity {

    @Override
    public void initView() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_intercept_v1;
    }
}