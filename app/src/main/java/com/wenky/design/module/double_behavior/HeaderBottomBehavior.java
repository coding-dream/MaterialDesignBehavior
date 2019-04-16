package com.wenky.design.module.double_behavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.wenky.design.R;
import com.wenky.design.util.LogHelper;

import java.lang.ref.WeakReference;

public class HeaderBottomBehavior extends CoordinatorLayout.Behavior<View> {

    private WeakReference<View> mDependencyView;

    private int mHeaderHeight;

    public HeaderBottomBehavior() {
        super();
    }

    public HeaderBottomBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int nestedScrollAxes, int type) {
        LogHelper.d("onStartNestedScroll");
        boolean started = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        return started;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type);
        LogHelper.d("onNestedScrollAccepted");
        getHeaderHeight(coordinatorLayout);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        // 初始进入页面会调用一次（还未滑动）
        child.setTranslationY(dependency.getHeight() + dependency.getTranslationY());

        float progress = Math.abs(dependency.getTranslationY() / (dependency.getHeight() - getHeaderHeight(parent)));
        LogHelper.d("progress: " + progress);
        return true;
    }


    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        if(dy < 0){
            return;
        }
        LogHelper.d("=============> onNestedPreScroll: " + dy);
        // onNestedPreScroll:::newTranslationY:-10.0 ---> minHeaderTranslation -413.0
        // onNestedPreScroll:::newTranslationY:-24.0 ---> minHeaderTranslation -413.0
        View dependentView = getDependencyView();
        float maxHeaderTranslate = -dependentView.getHeight();
        float newTranslationY = dependentView.getTranslationY() - dy;

        LogHelper.d("onNestedPreScroll:::newTranslationY:" + newTranslationY + "---> maxHeaderTranslate " + maxHeaderTranslate);
        if (newTranslationY >= maxHeaderTranslate) {
            dependentView.setTranslationY(newTranslationY);
            consumed[1] = dy;
        } else {
            // 防止快速滑动，TranslationY超出边界的情况（修复闪动Bug根源），但是因为是向上滑动，所以超出后，需要交给RecycleView处理事件，所以需设置：consumed[1] = 0
            newTranslationY = maxHeaderTranslate;
            dependentView.setTranslationY(newTranslationY);
            consumed[1] = 0;
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        LogHelper.d("onNestedScroll dyUnconsumed: " + dyUnconsumed);

        if(dyUnconsumed > 0){
            return;
        }
        LogHelper.d("=============> onNestedScroll: " + dyUnconsumed);
        View dependentView = getDependencyView();
        float newTranslateY = dependentView.getTranslationY() - dyUnconsumed;
        float maxHeaderTranslate = 0;
        LogHelper.d("onNestedScroll:::newTranslateY:" + newTranslateY + "---> maxHeaderTranslate " + maxHeaderTranslate);
        if (newTranslateY <= maxHeaderTranslate) {
            dependentView.setTranslationY(newTranslateY);
        } else {
            // 防止快速滑动，TranslationY超出边界的情况（修复闪动Bug根源）
            newTranslateY = maxHeaderTranslate;
            dependentView.setTranslationY(newTranslateY);
        }
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if (isDepend(dependency)) {
            mDependencyView = new WeakReference<>(dependency);
            return true;
        }
        return false;
    }

    private boolean isDepend(View dependency){
        return dependency != null && dependency.getId() == R.id.iv_header;
    }

    private View getDependencyView() {
        return mDependencyView.get();
    }

    private int getHeaderHeight(CoordinatorLayout coordinatorLayout) {
        if (mHeaderHeight == 0) {
            mHeaderHeight = coordinatorLayout.findViewById(R.id.iv_header).getMeasuredHeight();
        }
        return mHeaderHeight;
    }
}
