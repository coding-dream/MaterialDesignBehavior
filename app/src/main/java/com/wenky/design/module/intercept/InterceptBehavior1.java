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
 *
 * 之前有提到过：Behavior的拦截机制不仅仅由onInterceptTouchEvent决定，由于是 CoordinatorLayout触发，所以如果onInterceptTouchEvent返回false，会再次询问onTouchEvent是否拦截
 *
 * onInterceptTouchEvent也会触发两次
 * 1. 取消重置事件：其中第一次构造发送取消事件 resetTouchBehaviors（MotionEvent.ACTION_CANCEL），正序调用Behavior，即如
 * InterceptBehavior1: onInterceptTouchEvent
 * InterceptBehavior2: onInterceptTouchEvent
 * 2. 正常的拦截事件（见上面的解释）
 * InterceptBehavior2: onInterceptTouchEvent
 * InterceptBehavior1: onInterceptTouchEvent
 *
 * ================【重点】================
 * 所以总结来说：所有onInterceptTouchEvent和onTouchEvent都返回false的事件顺序是：
 * InterceptBehavior1: onInterceptTouchEvent(ACTION_CANCEL)
 * InterceptBehavior2: onInterceptTouchEvent(ACTION_CANCEL)
 *
 * InterceptBehavior2: onInterceptTouchEvent(ACTION_DOWN)
 * InterceptBehavior1: onInterceptTouchEvent(ACTION_DOWN)
 *
 * InterceptBehavior2: onTouchEvent(ACTION_DOWN)
 * InterceptBehavior1: onTouchEvent(ACTION_DOWN)
 *
 *  我们发现上述无论如何滑动，只会触发这些事件，根据事件拦截机制，只有（拦截或消耗）ACTION_DOWN事件，后续事件才会经过onInterceptTouchEvent和onTouchEvent
 */
public class InterceptBehavior1 extends CoordinatorLayout.Behavior<View> {

    public InterceptBehavior1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogHelper.d("Behavior1: ACTION_DOWN（onInterceptTouchEvent）");
                break;
            case MotionEvent.ACTION_MOVE:
                LogHelper.d("Behavior1: ACTION_MOVE（onInterceptTouchEvent）");
                break;
            case MotionEvent.ACTION_UP:
                LogHelper.d("Behavior1: ACTION_UP（onInterceptTouchEvent）");
                break;
        }
        return false;
    }

    /**
     * （根据优先级原理，此处不会获取事件）由于Behavior2在ACTION_DOWN中先拦截，所以这里即使 return true 也获取不到事件了。
     *
     */
    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogHelper.d("Behavior1: ACTION_DOWN（onTouchEvent）");
                break;
            case MotionEvent.ACTION_MOVE:
                LogHelper.d("Behavior1: ACTION_MOVE（onTouchEvent）");
                break;
            case MotionEvent.ACTION_UP:
                LogHelper.d("Behavior1: ACTION_UP（onTouchEvent）");
                break;
        }
        return false;
    }
}
