package com.wenky.design.module.intercept;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wenky.design.util.LogHelper;

/**
 * Created by wl on 2019/4/29.
 * Behavior调用的顺序按照CoordinatorLayout中子View的顺序
 */
public class InterceptBehavior2 extends CoordinatorLayout.Behavior<View> {

    public InterceptBehavior2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        LogHelper.d("onInterceptTouchEvent");
        return false;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        LogHelper.d("onTouchEvent");
        return true;
    }
}
