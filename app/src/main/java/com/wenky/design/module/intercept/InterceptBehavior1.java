package com.wenky.design.module.intercept;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wenky.design.util.LogHelper;

/**
 * Created by wl on 2019/4/29.
 * Behavior调用的顺序按照CoordinatorLayout中子View的 倒序 -> CoordinatorLayout#getTopSortedChildren(java.util.List)
 * InterceptBehavior2: onInterceptTouchEvent
 * InterceptBehavior1: onInterceptTouchEvent

 * InterceptBehavior2: onTouchEvent
 * InterceptBehavior1: onTouchEvent
 */
public class InterceptBehavior1 extends CoordinatorLayout.Behavior<View> {

    public InterceptBehavior1(Context context, AttributeSet attrs) {
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
