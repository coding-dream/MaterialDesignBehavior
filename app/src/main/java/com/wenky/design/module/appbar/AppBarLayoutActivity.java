package com.wenky.design.module.appbar;

import android.support.design.widget.AppBarLayout;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import org.jetbrains.anko.ToastsKt;

import butterknife.BindView;

/**
 * Created by wl on 2019/1/25.
 *
 * 多数App效果如UC则是：手指向上，整体向上，然后RecycleView向上，手指向下，RecycleView向下，然后整体向下。这种效果重写了onNestedPreScroll和onNestedScroll两个方法。
 * 常用属性：app:layout_scrollFlags="scroll|enterAlwaysCollapsed"
 *
 * ScrollingView值得是NestedScrollingChild，ChildView可以理解为NestedScrollingParent的behavior
 *
 * 注意：
 * 1. 基本上指的都是滑动View消耗事件的优先级问题。enterXX 表示向下滑动消耗事件View的优先级问题，exitXX表示向上滑动消耗事件View的优先级问题
 * 2. XXCollapsed  的属性需要设置minHeight属性，最小高度，折叠含义。
 *
 * ## scroll 属性（平时一般用这个就够了，当然推荐 scroll}snap 最佳效果）
 *
 * Child View 伴随着滚动事件而滚出或滚进屏幕。
 * 注意两点：
 * 第一点，如果使用了其他值，必定要使用这个值才能起作用；
 * 第二点：如果在这个Child View前面的任何其他Child View没有设置这个值，那么这个Child View的设置将失去作用。
 *
 * ## enterAlways属性（必须写成 scroll | enterAlways）
 *
 * 快速返回模式。其实就是【向下滚动】时ScrollingView和ChildView之间的滚动优先级问题。
 * 对比scroll和scroll | enterAlways设置，发生【向下滚动】事件时，前者优先滚动Scrolling View，
 * 【后者enterAlways】优先滚动Child View，当优先滚动的一方已经全部滚进屏幕之后，另一方ScrollingView才开始滚动。
 *
 * ## enterAlwaysCollapsed属性（必须写成 scroll|enterAlways|enterAlwaysCollapsed）
 *
 * enterAlways的附加值。这里涉及到Child View的高度和最小高度，向下滚动时，Child View先向下滚动最小高度值，
 * 然后Scrolling View开始滚动，到达边界时，Child View再向下滚动，直至显示完全。
 *
 * ```
 * android:layout_height="@dimen/dp_200"
 * android:minHeight="@dimen/dp_56"
 * app:layout_scrollFlags="scroll|enterAlways|enterAlwaysCollapsed"
 * ```
 *
 * ## exitUntilCollapsed属性
 * 这里也涉及到最小高度。发生向上滚动事件时，Child View向上滚动退出直至最小高度，然后Scrolling View开始滚动。也就是，Child View不会完全退出屏幕。
 *
 * 示例代码
 * ```
 * android:layout_height="@dimen/dp_200"
 * android:minHeight="@dimen/dp_56"
 * app:layout_scrollFlags="scroll|exitUntilCollapsed"
 * ```

 * ## snap 属性（强烈推荐）
 *
 *  简单理解，就是Child View滚动比例的一个吸附效果。
 *  也就是说，Child View不会存在局部显示的情况，滚动Child View的部分高度，
 *  当我们松开手指时，Child View要么向上全部滚出屏幕，要么向下全部滚进屏幕，有点类似ViewPager的左右滑动。
 *
 * ```
 * android:layout_height="@dimen/dp_200"
 * app:layout_scrollFlags="scroll|snap"
 * ```
 * 结束，一般推荐 scroll}snap 即可
 *
 * ## AppbarLayout的一个bug
 * 两个子View，如果两个View均可滑动，设置了 app:layout_scrollFlags="scroll|snap"，某些情况下需要设置第一个子view的 android:visibility="gone"（如没有数据时），此时设置marginTop = 20dp(小于第二个View的高度)
 * 发现：第二个View只能滑到距离顶部20dp的位置，不能滑动了，好像这20dp被当做第二个子view的一样，处理这种问题需要动态设置 第一个View的margin。
 */
public class AppBarLayoutActivity extends BaseActivity {

    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    private CollapsingToolbarLayoutState state;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    @Override
    public void initView() {
        appBarLayout.setExpanded(false);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    state = CollapsingToolbarLayoutState.EXPANDED;
                    ToastsKt.longToast(AppBarLayoutActivity.this, state.toString());
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    state = CollapsingToolbarLayoutState.COLLAPSED;
                    ToastsKt.longToast(AppBarLayoutActivity.this, state.toString());
                } else {
                    state = CollapsingToolbarLayoutState.INTERNEDIATE;
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_appbar_layout;
    }
}
