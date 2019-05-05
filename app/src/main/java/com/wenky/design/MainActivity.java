package com.wenky.design;

import android.view.View;
import com.wenky.design.base.BaseActivity;
import com.wenky.design.module.bottombutton.BottomButtonActivity;
import com.wenky.design.module.floatbutton.FloatButtonActivity;
import com.wenky.design.module.appbar.AppBarLayoutActivity;
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
import com.wenky.design.module.my_swipe_refresh.v1.MySwipeRefreshActivityV1;
import com.wenky.design.module.my_swipe_refresh.v1.MySwipeRefreshActivityV2;
import com.wenky.design.module.my_swipe_refresh.v2.MySwipeRefreshActivityV3;
import com.wenky.design.module.my_swipe_refresh.v2.MySwipeRefreshActivityV4;
import com.wenky.design.module.snakebar.CustomSnackBarActivity;
import com.wenky.design.module.svg.SvgRefreshActivityV1;
import com.wenky.design.module.svg.SvgRefreshActivityV2;
import com.wenky.design.module.tab_layout.TabLayoutActivity;
import com.wenky.design.module.fix_appbar_bug.FixBugAppBarLayoutActivity;
import com.wenky.design.module.viewpager.ViewPagerActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void initView() {
        startActivity(CustomSnackBarActivity.class);
        finish();
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
            R.id.FixBugAppBarLayoutActivity,
            R.id.btn_doubleBehaviorPreScrollActivity,
            R.id.btn_flingBehaviorActivity,
            R.id.btn_svgRefreshActivityV1,
            R.id.btn_svgRefreshActivityV2,
            R.id.BottomSheetBehaviorActivity,
            R.id.JShuAppBarLayoutActivity,
            R.id.ViewPagerActivity,
            R.id.AppBarTranslateScaleActivity,
            R.id.DrawerBehaviorActivityV1,
            R.id.DrawerBehaviorActivityV2,
            R.id.TabLayoutActivity,
            R.id.BottomSheetCustomActivity,
            R.id.MySwipeRefreshActivityV1,
            R.id.MySwipeRefreshActivityV2,
            R.id.MySwipeRefreshActivityV3,
            R.id.MySwipeRefreshActivityV4,
            R.id.FloatButtonActivity,
            R.id.BottomButtonActivity,
            R.id.CustomSnackBarActivity,
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_test:
                startActivity(FloatButtonActivity.class);
                break;
            case R.id.btn_appbar:
                startActivity(AppBarLayoutActivity.class);
                break;
            case R.id.btn_simple_behavior:
                startActivity(CollapsingToolbarLayoutActivity.class);
                break;
            case R.id.btn_recycleView:
                startActivity(RecycleViewActivity.class);
                break;
            case R.id.FixBugAppBarLayoutActivity:
                startActivity(FixBugAppBarLayoutActivity.class);
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
            case R.id.MySwipeRefreshActivityV1:
                startActivity(MySwipeRefreshActivityV1.class);
                break;
            case R.id.MySwipeRefreshActivityV2:
                startActivity(MySwipeRefreshActivityV2.class);
                break;
            case R.id.MySwipeRefreshActivityV3:
                startActivity(MySwipeRefreshActivityV3.class);
                break;
            case R.id.MySwipeRefreshActivityV4:
                startActivity(MySwipeRefreshActivityV4.class);
                break;
            case R.id.FloatButtonActivity:
                startActivity(FloatButtonActivity.class);
                break;
            case R.id.BottomButtonActivity:
                startActivity(BottomButtonActivity.class);
                break;
            case R.id.CustomSnackBarActivity:
                startActivity(CustomSnackBarActivity.class);
                break;
            default:
                break;
        }
    }
}
