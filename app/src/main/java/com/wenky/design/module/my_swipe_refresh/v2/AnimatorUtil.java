package com.wenky.design.module.my_swipe_refresh.v2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class AnimatorUtil {

    private ObjectAnimator objectAnimator;

    public void layoutTranslationAnimation(final Context context, final View view, int fromY, int toY) {
        clearBeforeAnimation();
        objectAnimator = ObjectAnimator.ofFloat(view, "translationY", fromY, toY);
        objectAnimator.setDuration(100);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
        objectAnimator.start();
    }

    private void clearBeforeAnimation() {
        if (objectAnimator != null) {
            objectAnimator.cancel();
            objectAnimator.removeAllListeners();
        }
    }
}
