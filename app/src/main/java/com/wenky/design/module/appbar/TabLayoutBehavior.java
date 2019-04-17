package com.wenky.design.module.appbar;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;

/**
 * 暂无使用，仅仅为了学习测试，演示了如果获取dependencyView的另一种方式，或者可以按照以前在 layoutDependsOn 用 WeakReference的方式引用即可。
 * @see com.wenky.design.module.tab_layout.HeaderTabViewBehavior
 */
public class TabLayoutBehavior extends CoordinatorLayout.Behavior<View> {

    private static final String TAG = "HeaderTabViewBehavior";

    private final Rect mHeadRect = new Rect();

    public TabLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return isDependOn(dependency);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        // 模仿 HeaderScrollingViewBehavior 获取dependencyView的一种方式，按照以前在 layoutDependsOn 用 WeakReference的方式引用即可。
        final List<View> dependencies = parent.getDependencies(child);
        final View dependency = findDependency(dependencies);
        if (dependency != null) {
            // 保持 TabLayout 初始位于Header的下面
            mHeadRect.set(dependency.getLeft(), dependency.getBottom(), dependency.getRight(), dependency.getBottom() + child.getMeasuredHeight());
            child.layout(mHeadRect.left, mHeadRect.top, mHeadRect.right, mHeadRect.bottom);
        }
        return true;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        child.setTranslationY(dependency.getTranslationY());
        return true;
    }

    private View findDependency(List<View> views) {
        for (int i = 0, z = views.size(); i < z; i++) {
            View view = views.get(i);
            if (isDependOn(view))
                return view;
        }
        return null;
    }

    AppBarLayout findFirstDependency(List<View> views) {
        for (int i = 0, z = views.size(); i < z; i++) {
            View view = views.get(i);
            if (view instanceof AppBarLayout) {
                return (AppBarLayout) view;
            }
        }
        return null;
    }

    /**
     * 可以在这里获取到dependView
     * @param dependency
     * @return
     */
    private boolean isDependOn(View dependency) {
        return dependency != null && dependency instanceof AppBarLayout;
    }
}
