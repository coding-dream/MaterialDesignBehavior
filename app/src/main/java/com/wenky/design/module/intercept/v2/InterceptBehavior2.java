package com.wenky.design.module.intercept.v2;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wenky.design.util.LogHelper;

/**
 * Created by wl on 2019/4/29.
 */
public class InterceptBehavior2 extends CoordinatorLayout.Behavior<View> {

    public InterceptBehavior2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogHelper.d("Behavior2: ACTION_DOWN（onInterceptTouchEvent）" + ev.getRawY());
                return false;
            case MotionEvent.ACTION_MOVE:
                LogHelper.d("Behavior2: ACTION_MOVE（onInterceptTouchEvent）" + ev.getRawY());
                return false;
            case MotionEvent.ACTION_UP:
                LogHelper.d("Behavior2: ACTION_UP（onInterceptTouchEvent）");
                return false;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogHelper.d("Behavior2: ACTION_DOWN（onTouchEvent）" + ev.getY());
                return false;
            case MotionEvent.ACTION_MOVE:
                LogHelper.d("Behavior2: ACTION_MOVE（onTouchEvent）" + ev.getY());
                return false;
            case MotionEvent.ACTION_UP:
                LogHelper.d("Behavior2: ACTION_UP（onTouchEvent）" + ev.getY());
                return false;
        }
        return false;
    }
}
