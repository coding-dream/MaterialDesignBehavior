package com.wenky.design.module.my_swipe_refresh.v2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;

import com.wenky.design.util.LogHelper;
import com.wenky.design.util.SystemUtils;

/**
 * 注意该库的 刷新状态 + 完成刷新状态 都是由用户设置
 */
public class NonoRefreshLayout extends ViewGroup implements NestedScrollingParent2 {

    // ================================
    private final String TAG = NonoRefreshLayout.class.getSimpleName();
    public View mTarget;
    private OnRefreshListener mListener;
    NonoRefreshView mCircleView;
    private int mCircleViewIndex = -1;

    int mCircleViewWidth = SystemUtils.dip2px(getContext(), 50);
    int mCircleViewHeight = SystemUtils.dip2px(getContext(), 50);

    int minTranslateY = -mCircleViewHeight;
    int maxTranslateY = SystemUtils.dip2px(getContext(), 100);

    private OnCanFingerDownCallback mFingerDownCallback;

    private float mTotalUnconsumed;
    /**
     * 当前触发的滑动模式是Nested模式（区别于普通模式 事件拦截方式实现的滑动）
     */
    private boolean mNestedScrollInProgress;

    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private int mTouchSlop;
    private boolean mRefreshing;

    private ObjectAnimator mStartAnimator;
    private ObjectAnimator mEndAnimator;
    private ObjectAnimator mCancelAnimator;
    private boolean isAnimatorRunning = false;


    // 非Nested机制的支持
    private static final int INVALID_POINTER = -1;
    private int mActivePointerId = INVALID_POINTER;
    private boolean mIsBeingDragged;
    /**
     * 只在onTouchEvent中设置，默认用户按下ACTION_DOWN时候记录，但是为了敏感度修正（mTouchSlop -> isStartDragging()方法中修正此值）
     */
    private float mInitialMotionY;

    private static final float DRAG_RATE = .5f;
    private boolean isViewPagerDragging;
    private View mNestedScrollingTarget;

    // ================================

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    /**
     * 原方法名 setOnChildScrollUpCallback存在歧义
     * 手指是否仍然可以往下滑动（手指从上 -> 下）
     */
    public interface OnCanFingerDownCallback {
        boolean canFingerScrollDown(NonoRefreshLayout parent, @Nullable View child);
    }

    public void setOnFingerDownCallback(@Nullable OnCanFingerDownCallback callback) {
        mFingerDownCallback = callback;
    }

