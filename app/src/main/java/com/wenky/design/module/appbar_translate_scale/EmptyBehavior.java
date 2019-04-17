package com.wenky.design.module.appbar_translate_scale;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * 写着一个没有意义的Behavior是为了测试
 * CollapsingToolbarLayout app:layout_scrollFlags="scroll|exitUntilCollapsed"
 *
 * 在没有设置 android:minHeight="xxx" 的情况下 NestedScrollView滑动后也能完全显示的问题。
 * 坤湖的代码可能就是没有设置但是某个地方不断去设设置其layoutParam参数才没有出现问题。
 * 但是建议使用CollapsingToolbarLayout一定要设置minHeight，Fucking the code。
 */
public class EmptyBehavior extends CoordinatorLayout.Behavior<View> {

    public EmptyBehavior() {
        super();
    }

    public EmptyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.width = 100;
        lp.height = 100;
        child.setLayoutParams(lp);
        return true;
    }
}
