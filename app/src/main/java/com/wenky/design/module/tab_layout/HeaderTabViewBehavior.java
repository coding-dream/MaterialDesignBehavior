package com.wenky.design.module.tab_layout;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.wenky.design.R;

import java.util.List;

public class HeaderTabViewBehavior extends CoordinatorLayout.Behavior<View> {

    private static final String TAG = "HeaderTabViewBehavior";

    private final Rect mHeadRect = new Rect();

    public HeaderTabViewBehavior() {
    }

    public HeaderTabViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return isDependOn(dependency);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
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

    private boolean isDependOn(View dependency) {
        return dependency != null && dependency.getId() == R.id.header;
    }
}
