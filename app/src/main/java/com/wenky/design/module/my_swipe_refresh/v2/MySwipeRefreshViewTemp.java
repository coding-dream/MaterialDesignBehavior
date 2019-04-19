//package com.wenky.design.module.my_swipe_refresh.v2;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.support.annotation.ColorInt;
//import android.support.annotation.ColorRes;
//import android.support.annotation.Nullable;
//import android.support.annotation.VisibleForTesting;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.view.NestedScrollingChildHelper;
//import android.support.v4.view.NestedScrollingParent;
//import android.support.v4.view.NestedScrollingParentHelper;
//import android.support.v4.view.ViewCompat;
//import android.support.v4.widget.ListViewCompat;
//import android.util.AttributeSet;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewConfiguration;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.Animation.AnimationListener;
//import android.view.animation.DecelerateInterpolator;
//import android.view.animation.Transformation;
//import android.widget.AbsListView;
//import android.widget.ListView;
//
//import com.wenky.design.module.my_swipe_refresh.v1.MyCircleImageView;
//import com.wenky.design.module.my_swipe_refresh.v1.MyCircularProgressDrawable;
//
///**
// * support-v4包下的下拉刷新组件源码
// * 修改点：
// * 1）把mTarget对象声明时由private改成了public修饰,方便子类可以访问
// *
// * @author yangjiantong
// */
//public class MySwipeRefreshViewTemp extends ViewGroup implements NestedScrollingParent
//       {
//    // Maps to ProgressBar.Large style
//    public static final int LARGE = MyCircularProgressDrawable.LARGE;
//    // Maps to ProgressBar default style
//    public static final int DEFAULT = MyCircularProgressDrawable.DEFAULT;
//
//    @VisibleForTesting
//    static final int CIRCLE_DIAMETER = 40;
//    @VisibleForTesting
//    static final int CIRCLE_DIAMETER_LARGE = 56;
//
//    private static final String LOG_TAG = MySwipeRefreshViewTemp.class.getSimpleName();
//
//    private static final int MAX_ALPHA = 255;
//    private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);
//
//    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
//    private static final int INVALID_POINTER = -1;
//    private static final float DRAG_RATE = .5f;
//
//    // Max amount of circle that can be filled by progress during swipe gesture,
//    // where 1.0 is a full circle
//    private static final float MAX_PROGRESS_ANGLE = .8f;
//
//    private static final int SCALE_DOWN_DURATION = 150;
//
//    private static final int ALPHA_ANIMATION_DURATION = 300;
//
//    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
//
//    private static final int ANIMATE_TO_START_DURATION = 200;
//
//    // Default background for the progress spinner
//    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
//    // Default offset in dips from the top of the view to where the progress spinner should stop
//    private static final int DEFAULT_CIRCLE_TARGET = 64;
//
//    public View mTarget; // the target of the gesture
//    OnRefreshListener mListener;
//    boolean mRefreshing = false;
//    private int mTouchSlop;
//    private float mTotalDragDistance = -1;
//
//    // If nested scrolling is enabled, the total amount that needed to be
//    // consumed by this as the nested scrolling parent is used in place of the
//    // overscroll determined by MOVE events in the onTouch handler
//    private float mTotalUnconsumed;
//    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
//    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
//    private final int[] mParentScrollConsumed = new int[2];
//    private final int[] mParentOffsetInWindow = new int[2];
//    private boolean mNestedScrollInProgress;
//
//    private int mMediumAnimationDuration;
//    int mCurrentTargetOffsetTop;
//
//    private float mInitialMotionY;
//    private float mInitialDownY;
//    private boolean mIsBeingDragged;
//    private int mActivePointerId = INVALID_POINTER;
//    // Whether this item is scaled up rather than clipped
//    boolean mScale;
//
//    // Target is returning to its start offset because it was cancelled or a
//    // refresh was triggered.
//    private boolean mReturningToStart;
//    private final DecelerateInterpolator mDecelerateInterpolator;
//    private static final int[] LAYOUT_ATTRS = new int[]{
//            android.R.attr.enabled
//    };
//
//    MyCircleImageView mCircleView;
//    private int mCircleViewIndex = -1;
//
//    protected int mFrom;
//
//    float mStartingScale;
//
//    protected int mOriginalOffsetTop;
//
//    int mSpinnerOffsetEnd;
//
//    MyCircularProgressDrawable mProgress;
//
//    private Animation mScaleAnimation;
//
//    private Animation mScaleDownAnimation;
//
//    private Animation mAlphaStartAnimation;
//
//    private Animation mAlphaMaxAnimation;
//
//    private Animation mScaleDownToStartAnimation;
//
//    boolean mNotify;
//
//    private int mCircleDiameter;
//
//    // Whether the client has set a custom starting position;
//    boolean mUsingCustomStart;
//
//    private OnChildScrollUpCallback mChildScrollUpCallback;
//
//    private float startX;
//    private float startY;
//    private boolean isViewPagerDragging = false;
//
//    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
//        @Override
//        public void onAnimationStart(Animation animation) {
//        }
//
//        @Override
//        public void onAnimationRepeat(Animation animation) {
//        }
//
//        @Override
//        public void onAnimationEnd(Animation animation) {
//            if (mRefreshing) {
//                // Make sure the progress view is fully visible
//                mProgress.setAlpha(MAX_ALPHA);
//                mProgress.start();
//                if (mNotify) {
//                    if (mListener != null) {
//                        mListener.onRefresh();
//                    }
//                }
//                mCurrentTargetOffsetTop = mCircleView.getTop();
//            } else {
//                reset();
//            }
//        }
//    };
//
//    void reset() {
//        mCircleView.clearAnimation();
//        mProgress.stop();
//        mCircleView.setVisibility(View.GONE);
//        setColorViewAlpha(MAX_ALPHA);
//        // Return the circle to its start position
//        if (mScale) {
//            setAnimationProgress(0 /* animation complete and view is hidden */);
//        } else {
//            setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop);
//        }
//        mCurrentTargetOffsetTop = mCircleView.getTop();
//    }
//
//    @Override
//    public void setEnabled(boolean enabled) {
//        super.setEnabled(enabled);
//        if (!enabled) {
//            reset();
//        }
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        reset();
//    }
//
//    private void setColorViewAlpha(int targetAlpha) {
//        mCircleView.getBackground().setAlpha(targetAlpha);
//        mProgress.setAlpha(targetAlpha);
//    }
//
//    /**
//     * Simple constructor to use when creating a SwipeRefreshLayout from code.
//     *
//     * @param context
//     */
//    public MySwipeRefreshViewTemp(Context context) {
//        this(context, null);
//    }
//
//    /**
//     * Constructor that is called when inflating SwipeRefreshLayout from XML.
//     *
//     * @param context
//     * @param attrs
//     */
//    public MySwipeRefreshViewTemp(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
//
//        mMediumAnimationDuration = getResources().getInteger(
//                android.R.integer.config_mediumAnimTime);
//
//        setWillNotDraw(false);
//        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
//
//        final DisplayMetrics metrics = getResources().getDisplayMetrics();
//        mCircleDiameter = (int) (CIRCLE_DIAMETER * metrics.density);
//
//        createProgressView();
//        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
//        // the absolute offset has to take into account that the circle starts at an offset
//        mSpinnerOffsetEnd = (int) (DEFAULT_CIRCLE_TARGET * metrics.density);
//        mTotalDragDistance = mSpinnerOffsetEnd;
//        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
//
//        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
//        setNestedScrollingEnabled(true);
//
//        mOriginalOffsetTop = mCurrentTargetOffsetTop = -mCircleDiameter;
//        moveToStart(1.0f);
//
//        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
//        setEnabled(a.getBoolean(0, true));
//        a.recycle();
//    }
//
//    @Override
//    protected int getChildDrawingOrder(int childCount, int i) {
//        if (mCircleViewIndex < 0) {
//            return i;
//        } else if (i == childCount - 1) {
//            // Draw the selected child last
//            return mCircleViewIndex;
//        } else if (i >= mCircleViewIndex) {
//            // Move the children after the selected child earlier one
//            return i + 1;
//        } else {
//            // Keep the children before the selected child the same
//            return i;
//        }
//    }
//
//    private void createProgressView() {
//        mCircleView = new MyCircleImageView(getContext(), CIRCLE_BG_LIGHT);
//        mProgress = new MyCircularProgressDrawable(getContext());
//        mProgress.setStyle(MyCircularProgressDrawable.DEFAULT);
//        mCircleView.setImageDrawable(mProgress);
//        mCircleView.setVisibility(View.GONE);
//        addView(mCircleView);
//    }
//
//    /**
//     * Set the listener to be notified when a refresh is triggered via the swipe
//     * gesture.
//     */
//    public void setOnRefreshListener(OnRefreshListener listener) {
//        mListener = listener;
//    }
//
//    /**
//     * Notify the widget that refresh state has changed. Do not call this when
//     * refresh is triggered by a swipe gesture.
//     *
//     * @param refreshing Whether or not the view should show refresh progress.
//     */
//    public void setRefreshing(boolean refreshing) {
//        if (refreshing && mRefreshing != refreshing) {
//            // scale and show
//            mRefreshing = refreshing;
//            int endTarget = 0;
//            if (!mUsingCustomStart) {
//                endTarget = mSpinnerOffsetEnd + mOriginalOffsetTop;
//            } else {
//                endTarget = mSpinnerOffsetEnd;
//            }
//            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop);
//            mNotify = false;
//            startScaleUpAnimation(mRefreshListener);
//        } else {
//            setRefreshing(refreshing, false /* notify */);
//        }
//    }
//
//    private void startScaleUpAnimation(AnimationListener listener) {
//        mCircleView.setVisibility(View.VISIBLE);
//        if (android.os.Build.VERSION.SDK_INT >= 11) {
//            // Pre API 11, alpha is used in place of scale up to show the
//            // progress circle appearing.
//            // Don't adjust the alpha during appearance otherwise.
//            mProgress.setAlpha(MAX_ALPHA);
//        }
//        mScaleAnimation = new Animation() {
//            @Override
//            public void applyTransformation(float interpolatedTime, Transformation t) {
//                setAnimationProgress(interpolatedTime);
//            }
//        };
//        mScaleAnimation.setDuration(mMediumAnimationDuration);
//        if (listener != null) {
//            mCircleView.setAnimationListener(listener);
//        }
//        mCircleView.clearAnimation();
//        mCircleView.startAnimation(mScaleAnimation);
//    }
//
//    /**
//     * Pre API 11, this does an alpha animation.
//     *
//     * @param progress
//     */
//    void setAnimationProgress(float progress) {
//        mCircleView.setScaleX(progress);
//        mCircleView.setScaleY(progress);
//    }
//
//    private void setRefreshing(boolean refreshing, final boolean notify) {
//        if (mRefreshing != refreshing) {
//            mNotify = notify;
//            ensureTarget();
//            mRefreshing = refreshing;
//            if (mRefreshing) {
//                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener);
//            } else {
//                startScaleDownAnimation(mRefreshListener);
//            }
//        }
//    }
//
//    void startScaleDownAnimation(Animation.AnimationListener listener) {
//        mScaleDownAnimation = new Animation() {
//            @Override
//            public void applyTransformation(float interpolatedTime, Transformation t) {
//                setAnimationProgress(1 - interpolatedTime);
//            }
//        };
//        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
//        mCircleView.setAnimationListener(listener);
//        mCircleView.clearAnimation();
//        mCircleView.startAnimation(mScaleDownAnimation);
//    }
//
//    private void startProgressAlphaStartAnimation() {
//        mAlphaStartAnimation = startAlphaAnimation(mProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
//    }
//
//    private void startProgressAlphaMaxAnimation() {
//        mAlphaMaxAnimation = startAlphaAnimation(mProgress.getAlpha(), MAX_ALPHA);
//    }
//
//    private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
//        Animation alpha = new Animation() {
//            @Override
//            public void applyTransformation(float interpolatedTime, Transformation t) {
//                mProgress.setAlpha(
//                        (int) (startingAlpha + ((endingAlpha - startingAlpha) * interpolatedTime)));
//            }
//        };
//        alpha.setDuration(ALPHA_ANIMATION_DURATION);
//        // Clear out the previous animation listeners.
//        mCircleView.setAnimationListener(null);
//        mCircleView.clearAnimation();
//        mCircleView.startAnimation(alpha);
//        return alpha;
//    }
//
//    /**
//     * @deprecated Use {@link #setProgressBackgroundColorSchemeResource(int)}
//     */
//    @Deprecated
//    public void setProgressBackgroundColor(int colorRes) {
//        setProgressBackgroundColorSchemeResource(colorRes);
//    }
//
//    /**
//     * Set the background color of the progress spinner disc.
//     *
//     * @param colorRes Resource id of the color.
//     */
//    public void setProgressBackgroundColorSchemeResource(@ColorRes int colorRes) {
//        setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getContext(), colorRes));
//    }
//
//    /**
//     * Set the background color of the progress spinner disc.
//     *
//     * @param color
//     */
//    public void setProgressBackgroundColorSchemeColor(@ColorInt int color) {
//        mCircleView.setBackgroundColor(color);
//    }
//
//    /**
//     * @deprecated Use {@link #setColorSchemeResources(int...)}
//     */
//    @Deprecated
//    public void setColorScheme(@ColorRes int... colors) {
//        setColorSchemeResources(colors);
//    }
//
//    /**
//     * Set the color resources used in the progress animation from color resources.
//     * The first color will also be the color of the bar that grows in response
//     * to a user swipe gesture.
//     *
//     * @param colorResIds
//     */
//    public void setColorSchemeResources(@ColorRes int... colorResIds) {
//        final Context context = getContext();
//        int[] colorRes = new int[colorResIds.length];
//        for (int i = 0; i < colorResIds.length; i++) {
//            colorRes[i] = ContextCompat.getColor(context, colorResIds[i]);
//        }
//        setColorSchemeColors(colorRes);
//    }
//
//    /**
//     * Set the colors used in the progress animation. The first
//     * color will also be the color of the bar that grows in response to a user
//     * swipe gesture.
//     *
//     * @param colors
//     */
//    public void setColorSchemeColors(@ColorInt int... colors) {
//        ensureTarget();
//        mProgress.setColorSchemeColors(colors);
//    }
//
//    /**
//     * @return Whether the SwipeRefreshWidget is actively showing refresh
//     * progress.
//     */
//    public boolean isRefreshing() {
//        return mRefreshing;
//    }
//
//    /**
//     * @return Whether it is possible for the child view of this layout to
//     * scroll up. Override this if the child view is a custom view.
//     */
//    public boolean canChildScrollUp() {
//        if (mChildScrollUpCallback != null) {
//            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
//        }
//        if (mTarget instanceof ListView) {
//            return ListViewCompat.canScrollList((ListView) mTarget, -1);
//        }
//        return mTarget.canScrollVertically(-1);
//    }
//
//    /**
//     * Set a callback to override {@link MySwipeRefreshViewTemp#canChildScrollUp()} method. Non-null
//     * callback will return the value provided by the callback and ignore all internal logic.
//     *
//     * @param callback Callback that should be called when canChildScrollUp() is called.
//     */
//    public void setOnChildScrollUpCallback(@Nullable OnChildScrollUpCallback callback) {
//        mChildScrollUpCallback = callback;
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        ensureTarget();
//
//        final int action = ev.getActionMasked();
//        int pointerIndex;
//
//        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
//            mReturningToStart = false;
//        }
//
//        if (!isEnabled() || mReturningToStart || canChildScrollUp()
//                || mRefreshing || mNestedScrollInProgress) {
//            // Fail fast if we're not in a state where a swipe is possible
//            return false;
//        }
//
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView.getTop());
//                mActivePointerId = ev.getPointerId(0);
//                mIsBeingDragged = false;
//
//                pointerIndex = ev.findPointerIndex(mActivePointerId);
//                if (pointerIndex < 0) {
//                    return false;
//                }
//                mInitialDownY = ev.getY(pointerIndex);
//                startX = ev.getX();
//                startY = ev.getY();
//                isViewPagerDragging = false;
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                if (isViewPagerDragging) {
//                    return false;
//                }
//                if (mActivePointerId == INVALID_POINTER) {
//                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
//                    return false;
//                }
//
//                pointerIndex = ev.findPointerIndex(mActivePointerId);
//                if (pointerIndex < 0) {
//                    return false;
//                }
//                final float y = ev.getY(pointerIndex);
//
//                float endX = ev.getX();
//                float endY = ev.getY();
//                float distanceX = Math.abs(endX - startX);
//                float distanceY = Math.abs(endY - startY);
//                if (distanceX > mTouchSlop && distanceX > distanceY) {
//                    isViewPagerDragging = true;
//                    return false;
//                }
//
//                startDragging(y);
//                break;
//
//            case MotionEvent.ACTION_POINTER_UP:
//                onSecondaryPointerUp(ev);
//                break;
//
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                mIsBeingDragged = false;
//                mActivePointerId = INVALID_POINTER;
//                break;
//        }
//
//        return mIsBeingDragged;
//    }
//
//    @Override
//    public void requestDisallowInterceptTouchEvent(boolean b) {
//        // if this is a List < L or another view that doesn't support nested
//        // scrolling, ignore this request so that the vertical scroll event
//        // isn't stolen
//        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
//                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
//            // Nope.
//        } else {
//            super.requestDisallowInterceptTouchEvent(b);
//        }
//    }
//
//    // NestedScrollingParent
//
//    @Override
//    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
//        return isEnabled() && !mReturningToStart && !mRefreshing
//                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
//    }
//
//    @Override
//    public void onNestedScrollAccepted(View child, View target, int axes) {
//        // Reset the counter of how much leftover scroll needs to be consumed.
//        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
//        // Dispatch up to the nested parent
//        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
//        mTotalUnconsumed = 0;
//        mNestedScrollInProgress = true;
//    }
//
//    @Override
//    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
//        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
//        // before allowing the list to scroll
//        if (dy > 0 && mTotalUnconsumed > 0) {
//            if (dy > mTotalUnconsumed) {
//                consumed[1] = dy - (int) mTotalUnconsumed;
//                mTotalUnconsumed = 0;
//            } else {
//                mTotalUnconsumed -= dy;
//                consumed[1] = dy;
//            }
//            moveSpinner(mTotalUnconsumed);
//        }
//
//    }
//
//    @Override
//    public int getNestedScrollAxes() {
//        return mNestedScrollingParentHelper.getNestedScrollAxes();
//    }
//
//    @Override
//    public void onStopNestedScroll(View target) {
//        mNestedScrollingParentHelper.onStopNestedScroll(target);
//        mNestedScrollInProgress = false;
//        // Finish the spinner for nested scrolling if we ever consumed any
//        // unconsumed nested scroll
//        if (mTotalUnconsumed > 0) {
//            finishSpinner(mTotalUnconsumed);
//            mTotalUnconsumed = 0;
//        }
//        // Dispatch up our nested parent
//        stopNestedScroll();
//    }
//
//    @Override
//    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
//                               final int dxUnconsumed, final int dyUnconsumed) {
//        // Dispatch up to the nested parent first
//        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
//                mParentOffsetInWindow);
//
//        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
//        // sometimes between two nested scrolling views, we need a way to be able to know when any
//        // nested scrolling parent has stopped handling events. We do that by using the
//        // 'offset in window 'functionality to see if we have been moved from the event.
//        // This is a decent indication of whether we should take over the event stream or not.
//        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
//        if (dy < 0 && !canChildScrollUp()) {
//            mTotalUnconsumed += Math.abs(dy);
//            moveSpinner(mTotalUnconsumed);
//        }
//    }
//
//    private boolean isAnimationRunning(Animation animation) {
//        return animation != null && animation.hasStarted() && !animation.hasEnded();
//    }
//
//    private void moveSpinner(float overscrollTop) {
//        mProgress.setArrowEnabled(true);
//        float originalDragPercent = overscrollTop / mTotalDragDistance;
//
//        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
//        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
//        float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
//        float slingshotDist = mSpinnerOffsetEnd;
//        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
//                / slingshotDist);
//        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
//                (tensionSlingshotPercent / 4), 2)) * 2f;
//        float extraMove = (slingshotDist) * tensionPercent * 2;
//
//        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
//        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop);
//    }
//
//    private void finishSpinner(float overscrollTop) {
//        if (overscrollTop > mTotalDragDistance) {
//            setRefreshing(true, true /* notify */);
//        } else {
//            // cancel refresh
//            mRefreshing = false;
//            mProgress.setStartEndTrim(0f, 0f);
//            Animation.AnimationListener listener = null;
//            if (!mScale) {
//                listener = new Animation.AnimationListener() {
//
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        if (!mScale) {
//                            startScaleDownAnimation(null);
//                        }
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//
//                };
//            }
//            animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
//            mProgress.setArrowEnabled(false);
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        final int action = ev.getActionMasked();
//        int pointerIndex = -1;
//
//        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
//            mReturningToStart = false;
//        }
//
//        if (!isEnabled() || mReturningToStart || canChildScrollUp()
//                || mRefreshing || mNestedScrollInProgress) {
//            // Fail fast if we're not in a state where a swipe is possible
//            return false;
//        }
//
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mActivePointerId = ev.getPointerId(0);
//                mIsBeingDragged = false;
//                break;
//
//            case MotionEvent.ACTION_MOVE: {
//                pointerIndex = ev.findPointerIndex(mActivePointerId);
//                if (pointerIndex < 0) {
//                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
//                    return false;
//                }
//
//                final float y = ev.getY(pointerIndex);
//                startDragging(y);
//
//                if (mIsBeingDragged) {
//                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
//                    if (overscrollTop > 0) {
//                        moveSpinner(overscrollTop);
//                    } else {
//                        return false;
//                    }
//                }
//                break;
//            }
//            case MotionEvent.ACTION_POINTER_DOWN: {
//                pointerIndex = ev.getActionIndex();
//                if (pointerIndex < 0) {
//                    Log.e(LOG_TAG,
//                            "Got ACTION_POINTER_DOWN event but have an invalid action index.");
//                    return false;
//                }
//                mActivePointerId = ev.getPointerId(pointerIndex);
//                break;
//            }
//
//            case MotionEvent.ACTION_POINTER_UP:
//                onSecondaryPointerUp(ev);
//                break;
//
//            case MotionEvent.ACTION_UP: {
//                pointerIndex = ev.findPointerIndex(mActivePointerId);
//                if (pointerIndex < 0) {
//                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
//                    return false;
//                }
//
//                if (mIsBeingDragged) {
//                    final float y = ev.getY(pointerIndex);
//                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
//                    mIsBeingDragged = false;
//                    finishSpinner(overscrollTop);
//                }
//                mActivePointerId = INVALID_POINTER;
//                return false;
//            }
//            case MotionEvent.ACTION_CANCEL:
//                return false;
//        }
//
//        return true;
//    }
//
//    private void startDragging(float y) {
//        final float yDiff = y - mInitialDownY;
//        if (yDiff > mTouchSlop && !mIsBeingDragged) {
//            mInitialMotionY = mInitialDownY + mTouchSlop;
//            mIsBeingDragged = true;
//            mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
//        }
//    }
//
//    private void animateOffsetToCorrectPosition(int from, AnimationListener listener) {
//        mFrom = from;
//        mAnimateToCorrectPosition.reset();
//        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
//        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
//        if (listener != null) {
//            mCircleView.setAnimationListener(listener);
//        }
//        mCircleView.clearAnimation();
//        mCircleView.startAnimation(mAnimateToCorrectPosition);
//    }
//
//    private void animateOffsetToStartPosition(int from, AnimationListener listener) {
//        if (mScale) {
//            // Scale the item back down
//            startScaleDownReturnToStartAnimation(from, listener);
//        } else {
//            mFrom = from;
//            mAnimateToStartPosition.reset();
//            mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
//            mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
//            if (listener != null) {
//                mCircleView.setAnimationListener(listener);
//            }
//            mCircleView.clearAnimation();
//            mCircleView.startAnimation(mAnimateToStartPosition);
//        }
//    }
//
//    private final Animation mAnimateToCorrectPosition = new Animation() {
//        @Override
//        public void applyTransformation(float interpolatedTime, Transformation t) {
//            int targetTop = 0;
//            int endTarget = 0;
//            if (!mUsingCustomStart) {
//                endTarget = mSpinnerOffsetEnd - Math.abs(mOriginalOffsetTop);
//            } else {
//                endTarget = mSpinnerOffsetEnd;
//            }
//            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
//            int offset = targetTop - mCircleView.getTop();
//            setTargetOffsetTopAndBottom(offset);
//            mProgress.setArrowScale(1 - interpolatedTime);
//        }
//    };
//
//    void moveToStart(float interpolatedTime) {
//        int targetTop = 0;
//        targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
//        int offset = targetTop - mCircleView.getTop();
//        setTargetOffsetTopAndBottom(offset);
//    }
//
//    private final Animation mAnimateToStartPosition = new Animation() {
//        @Override
//        public void applyTransformation(float interpolatedTime, Transformation t) {
//            moveToStart(interpolatedTime);
//        }
//    };
//
//    private void startScaleDownReturnToStartAnimation(int from,
//                                                      Animation.AnimationListener listener) {
//        mFrom = from;
//        mStartingScale = mCircleView.getScaleX();
//        mScaleDownToStartAnimation = new Animation() {
//            @Override
//            public void applyTransformation(float interpolatedTime, Transformation t) {
//                float targetScale = (mStartingScale + (-mStartingScale * interpolatedTime));
//                setAnimationProgress(targetScale);
//                moveToStart(interpolatedTime);
//            }
//        };
//        mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
//        if (listener != null) {
//            mCircleView.setAnimationListener(listener);
//        }
//        mCircleView.clearAnimation();
//        mCircleView.startAnimation(mScaleDownToStartAnimation);
//    }
//
//    void setTargetOffsetTopAndBottom(int offset) {
//        mCircleView.bringToFront();
//        ViewCompat.offsetTopAndBottom(mCircleView, offset);
//        mCurrentTargetOffsetTop = mCircleView.getTop();
//    }
//
//    private void onSecondaryPointerUp(MotionEvent ev) {
//        final int pointerIndex = ev.getActionIndex();
//        final int pointerId = ev.getPointerId(pointerIndex);
//        if (pointerId == mActivePointerId) {
//            // This was our active pointer going up. Choose a new
//            // active pointer and adjust accordingly.
//            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//            mActivePointerId = ev.getPointerId(newPointerIndex);
//        }
//    }
//
//}