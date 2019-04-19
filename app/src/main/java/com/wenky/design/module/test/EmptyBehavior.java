package com.wenky.design.module.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import com.wenky.design.util.LogHelper;

/**
 * 标准的dy计算模板
 * 之前遇到的深坑：dy下发不准的问题并非因为移动后event.getY()导致，参考：NestedScrollView#onTouchEvent(android.view.MotionEvent)
 *
 * dispatchNestedPreScroll
 * dispatchNestedScroll
 *
 * 上面两个的计算中获取 mLastMotionY -= mScrollOffset[1]; 使用的是一个变量 mScrollOffset[1]
 * 这会导致前者dispatchNestedPreScroll稍微计算错误1px，后续的计算就有问题（连锁反应）。
 * 因为onNestedPreScroll 和 onNestedScroll 移动的距离是相同的，我们也使用一个变量来控制，防止导致连锁反应。
 */
public class EmptyBehavior extends CoordinatorLayout.Behavior<View> {

    private float mTotalUnconsumed;

    public EmptyBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int nestedScrollAxes, int type) {
        boolean started = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        return started;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type);
        mTotalUnconsumed = 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                // 纠错能力：瞬间状态（不一定触发）把剩余的消耗完
                consumed[1] = dy - (int) mTotalUnconsumed;
                LogHelper.d("onNestedPreScroll dy > mTotalUnconsumed, " + mTotalUnconsumed + " dy " + dy);
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                LogHelper.d("onNestedPreScroll dy < mTotalUnconsumed, " + mTotalUnconsumed + " dy " + dy);
                consumed[1] = dy;
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        final int dy = dyUnconsumed;
        if (dy < 0 && !canChildScrollUp(target)) {
            mTotalUnconsumed += Math.abs(dy);
            LogHelper.d("onNestedScroll dy < 0, " + mTotalUnconsumed + " dy " + dy);
        }
    }

    public boolean canChildScrollUp(View target) {
        if (target instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) target, -1);
        }
        return target.canScrollVertically(-1);
    }
}
