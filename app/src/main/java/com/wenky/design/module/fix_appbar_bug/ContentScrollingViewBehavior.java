package com.wenky.design.module.fix_appbar_bug;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

public class ContentScrollingViewBehavior extends AppBarLayout.ScrollingViewBehavior {

    public ContentScrollingViewBehavior() {
        super();
    }

    public ContentScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
