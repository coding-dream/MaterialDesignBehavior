package com.wenky.design.module.double_behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import java.lang.reflect.Field;

import static android.view.MotionEvent.ACTION_DOWN;

public class HeaderBottomBehavior extends CoordinatorLayout.Behavior<View> {

    private static final String TAG = "HeaderBottomBehavior";

    public HeaderBottomBehavior() {
        super();
    }

    public HeaderBottomBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