    public NonoRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        // 触摸的灵敏度
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setWillNotDraw(false);
        createCircleView();
    }

    private void createCircleView() {
        mCircleView = new NonoRefreshView(getContext());
        mCircleView.setTranslationY(minTranslateY);
        addView(mCircleView);
    }

    /**
     * 只需要在 onStartNestedScroll加上 && type == ViewCompat.TYPE_TOUCH 即可
     * @param child
     * @param target
     * @param nestedScrollAxes
     * @param type
     * @return
     */
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes, int type) {
        LogHelper.d("onStartNestedScroll");
        return isEnabled() && !mRefreshing && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && type == ViewCompat.TYPE_TOUCH;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        LogHelper.d("onNestedScrollAccepted");
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type);
        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;

        mNestedScrollingTarget = target;
    }

    /**
     * 每次手指按下：都会触发两次下面的事件，需要小心。
     * D/LogHelper: onStartNestedScroll
     * D/LogHelper: onNestedScrollAccepted
     * D/LogHelper: onStopNestedScroll: 1
     *
     * D/LogHelper: onStartNestedScroll
     * D/LogHelper: onNestedScrollAccepted
     * D/LogHelper: onStopNestedScroll: 1
     */
    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        LogHelper.d("onStopNestedScroll: 1");
        mNestedScrollingParentHelper.onStopNestedScroll(target, type);
        mNestedScrollInProgress = false;
        mNestedScrollingTarget = null;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        // onStopNestedScroll 会在首次触摸屏幕也会触发，所以onNestedScrollAccepted设置mTotalUnconsumed = 0这里if判断 > 0 保证了只在结束时触发一次。
        if (mTotalUnconsumed > 0) {
            LogHelper.d("onStopNestedScroll: 2");
            finishSpinner();
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
                LogHelper.d("dy: " + dy + " mTotalUnconsumed: " + mTotalUnconsumed);
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
            moveSpinner(mTotalUnconsumed);
            onChildViewsChanged();
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        // 下拉刷新
        final int dy = dyUnconsumed;
        if (dy < 0 && !canFingerScrollDown()) {
            LogHelper.d("dxUnconsumed: " + dxUnconsumed + " mTotalUnconsumed: " + mTotalUnconsumed);
            mTotalUnconsumed += Math.abs(dy);
            moveSpinner(mTotalUnconsumed);
            onChildViewsChanged();
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    /**
     * 手指是否仍然可以继续向下滑动（手指从上 -> 下）
     * @return
     */
    private boolean canFingerScrollDown() {
        if (mFingerDownCallback != null) {
            return mFingerDownCallback.canFingerScrollDown(this, mTarget);
        }
        if (mTarget instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mTarget, -1);
        }
        if (mTarget instanceof RecyclerView || mTarget instanceof NestedScrollView) {
            return mTarget.canScrollVertically(-1);
        }
        // 尝试获取 mTarget 的第一层子View是否支持滑动
        if (mTarget instanceof ViewGroup) {
            int childCount = ((ViewGroup) mTarget).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = ((ViewGroup) mTarget).getChildAt(i);
                if (childView instanceof ListView) {
                    return ListViewCompat.canScrollList((ListView) childView, -1);
                } else if (childView instanceof RecyclerView || childView instanceof NestedScrollView) {
                    return childView.canScrollVertically(-1);
                }
            }
        }
        return false;
    }

    /**
     * 移动刷新的View
     * @param overScrollTop 事件滑动距离顶部的距离（正值）
     */
    private void moveSpinner(float overScrollTop) {
        // 设置比率，不让滑动的太快
        overScrollTop = overScrollTop * DRAG_RATE;
        int newTranslateY = (int) (overScrollTop + minTranslateY);
        newTranslateY = Math.min(newTranslateY, maxTranslateY);
        mCircleView.setTranslationY(newTranslateY);
    }

    /**
     * 结束下拉刷新
     */
    private void finishSpinner() {
        if (mCircleView.getTranslationY() >= maxTranslateY * (1 / 2f)) {
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
        if (mCircleView == null) {
            return;
        }
        if (mRefreshing != refreshing) {
            mRefreshing = refreshing;
            if (mRefreshing) {
                // 移动到指定的位置
                startRefreshAnimator(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        // 移动到指定位置后，展示旋转的progressBar，然后回调给用户
                        mCircleView.playLoadingView();

                        if (mListener != null) {
                            mListener.onRefresh();
                            isAnimatorRunning = false;
                        }
                    }
                });
            } else {
                // 结束刷新动画，结束后
                mCircleView.stopLoadingView();

                finishRefreshAnimator(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        reset();
                        isAnimatorRunning = false;
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
        LogHelper.d("startRefreshAnimator");
        if (mStartAnimator != null) {
            mStartAnimator.cancel();
            isAnimatorRunning = false;
        }
        mStartAnimator = ObjectAnimator.ofFloat(mCircleView, "translationY", mCircleView.getTranslationY(), maxTranslateY * .3f);
        mStartAnimator.setDuration(200);
        mStartAnimator.setInterpolator(new DecelerateInterpolator());
        mStartAnimator.addListener(refreshAnimatorListener);
        mStartAnimator.start();
        isAnimatorRunning = true;
    }

    /**
     * 刷新结束后，隐藏缩放刷新View，并结束刷新。
     * @param refreshAnimatorListener
     */
    private void finishRefreshAnimator(AnimatorListenerAdapter refreshAnimatorListener) {
        LogHelper.d("finishRefreshAnimator");
        if (mEndAnimator != null) {
            mEndAnimator.cancel();
            isAnimatorRunning = false;
        }
        mEndAnimator = ObjectAnimator.ofFloat(mCircleView, "translationY", mCircleView.getTranslationY(), minTranslateY);
        mEndAnimator.setDuration(200);
        mEndAnimator.setInterpolator(new AccelerateInterpolator());
        mEndAnimator.addListener(refreshAnimatorListener);
        mEndAnimator.start();
        isAnimatorRunning = true;
    }

    /**
     * 取消刷新后，移动到开始位置
     */
    private void cancelRefreshAnimator() {
        LogHelper.d("cancelRefreshAnimator");
        if (mCancelAnimator != null) {
            mCancelAnimator.cancel();
            isAnimatorRunning = false;
        }
        mCancelAnimator = ObjectAnimator.ofFloat(mCircleView, "translationY", mCircleView.getTranslationY(), minTranslateY);
        mCancelAnimator.setDuration(200);
        mCancelAnimator.setInterpolator(new AccelerateInterpolator());
        mCancelAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimatorRunning = false;
            }
        });
        mCancelAnimator.start();
        isAnimatorRunning = true;
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
        mCircleView.layout((width / 2 - circleWidth / 2), 0, (width / 2 + circleWidth / 2), circleHeight);
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

    /**
     * 重置所有的动画参数
     * 1. 从window移除的时候
     * 2. 禁用刷新控件的时候
     * 3. 下拉刷新动画结束后
     */
    private void reset() {
        mCircleView.setTranslationY(minTranslateY);
        mTotalUnconsumed = 0;
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

    /**
     * 是否正在刷新
     * @return
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    private float startX;
    private float startY;

    /**
     * 非Nested机制的子View，使用的是onInterceptTouchEvent + onTouchEvent触发滑动刷新的
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex;
        if (!isEnabled() || canFingerScrollDown() || mRefreshing || mNestedScrollInProgress || isAnimatorRunning) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 首次按下，触摸点的index = 0
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialMotionY = ev.getY(pointerIndex);
                startX = ev.getX();
                startY = ev.getY();
                isViewPagerDragging = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (isViewPagerDragging) {
                    return false;
                }
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);

                float endX = ev.getX();
                float endY = ev.getY();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    isViewPagerDragging = true;
                    return false;
                }

                isStartDragging(y);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex = -1;

        if (!isEnabled() || canFingerScrollDown() || mRefreshing || mNestedScrollInProgress || isAnimatorRunning) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                LogHelper.d("onTouchEvent: ACTION_DOWN " + mActivePointerId);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = ev.getY(pointerIndex);
                isStartDragging(y);

                if (mIsBeingDragged) {
                    final float overScrollTop = (y - mInitialMotionY);
                    // 只处理 > 0 的时候，说明手势只有下拉的时候才触发(或者手指终点位置 > 手指起始位置)
                    if (overScrollTop > 0) {
                        moveSpinner(overScrollTop);
                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                // ACTION_POINTER_DOWN 在 onInterceptTouchEvent 不会被触发，所以onInterceptTouchEvent不需要加此事件。
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                if (mIsBeingDragged) {
                    final float y = ev.getY(pointerIndex);
                    final float overScrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    mIsBeingDragged = false;
                    finishSpinner();
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return true;
    }

    /**
     * 修正ACTION_DOWN事件的起始点，模仿ViewPager防止触摸事件太敏感
     * @param y
     */
    private void isStartDragging(float y) {
        final float yDiff = y - mInitialMotionY;
        if (yDiff > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialMotionY + mTouchSlop;
            mIsBeingDragged = true;
        }
    }

    /**
     * 多点触控弹起的时候，更换触摸点
     * @param ev
     */
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCircleView.bringToFront();
    }

    private OnPreDrawListener mOnPreDrawListener;

    class OnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
        @Override
        public boolean onPreDraw() {
            onChildViewsChanged();
            return true;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mOnPreDrawListener == null) {
            mOnPreDrawListener = new OnPreDrawListener();
        }
        final ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnPreDrawListener(mOnPreDrawListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();

        if (mOnPreDrawListener != null) {
            ViewTreeObserver vto = getViewTreeObserver();
            vto.removeOnPreDrawListener(mOnPreDrawListener);
        }

        if (mNestedScrollingTarget != null) {
            onStopNestedScroll(mNestedScrollingTarget);
        }
    }

    private void onChildViewsChanged() {
        float translateY = mCircleView.getTranslationY();
        mTarget.setTranslationY(translateY - minTranslateY);
    }
}