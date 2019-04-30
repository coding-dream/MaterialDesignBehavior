package com.wenky.design.module.floatbutton;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import com.wenky.design.util.LogHelper;

/**
 * Created by wl on 2019/4/30.
 *
 * SnackBar extends BaseTransientBottomBar#animateViewIn(设置TranslateY)
 * 使用的是 SnackBarLayout 创造View视图
 */
public class FloatButtonBehavior extends CoordinatorLayout.Behavior<View> {

    public FloatButtonBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        // SnackBar的getTranslationY弹起的时候从 131，120,50,20,0，隐藏的时候从0,20,50,120,131
        // 说明SnackBar默认的初始化位置是设置了TranslationY = 其高度（隐藏）
        LogHelper.d("dependency.getTranslationY(): " + dependency.getTranslationY());
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }
}
