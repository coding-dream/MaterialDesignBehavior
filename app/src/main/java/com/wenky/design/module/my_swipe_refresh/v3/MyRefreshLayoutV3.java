package com.wenky.design.module.my_swipe_refresh.v3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;

import com.wenky.design.R;
import com.wenky.design.module.my_swipe_refresh.v1.MyCircleImageView;
import com.wenky.design.util.SystemUtils;

/**
 * 注意该库的 刷新状态 + 完成刷新状态 都是由用户设置
 */
public class MyRefreshLayoutV3 extends ViewGroup implements NestedScrollingParent2 {

    // ================================
    public View mTarget;
    private OnRefreshListener mListener;
    MyCircleImageView mCircleView;
    private int mCircleViewIndex = -1;

    /**
     * 当前刷新View距离顶部的距离
     */
    int mCurrentCircleViewOffsetTop;
    /**
     * 初始化CircleView的原始位置
     */
    int mInitCircleViewOffsetTop;
    /**
     * CircleView总共可以移动的距离
     */
    int mTotalCircleViewDistance;

    int mCircleViewWidth = SystemUtils.dip2px(getContext(), 50);
    int mCircleViewHeight = SystemUtils.dip2px(getContext(), 50);

    private OnChildScrollUpCallback mChildScrollUpCallback;

    private float mTotalUnconsumed;
    private boolean mNestedScrollInProgress;

    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private int mTouchSlop;
    private boolean mRefreshing;

    private ObjectAnimator mStartAnimator;
    private ObjectAnimator mEndAnimator;
    private ObjectAnimator mCancelAnimator;

    // ================================

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnChildScrollUpCallback {
        boolean canChildScrollUp(MyRefreshLayoutV3 parent, @Nullable View child);
    }

    public void setOnChildScrollUpCallback(@Nullable OnChildScrollUpCallback callback) {
        mChildScrollUpCallback = callback;
    }


    public MyRefreshLayoutV3(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        // 触摸的灵敏度
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        // 初始化原始Circle位置
        mInitCircleViewOffsetTop = - mCircleViewHeight;
        // 初始化当前Circle位置
        mCurrentCircleViewOffsetTop = - mCircleViewHeight;
        mTotalCircleViewDistance = SystemUtils.dip2px(getContext(), 120);

        createCircleView();
    }

