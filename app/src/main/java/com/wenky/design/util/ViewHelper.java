package com.wenky.design.util;

import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.util.Pools;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
/**
 * Created by wl on 2019/4/30.
 */
public class ViewHelper {

    private static final Pools.Pool<Rect> sRectPool = new Pools.SynchronizedPool<>(12);

    /**
     * 参考自 BaseTransientBottomBar，SnakeBar的显示与隐藏逻辑
     */
    // On JB/KK versions of the platform sometimes View.setTranslationY does not
    // result in layout / draw pass, and CoordinatorLayout relies on a draw pass to
    // happen to sync vertical positioning of all its child views
    private static final boolean USE_OFFSET_API = (Build.VERSION.SDK_INT >= 16) && (Build.VERSION.SDK_INT <= 19);

    /**
     * CustomSnakeBar 根据当前View找到合适的Parent（寻找CoordinatorLayout或者帧布局）
     * @param view
     * @return
     */
    public static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return (ViewGroup) view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                }
            }

            // 获取当前View的Parent然后继续循环，看是否是CoordinatorLayout或者FrameLayout，找到了才结束（否则继续向上层找）
            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }



    @NonNull
    private static Rect acquireTempRect() {
        Rect rect = sRectPool.acquire();
        if (rect == null) {
            rect = new Rect();
        }
        return rect;
    }

    private static void releaseTempRect(@NonNull Rect rect) {
        rect.setEmpty();
        sRectPool.release(rect);
    }

    /**
     * 检查CoordinatorLayout坐标中的给定点是否在childView视图范围内
     *
     * @param parent CoordinatorLayout
     * @param child 对childView进行测试
     * @param x 要在CoordinatorLayout的坐标系中测试的X坐标
     * @param y 要在CoordinatorLayout的坐标系中测试的Y坐标
     * @return 如果该点在子视图的边界内，则为true，否则为false
     */
    public boolean isPointInChildBounds(CoordinatorLayout parent, View child, int x, int y) {
        final Rect r = acquireTempRect();
        ZZViewGroupUtils.getDescendantRect(parent, child, r);
        try {
            return r.contains(x, y);
        } finally {
            releaseTempRect(r);
        }
    }
}
