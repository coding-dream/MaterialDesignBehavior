package com.wenky.design.util;

import android.animation.ValueAnimator;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class ScrollHelper {

    private ValueAnimator mOffsetAnimator;

    static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    // ms
    private static final int MAX_OFFSET_ANIMATION_DURATION = 600;

    public static boolean canScroll(View scrollingView) {
        return scrollingView != null && scrollingView.isShown() && !scrollingView.canScrollVertically(-1);
    }

    private void animateOffsetTo(final CoordinatorLayout coordinatorLayout,
                                 final AppBarLayout child, final int offset, float velocity) {
        final int distance = Math.abs(getTopBottomOffsetForScrollingSibling() - offset);

        final int duration;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = 3 * Math.round(1000 * (distance / velocity));
        } else {
            final float distanceRatio = (float) distance / child.getHeight();
            duration = (int) ((distanceRatio + 1) * 150);
        }

        animateOffsetWithDuration(coordinatorLayout, child, offset, duration);
    }

    private void animateOffsetWithDuration(final CoordinatorLayout coordinatorLayout,
                                           final AppBarLayout child, final int offset, final int duration) {
        final int currentOffset = getTopBottomOffsetForScrollingSibling();
        if (currentOffset == offset) {
            if (mOffsetAnimator != null && mOffsetAnimator.isRunning()) {
                mOffsetAnimator.cancel();
            }
            return;
        }

        if (mOffsetAnimator == null) {
            mOffsetAnimator = new ValueAnimator();
            mOffsetAnimator.setInterpolator(DECELERATE_INTERPOLATOR);
            mOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                     setHeaderTopBottomOffset(coordinatorLayout, child, (int) animation.getAnimatedValue());
                }
            });
        } else {
            mOffsetAnimator.cancel();
        }

        mOffsetAnimator.setDuration(Math.min(duration, MAX_OFFSET_ANIMATION_DURATION));
        mOffsetAnimator.setIntValues(currentOffset, offset);
        mOffsetAnimator.start();
    }

    /**
     * TODO 待更新，参考AppBarLayout
     *
     * @param coordinatorLayout
     * @param child
     * @param animatedValue
     */
    private void setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout, AppBarLayout child, int animatedValue) {

    }

    /**
     * TODO 待更新，参考AppBarLayout
     *
     * @return
     */
    private int getTopBottomOffsetForScrollingSibling() {
        return 0;
    }

    boolean isOffsetAnimatorRunning() {
        return mOffsetAnimator != null && mOffsetAnimator.isRunning();
    }

    public void cancelAnimator() {
        if (mOffsetAnimator != null) {
            // Cancel any offset animation
            mOffsetAnimator.cancel();
        }
    }
}
