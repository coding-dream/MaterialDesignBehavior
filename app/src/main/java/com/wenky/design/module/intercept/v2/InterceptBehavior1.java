package com.wenky.design.module.intercept.v2;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wenky.design.util.LogHelper;

/**
 * Created by wl on 2019/4/29.
 *
 * ================【重点2】================
 * 例外：如果CoordinatorLayout内部有一个可以滚动的View如ScrollView，测试发现 onInterceptTouchEvent里面也可以监听到ACTION_MOVE事件【一般只会触发一次】。
 * 普通的自定义 ViewGroup 如ViewPagerEx也是仅仅触发一次（ACTION_MOVE），MotionEvent.ACTION_DOWN: intercepted = false;的情况下。
 * 可以根据 ACTION_DOWN（ev.getRawY）和 ACTION_MOVE（ev.getRawY）判断是否拦截。
 */
public class InterceptBehavior1 extends CoordinatorLayout.Behavior<View> {

    private int mLastX = 0;
    private int mLastY = 0;

    public InterceptBehavior1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 事件冲突解决方式：外部拦截法（注：只有CoordinatorLayout内部含有一个ScrollView且滑动ScrollView时候，才会触发调用一次MotionEvent.ACTION_MOVE【一次】）
     * 内部含有ScrollView时候拦截方式和普通的自定义ViewGroup一样，使用外部拦截法。因为只有此种情况下才能在MotionEvent.ACTION_MOVE中拦截。
     * 没有ScrollView的情况只能在MotionEvent.ACTION_DOWN 中拦截。
     * @param parent
     * @param child
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent event) {
        boolean intercepted = false;
        final int action = event.getActionMasked();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogHelper.d("Behavior1: ACTION_DOWN（onInterceptTouchEvent）" + event.getRawY());
                // 不可以拦截 ACTION_DOWN 否则后续事件都会交由父容器来处理
                intercepted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                LogHelper.d("Behavior1: ACTION_MOVE（onInterceptTouchEvent）" + event.getRawY());
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (Math.abs(deltaY) > Math.abs(deltaX)) {
                    intercepted = true;
                } else {
                    intercepted = false;
                }

                // TODO 测试拦截
                intercepted = true;
                break;
            case MotionEvent.ACTION_UP:
                LogHelper.d("Behavior1: ACTION_UP（onInterceptTouchEvent）");
                intercepted = false;
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return intercepted;
    }

    /**
     * onTouchEvent 中 MotionEvent.ACTION_DOWN 返回true只对自己（触摸区域为自己）的后续触摸事件有作用。
     * 如果MotionEvent 来自于【拦截】子View的事件，那么返回值 MotionEvent.ACTION_DOWN = (true)false 不影响后续子View的传递。
     *
     * 但是一般开发中我们希望 【自己的事件 + 子View的事件】一样经过自己处理（全部当做自己的事件）
     * 那么onTouchEvent的MotionEvent.ACTION_DOWN 返回true即可。
     */
    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogHelper.d("Behavior1: ACTION_DOWN（onTouchEvent）" + ev.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
                // 此处返回值无意义，只要 MotionEvent.ACTION_DOWN = true，后续事件均经过此处。
                LogHelper.d("Behavior1: ACTION_MOVE（onTouchEvent）" + ev.getY());
                return false;
            case MotionEvent.ACTION_UP:
                LogHelper.d("Behavior1: ACTION_UP（onTouchEvent）" + ev.getY());
                return false;
        }
        return false;
    }
}