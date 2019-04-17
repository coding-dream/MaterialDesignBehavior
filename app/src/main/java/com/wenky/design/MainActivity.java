package com.wenky.design;

import android.view.View;
import com.wenky.design.base.BaseActivity;
import com.wenky.design.module.appbar.AppBarLayoutActivity;
import com.wenky.design.module.appbar_refresh_bug.Bug1AppBarRefreshActivity;
import com.wenky.design.module.appbar_refresh_bug.Bug2AppBarRefreshActivity;
import com.wenky.design.module.appbar_translate_scale.AppBarTranslateScaleActivity;
import com.wenky.design.module.bottom_sheet_custom.BottomSheetCustomActivity;
import com.wenky.design.module.bottom_sheet_dialog.BottomSheetBehaviorActivity;
import com.wenky.design.module.collapsing.CollapsingToolbarLayoutActivity;
import com.wenky.design.module.common_recycleview.RecycleViewActivity;
import com.wenky.design.module.double_behavior.DoubleBehaviorPreScrollActivity;
import com.wenky.design.module.drawer.DrawerBehaviorActivityV1;
import com.wenky.design.module.drawer.DrawerBehaviorActivityV2;
import com.wenky.design.module.fling.FlingBehaviorActivity;
import com.wenky.design.module.j_shu.JShuAppBarLayoutActivity;
import com.wenky.design.module.svg.SvgRefreshActivityV1;
import com.wenky.design.module.svg.SvgRefreshActivityV2;
import com.wenky.design.module.tab_layout.TabLayoutActivity;
import com.wenky.design.module.test.TestActivity;
import com.wenky.design.module.think_appbar_source.MyCoordinatorLayoutActivity;
import com.wenky.design.module.viewpager.ViewPagerActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void initView() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @OnClick({
            R.id.btn_test,
            R.id.btn_appbar,
            R.id.btn_simple_behavior,
            R.id.btn_recycleView,
            R.id.btn_myCoordinatorLayout,
            R.id.btn_doubleBehaviorPreScrollActivity,
            R.id.btn_flingBehaviorActivity,
            R.id.btn_svgRefreshActivityV1,
            R.id.btn_svgRefreshActivityV2,
            R.id.BottomSheetBehaviorActivity,
            R.id.JShuAppBarLayoutActivity,
            R.id.ViewPagerActivity,
            R.id.AppBarTranslateScaleActivity,
            R.id.Bug1AppBarRefreshActivity,
            R.id.Bug2AppBarRefreshActivity,
            R.id.DrawerBehaviorActivityV1,
            R.id.DrawerBehaviorActivityV2,
            R.id.TabLayoutActivity,
            R.id.BottomSheetCustomActivity,
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_test:
                startActivity(TestActivity.class);
            case R.id.btn_appbar:
                startActivity(AppBarLayoutActivity.class);
                break;
            case R.id.btn_simple_behavior:
                startActivity(CollapsingToolbarLayoutActivity.class);
                break;
            case R.id.btn_recycleView:
                startActivity(RecycleViewActivity.class);
                break;
            case R.id.btn_myCoordinatorLayout:
                startActivity(MyCoordinatorLayoutActivity.class);
                break;
            case R.id.btn_doubleBehaviorPreScrollActivity:
                startActivity(DoubleBehaviorPreScrollActivity.class);
                break;
            case R.id.btn_flingBehaviorActivity:
                startActivity(FlingBehaviorActivity.class);
                break;
            case R.id.btn_svgRefreshActivityV1:
                startActivity(SvgRefreshActivityV1.class);
                break;
            case R.id.btn_svgRefreshActivityV2:
                startActivity(SvgRefreshActivityV2.class);
                break;
            case R.id.BottomSheetBehaviorActivity:
                startActivity(BottomSheetBehaviorActivity.class);
                break;
            case R.id.JShuAppBarLayoutActivity:
                startActivity(JShuAppBarLayoutActivity.class);
                break;
            case R.id.ViewPagerActivity:
                startActivity(ViewPagerActivity.class);
                break;
            case R.id.AppBarTranslateScaleActivity:
                startActivity(AppBarTranslateScaleActivity.class);
                break;
            case R.id.Bug1AppBarRefreshActivity:
                startActivity(Bug1AppBarRefreshActivity.class);
                break;
            case R.id.Bug2AppBarRefreshActivity:
                startActivity(Bug2AppBarRefreshActivity.class);
                break;
            case R.id.DrawerBehaviorActivityV1:
                startActivity(DrawerBehaviorActivityV1.class);
                break;
            case R.id.DrawerBehaviorActivityV2:
                startActivity(DrawerBehaviorActivityV2.class);
                break;
            case R.id.TabLayoutActivity:
                startActivity(TabLayoutActivity.class);
                break;
            case R.id.BottomSheetCustomActivity:
                startActivity(BottomSheetCustomActivity.class);
                break;
            default:
                break;
        }
    }
}
