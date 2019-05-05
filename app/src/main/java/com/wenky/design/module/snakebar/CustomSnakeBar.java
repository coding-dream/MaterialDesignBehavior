package com.wenky.design.module.snakebar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.wenky.design.util.ViewHelper;

/**
 * Created by wl on 2019/4/30.
 * 显示: BaseTransientBottomBar.animateViewIn
 * 隐藏: BaseTransientBottomBar.animateViewOut
 *
 * SnakeBar的源码中存在两次 LayoutInflater.from()
 * CustomSnakeBar extends BaseTransientBottomBar{
 *     private View mView;
 * }
 * design_layout_snack_bar.xml
 * design_layout_snack_bar_include.xml
 *
 * SnackBarContentLayout content = (SnackBarContentLayout) inflater.inflate(R.layout.design_layout_snack_bar_include, parent, false);
 * LayoutInflater inflater = LayoutInflater.from(mContext);
 * mView = (SnackBarLayout) inflater.inflate(R.layout.design_layout_snack_bar, mTargetParent, false);
 * mView.addView(content);
 *
 * 且上述mView创建两个自定义布局 SnackBarLayout & SnackBarContentLayout，事件处理写在两个布局中，不如直接合并成一个。
 *
 * 最终是把mView添加到mTargetParent中，然后动画显示，这里做法不如直接mView的创建放入一个单独的布局SnackBarChildView中，不需这么麻烦
 */
public class CustomSnakeBar {

    static boolean enableSnakeBar = true;

    static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();
    static final Interpolator FAST_OUT_LINEAR_IN_INTERPOLATOR = new FastOutLinearInInterpolator();
    static final Interpolator LINEAR_OUT_SLOW_IN_INTERPOLATOR = new LinearOutSlowInInterpolator();
    static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    // On JB/KK versions of the platform sometimes View.setTranslationY does not
    // result in layout / draw pass, and CoordinatorLayout relies on a draw pass to
    // happen to sync vertical positioning of all its child views
    private static final boolean USE_OFFSET_API = (Build.VERSION.SDK_INT >= 16) && (Build.VERSION.SDK_INT <= 19);

    static final int ANIMATION_DURATION = 250;
    static final int ANIMATION_FADE_DURATION = 180;
    private final Context mContext;

    private ViewGroup mTargetParent;
    private SnackBarChildView mView;

    static final Handler sHandler;
    static final int MSG_SHOW = 0;
    static final int MSG_DISMISS = 1;

    static {
        sHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_SHOW:
                        ((CustomSnakeBar) message.obj).showView();
                        return true;
                    case MSG_DISMISS:
                        ((CustomSnakeBar) message.obj).hideView();
                        return true;
                }
                return false;
            }
        });
    }

    public CustomSnakeBar(ViewGroup parent, SnackBarChildView contentView) {
        this.mTargetParent = parent;
        this.mView = contentView;
        this.mContext = mTargetParent.getContext();
    }

    static CustomSnakeBar make(@NonNull View view, @NonNull CharSequence text) {
        final ViewGroup parent = ViewHelper.findSuitableParent(view);
        if (parent == null) {
            throw new IllegalArgumentException("No suitable parent found from the given view. Please provide a valid view.");
        }
        SnackBarChildView snackBarChildView = new SnackBarChildView(view.getContext());
        CustomSnakeBar currentSnakeBar = new CustomSnakeBar(parent, snackBarChildView);
        snackBarChildView.setSnakeBar(currentSnakeBar);
        return currentSnakeBar;
    }

    void show() {
        if (CustomSnakeBar.enableSnakeBar) {
            addSnakeView();
            sHandler.sendMessageDelayed(sHandler.obtainMessage(MSG_SHOW, CustomSnakeBar.this), 200);
        }
    }

    void hide() {
        sHandler.sendMessageDelayed(sHandler.obtainMessage(MSG_DISMISS, CustomSnakeBar.this), 200);
    }

    private void showView() {
        animateViewIn();
    }

    private void addSnakeView() {
        // 先设置需要添加View的Behavior，再添加到mTargetParent中，然后再设置TransLateY = height，然后动画显示
        if (mView.getParent() == null) {
            // 设置Behavior
            CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.BOTTOM;
            SnakeBarBehavior behavior = new SnakeBarBehavior(mContext);
            lp.setBehavior(behavior);
            // 添加到CoordinatorLayout容器中
            mTargetParent.addView(mView, lp);
            mView.setVisibility(View.INVISIBLE);
            CustomSnakeBar.enableSnakeBar = false;
        }
    }

    private void hideView(){
        if (mView.getVisibility() == View.VISIBLE) {
            animateViewOut();
        } else {
            onViewHidden();
        }
    }

    /**
     * 显示SnakeBar
     */
    private void animateViewIn() {
        final int viewHeight = mView.getHeight();
        if (USE_OFFSET_API) {
            ViewCompat.offsetTopAndBottom(mView, viewHeight);
        } else {
            mView.setTranslationY(viewHeight);
        }
        mView.setVisibility(View.VISIBLE);
        final ValueAnimator animator = new ValueAnimator();
        animator.setIntValues(viewHeight, 0);
        animator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
        animator.setDuration(ANIMATION_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                mView.animateContentIn(
                        ANIMATION_DURATION - ANIMATION_FADE_DURATION,
                        ANIMATION_FADE_DURATION);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private int mPreviousAnimatedIntValue = viewHeight;

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int currentAnimatedIntValue = (int) animator.getAnimatedValue();
                if (USE_OFFSET_API) {
                    ViewCompat.offsetTopAndBottom(mView,
                            currentAnimatedIntValue - mPreviousAnimatedIntValue);
                } else {
                    mView.setTranslationY(currentAnimatedIntValue);
                }
                mPreviousAnimatedIntValue = currentAnimatedIntValue;
            }
        });
        animator.start();
    }

    /**
     * 隐藏SnakeBar
     */
    private void animateViewOut() {
        final ValueAnimator animator = new ValueAnimator();
        animator.setIntValues(0, mView.getHeight());
        animator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
        animator.setDuration(ANIMATION_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                mView.animateContentOut(0, ANIMATION_FADE_DURATION);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                onViewHidden();
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private int mPreviousAnimatedIntValue = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int currentAnimatedIntValue = (int) animator.getAnimatedValue();
                if (USE_OFFSET_API) {
                    ViewCompat.offsetTopAndBottom(mView, currentAnimatedIntValue - mPreviousAnimatedIntValue);
                } else {
                    mView.setTranslationY(currentAnimatedIntValue);
                }
                mPreviousAnimatedIntValue = currentAnimatedIntValue;
            }
        });
        animator.start();
    }

    void onViewHidden() {
        mView.setVisibility(View.INVISIBLE);
        ViewParent parent = this.mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup)parent).removeView(this.mView);
        }
        CustomSnakeBar.enableSnakeBar = true;
    }
}
