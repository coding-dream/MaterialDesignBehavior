package com.wenky.design;

import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.wenky.design.base.BaseActivity;

/**
 * Created by wl on 2019/2/27.
 *
 * 根据 AppBarLayoutActivity里面如 app:layout_scrollFlags="scroll|snap" 属性的解释
 * AppBarLayout中的所有子View（TextView，ImageView）都是可以使用layout_scrollFlags属性的，当然CollapsingToolbarLayout也是一样的，
 * 可以直接吧CollapsingToolbarLayout当作一个普通的ViewGroup对待。
 *
 * 设置在CollapsingToolbarLayout子控件的属性：
 * pin：固定模式，在折叠的时候最后固定在顶端；
 * parallax：视差模式，在折叠的时候会有个视差折叠的效果。
 * app:layout_collapseParallaxMultiplier="1.0" 子视图的视觉差，可以通过属性app:layout_collapseParallaxMultiplier=”0.6”改变。值de的范围[0.0,1.0]，值越大视察越大。
 *
 * 神坑：CollapsingToolbarLayout必须设置 android:minHeight="?actionBarSize"（与ToolBar的固定不变的一样大小），否则会导致RecycleView或者NestedScrollingView滑动显示不全
 */
public class CollapsingToolbarLayoutActivity extends BaseActivity {

    @Override
    public void initView() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_layout_toolbar);
        toolbar.setTitleTextColor(Color.TRANSPARENT);
        toolbar.inflateMenu(R.menu.menu_search);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_layout);
        collapsingToolbarLayout.setTitle("");
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.white));
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.e("wl", "appbarHeight:" + appBarLayout.getHeight() + " getTotalScrollRange:" + appBarLayout.getTotalScrollRange() + " offSet:" + verticalOffset);
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                    collapsingToolbarLayout.setTitle("我是标题");
                } else {
                    collapsingToolbarLayout.setTitle("我是标题");
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_collapsing_toolbar_layout;
    }
}