    private void createCircleView() {
        mCircleView = new MyCircleImageView(getContext(), 0xFFFAFAFA);
        mCircleView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_loading));
        addView(mCircleView);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes, int type) {
        return isEnabled() && !mRefreshing && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);

        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScrollInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            finishSpinner(mTotalUnconsumed);
            mTotalUnconsumed = 0;
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
            moveSpinner(mTotalUnconsumed);
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        // 下拉刷新
        final int dy = dyUnconsumed;
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy);
            moveSpinner(mTotalUnconsumed);
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    /**
     * 是否子View还可以向上滚动
     * @return
     */
    private boolean canChildScrollUp() {
        if (mChildScrollUpCallback != null) {
            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
        }
        if (mTarget instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mTarget, -1);
        }
        return mTarget.canScrollVertically(-1);
    }

    /**
     * 移动刷新的View
     * @param overScrollTop 事件滑动距离顶部的距离（正值）
     */
    private void moveSpinner(float overScrollTop) {
        // 从 原始位置（mInitCircleViewOffsetTop） ~ 最终位置（targetTop）
        float originalDragPercent = overScrollTop / mTotalCircleViewDistance;
        // 上面的肯定会超出（限制 0 ~ 1）
        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));

        float moveLength = dragPercent * mTotalCircleViewDistance;
        mCircleView.setTranslationY(moveLength);
    }

    /**
     * 结束下拉刷新
     * @param overScrollTop 事件滑动距离顶部的距离（正值）
     */
    private void finishSpinner(float overScrollTop) {
        if (overScrollTop > mTotalCircleViewDistance) {
            setRefreshing(true);
        } else {
            cancelRefresh();
        }
    }

    /**
     * 滑动距离没有超过刷新距离，取消刷新
     */
    private void cancelRefresh() {
        // cancel refresh
        mRefreshing = false;
        cancelRefreshAnimator();
    }

    public void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            mRefreshing = refreshing;
            if (mRefreshing) {
                startRefreshAnimator(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        // 移动到指定位置后，展示旋转的progressBar，然后回调给用户
                        if (mListener != null) {
                            mListener.onRefresh();
                            mCurrentCircleViewOffsetTop = mCircleView.getTop();
                        }
                    }
                });
            } else {
                // 结束刷新动画，结束后
                finishRefreshAnimator(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        reset();
                        mCurrentCircleViewOffsetTop = mCircleView.getTop();
                    }
                });
            }
        }
    }

    /**
     * 触发刷新后，移动到中间位置，并开始刷新。
     * @param refreshAnimatorListener
     */
    private void startRefreshAnimator(AnimatorListenerAdapter refreshAnimatorListener) {
        if (mStartAnimator != null) {
            mStartAnimator.cancel();
            mStartAnimator.removeAllListeners();
        }
        mStartAnimator = ObjectAnimator.ofFloat(mCircleView, "translationY", mCircleView.getTranslationY(), mTotalCircleViewDistance / 2f);
        mStartAnimator.setDuration(3000);
        mStartAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mStartAnimator.addListener(refreshAnimatorListener);
        mStartAnimator.start();
    }

    /**
     * 刷新结束后，隐藏缩放刷新View，并结束刷新。
     * @param refreshAnimatorListener
     */
    private void finishRefreshAnimator(AnimatorListenerAdapter refreshAnimatorListener) {
        if (mEndAnimator != null) {
            mEndAnimator.cancel();
            mEndAnimator.removeAllListeners();
        }
        mEndAnimator = ObjectAnimator.ofFloat(mCircleView, "translationY", mCircleView.getTranslationY(), mInitCircleViewOffsetTop);
        mEndAnimator.setDuration(3000);
        mEndAnimator.setInterpolator(new LinearInterpolator());
        mEndAnimator.addListener(refreshAnimatorListener);
        mEndAnimator.start();
    }

    /**
     * 取消刷新后，移动到开始位置
     */
    private void cancelRefreshAnimator() {
        if (mCancelAnimator != null) {
            mCancelAnimator.cancel();
            mCancelAnimator.removeAllListeners();
        }
        mCancelAnimator = ObjectAnimator.ofFloat(mCircleView, "translationY", mCircleView.getTranslationY(), mInitCircleViewOffsetTop);
        mCancelAnimator.setDuration(3000);
        mCancelAnimator.setInterpolator(new LinearInterpolator());
        mCancelAnimator.start();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        int circleWidth = mCircleView.getMeasuredWidth();
        int circleHeight = mCircleView.getMeasuredHeight();
        // layout 刷新View的left,top,right,bottom
        mCircleView.layout((width / 2 - circleWidth / 2), mCurrentCircleViewOffsetTop, (width / 2 + circleWidth / 2), mCurrentCircleViewOffsetTop + circleHeight);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(
                MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        mCircleView.measure(
                MeasureSpec.makeMeasureSpec(mCircleViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleViewHeight, MeasureSpec.EXACTLY));
        mCircleViewIndex = -1;
        // Get the index of the circleview.
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mCircleView) {
                mCircleViewIndex = index;
                break;
            }
        }
    }

    /**
     * 确定目标 RecycleView 或 NestedScrollView
     */
    private void ensureTarget() {
        // 如果parent还没有layout出来，请不要费心获取parent的height
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mCircleView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    /**
     * 配置View绘制的层级顺序，默认 return i，但是因为CircleView是首先添加到MyRefreshLayout中的，所以按照默认顺序的话绘制会在底部。
     * @param childCount
     * @param i 当前child的index
     * @return
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mCircleViewIndex < 0) {
            return i;
        } else if (i == childCount - 1) {
            // Draw the selected child last
            return mCircleViewIndex;
        } else if (i >= mCircleViewIndex) {
            // Move the children after the selected child earlier one
            return i + 1;
        } else {
            // Keep the children before the selected child the same
            return i;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            reset();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    /**
     * 重置所有的动画参数
     * 1. 从window移除的时候
     * 2. 禁用刷新控件的时候
     * 3. 下拉刷新动画结束后
     */
    private void reset() {
        mCircleView.clearAnimation();
    }

    /**
     * 供子类调用的方式
     * @param b
     */
    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }
}