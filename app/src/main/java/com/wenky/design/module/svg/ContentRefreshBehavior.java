package com.wenky.design.module.svg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.OverScroller;

import com.wenky.design.R;
import com.wenky.design.util.LogHelper;

import java.lang.ref.WeakReference;

public class ContentRefreshBehavior extends CoordinatorLayout.Behavior<View> {

    private OverScroller mOverScroller;
    private WeakReference<View> mDependencyView;
    private int mHeaderHeight;
    private boolean isExpand;

    private CollapsingLayoutStateCallback collapsingLayoutStateCallback;

    private boolean enable = true;
    private boolean isAutoScrolling;
    private boolean isHeaderFling = false;

    public interface CollapsingLayoutStateCallback {
        void expanded();
        void collapsed();
        void internediate();
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setCollapsingLayoutStateCallback(CollapsingLayoutStateCallback callback) {
        this.collapsingLayoutStateCallback = callback;
    }

    public ContentRefreshBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mOverScroller = new OverScroller(context);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int nestedScrollAxes, int type) {
        LogHelper.d("onStartNestedScroll");
        boolean started = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        if (started && !mOverScroller.isFinished()) {
            mOverScroller.abortAnimation();
        }
        return started && enable;
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
        if(dy < 0 || !enable ){
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
        if(dyUnconsumed > 0 || !enable){
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
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type) {
        LogHelper.d("onStopNestedScroll");
        if (enable) {
            // 此处判断是否到达刷新边界
            if (getDependencyView().getTranslationY() == 0 && collapsingLayoutStateCallback != null) {
                collapsingLayoutStateCallback.expanded();
            } else {
                if (!isAutoScrolling) {
                    hideRefreshView();
                }
            }
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, float velocityX, float velocityY) {
        LogHelper.d("onNestedPreFling");
        View dependentView = getDependencyView();
        float maxMoveLength = dependentView.getHeight();

        if (dependentView.getTranslationY() >= 0 || dependentView.getTranslationY() <= -maxMoveLength) {
            LogHelper.d("目前在两个端点处fling: " + dependentView.getTranslationY());
            // 让RecycleView进行处理
            return false;
        }
        // 自己消耗（实际未执行任何操作）
        return true;
    }

    private Runnable flingRunnable = new Runnable() {
        @Override
        public void run() {
            // OverScroller.computeScrollOffset() true说明滚动尚未完成，false说明滚动已经完成。这是一个很重要的方法，通常放在View.computeScroll()中，用来判断是否滚动是否结束。
            if(mOverScroller.computeScrollOffset()){
                LogHelper.d("mOverScroller.getCurrY(): " + mOverScroller.getCurrY());
                getDependencyView().setTranslationY(mOverScroller.getCurrY());
                getDependencyView().post(this);
            } else {
                isExpand = getDependencyView().getTranslationX() != 0;
                isAutoScrolling = false;
            }
        }
    };

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if (isDepend(dependency)) {
            mDependencyView = new WeakReference<>(dependency);
            return true;
        }
        return false;
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        getDependencyView().setTranslationY(-getHeaderHeight(parent));
        parent.onLayoutChild(child, layoutDirection);
        return true;
    }

    private boolean isDepend(View dependency){
        return dependency != null && dependency.getId() == R.id.layout_header;
    }

    private View getDependencyView() {
        return mDependencyView.get();
    }

    private int getHeaderHeight(CoordinatorLayout coordinatorLayout) {
        if (mHeaderHeight == 0) {
            mHeaderHeight = coordinatorLayout.findViewById(R.id.layout_header).getMeasuredHeight();
        }
        return mHeaderHeight;
    }

    public boolean hideRefreshView() {
        if (flingRunnable != null) {
            getDependencyView().removeCallbacks(flingRunnable);
        }

        View dependentView = getDependencyView();
        float maxMoveLength = dependentView.getHeight();
        LogHelper.d("autoScroll true");
        float originTranslateY = dependentView.getTranslationY();
        float targetTranslateY = -maxMoveLength;

        // 计算剩下需要滑动的距离
        // 根据相关公式mFinal = start + distance; scrollTo(mScrollX + x, mScrollY + y);
        int dy = (int) (targetTranslateY - originTranslateY);
        LogHelper.d("auto move dy: " + dy);
        // dy为正，mOverScroller.getCurrY()，则是递增的
        mOverScroller.startScroll(0, (int) originTranslateY, 0, dy, 800);
        // 因为Behavior里面没有View中的computeScroll方法，因此只能这样递归调用。
        getDependencyView().post(flingRunnable);
        isAutoScrolling = true;
        return true;
    }
}
