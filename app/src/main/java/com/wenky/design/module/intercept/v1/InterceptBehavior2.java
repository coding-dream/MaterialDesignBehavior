package com.wenky.design.module.intercept.v1;

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
        boolean intercepted = false;
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogHelper.d("Behavior2: ACTION_DOWN（onInterceptTouchEvent）" + ev.getRawY());
                intercepted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                LogHelper.d("Behavior2: ACTION_MOVE（onInterceptTouchEvent）" + ev.getRawY());
                intercepted = false;
                break;
            case MotionEvent.ACTION_UP:
                LogHelper.d("Behavior2: ACTION_UP（onInterceptTouchEvent）");
                intercepted = false;
                break;
        }
        return intercepted;
    }

    /**
     * 根据CoordinatorLayout.onTouchEvent的实现：
     * ============【条件判断】============
     * if (mBehaviorTouchView != null || (cancelSuper = performIntercept(ev, TYPE_ON_TOUCH)))
     * {    handled = b.onTouchEvent(this, mBehaviorTouchView, ev);    }
     *
     * 因为是两个条件判断，Behavior的onTouchEvent(如果onInterceptTouchEvent返回false，ACTION_DOWN会触发此处调用两次)
     * 第一次：用于再次判断是否拦截（遍历所有的child，并设置mBehaviorTouchView）
     * CoordinatorLayout#performIntercept(android.view.MotionEvent, int) for循环中的内容：
     * if (!intercepted && b != null) {
     *     switch (type) {
     *         case TYPE_ON_INTERCEPT:
     *             intercepted = b.onInterceptTouchEvent(this, child, ev);
     *             break;
     *         case TYPE_ON_TOUCH:
     *             intercepted = b.onTouchEvent(this, child, ev);
     *             break;
     *     }
     *     if (intercepted) {
     *         mBehaviorTouchView = child;
     *     }
     * }
     *
     * 总的方法：CoordinatorLayout.onInterceptTouchEvent 和 CoordinatorLayout.onTouchEvent 与普通的事件分发一样。分发每一个child的Behavior的子方法。
     *
     * 遍历所有的Behavior调用其onInterceptTouchEvent和onTouchEvent，两者任一都没有返回true之前，每个child的onInterceptTouchEvent和onTouchEvent都会调用询问是否拦截(ACTION_DOWN中)。
     * 只有当 onInterceptTouchEvent和onTouchEvent 其中之一【第一次ACTION_DOWN】返回true时（true拦截，false 则后续事件都不再经过这两个方法），此时mBehaviorTouchView不再为空.
     * ============【条件判断】============ 部分就不再调用(cancelSuper = performIntercept(ev, TYPE_ON_TOUCH))判断是否拦截了，之后的事件会一直走mBehaviorTouchView的Behavior。
     *
     * 注意：只有ACTION_DOWN时候判断当前Behavior是否拦截事件（onInterceptTouchEvent|onTouchEvent），否则后续事件均不再经过这两个方法。
     * 所以如果想要处理一系列事件，（onInterceptTouchEvent|onTouchEvent）中ACTION_DOWN必须返回true，ACTION_MOVE事件才会经过这两个方法，然后我们可以选择是否处理ACTION_MOVE即可
     * 不处理的话，但是我们发现：CoordinatorLayout.onTouchEvent.ACTION_MOVE 并没有进行任何处理（没有传递给下层的ScrollView），Behavior中return false 无任何意义（事件nothing to do）
     *
     * @param parent
     * @param child
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogHelper.d("Behavior2: ACTION_DOWN（onTouchEvent）");
                return false;
            case MotionEvent.ACTION_MOVE:
                LogHelper.d("Behavior2: ACTION_MOVE（onTouchEvent）");
                return false;
            case MotionEvent.ACTION_UP:
                LogHelper.d("Behavior2: ACTION_UP（onTouchEvent）");
                // false对于CoordinatorLayout来说毫无意义，Behavior已经拦截ACTION_DOWN，后续CoordinatorLayout没有处理false事件
                return false;
        }
        return false;
    }
}
