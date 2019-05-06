package com.wenky.design.module.intercept.v1;

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
 * ================【重点1】================
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
 * 我们发现上述无论如何滑动，只会触发这些事件，根据事件拦截机制，只有（拦截或消耗）ACTION_DOWN 事件，后续事件才会经过onInterceptTouchEvent和onTouchEvent
 *
 * 上面【重点】部分非常重要，所以CoordinatorLayout的拦截设计有点鸡肋（onInterceptTouchEvent多此一举），所以最终决定具体哪一个Behavior拦截的由下面的先后顺序决定。
 * 【更正说法】因为是处理自己的事件，那么就不需要重写onInterceptTouchEvent。onInterceptTouchEvent 常用于处理滑动冲突（子View的事件）
 *
 * InterceptBehavior2: onInterceptTouchEvent(ACTION_DOWN)
 * InterceptBehavior1: onInterceptTouchEvent(ACTION_DOWN)
 * InterceptBehavior2: onTouchEvent(ACTION_DOWN)
 * InterceptBehavior1: onTouchEvent(ACTION_DOWN)
 *
 * 即如果两个Behavior都设置onInterceptTouchEvent
 * 1. 则有onInterceptTouchEvent（ACTION_DOWN）决定是否拦截，
 * 2. 如果都没有设置，则由onTouchEvent（ACTION_DOWN）决定是否拦截。
 *
 * ================【重点2】================
 * 例外：如果CoordinatorLayout内部有一个可以滚动的View如ScrollView，测试发现 onInterceptTouchEvent里面也可以监听到ACTION_MOVE事件（只会触发一次）
 * 普通的自定义 ViewGroup 如ViewPagerEx也是仅仅触发一次（ACTION_MOVE），MotionEvent.ACTION_DOWN: intercepted = false;的情况下。
 * 可以根据 ACTION_DOWN（ev.getRawY）ACTION_MOVE（ev.getRawY）判断是否拦截。
 */
public class InterceptBehavior1 extends CoordinatorLayout.Behavior<View> {

    public InterceptBehavior1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent event) {
        boolean intercepted = false;
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogHelper.d("Behavior1: ACTION_DOWN（onInterceptTouchEvent）" + event.getRawY());
                // 不可以拦截 ACTION_DOWN 否则后续事件都会交由父容器来处理
                intercepted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                LogHelper.d("Behavior1: ACTION_MOVE（onInterceptTouchEvent）" + event.getRawY());
                intercepted = false;
                break;
            case MotionEvent.ACTION_UP:
                LogHelper.d("Behavior1: ACTION_UP（onInterceptTouchEvent）");
                intercepted = false;
                break;
            default:
                break;
        }
        return intercepted;
    }

    /**
     * 如果Behavior2 在onInterceptTouchEvent（ACTION_DOWN）中先拦截，那么这里即使 return true 也获取不到事件了（根据优先级原理）。
     */
    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogHelper.d("Behavior1: ACTION_DOWN（onTouchEvent）");
                return false;
            case MotionEvent.ACTION_MOVE:
                LogHelper.d("Behavior1: ACTION_MOVE（onTouchEvent）");
                return false;
            case MotionEvent.ACTION_UP:
                // false对于CoordinatorLayout来说毫无意义，Behavior已经拦截ACTION_DOWN，后续CoordinatorLayout没有处理false事件
                LogHelper.d("Behavior1: ACTION_UP（onTouchEvent）");
                return false;
        }
        return false;
    }
}
