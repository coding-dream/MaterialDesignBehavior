package com.wenky.design.module.snakebar;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wl on 2019/5/5.
 */
public class SnakeBarBehavior extends CoordinatorLayout.Behavior<View> {

    public SnakeBarBehavior(Context context) {
        this(context, null);
    }

    public SnakeBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
