package com.wenky.design;

import android.view.View;
import com.wenky.design.base.BaseActivity;
import com.wenky.design.module.appbar.AppBarLayoutActivity;
import com.wenky.design.module.collapsing.CollapsingToolbarLayoutActivity;
import com.wenky.design.module.common_recycleview.RecycleViewActivity;
import com.wenky.design.module.double_behavior.DoubleBehaviorPreScrollActivity;
import com.wenky.design.module.fling.FlingBehaviorActivity;
import com.wenky.design.module.svg.SvgRefreshActivityV1;
import com.wenky.design.module.svg.SvgRefreshActivityV2;
import com.wenky.design.module.think_appbar_source.MyCoordinatorLayoutActivity;

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
            R.id.btn_appbar,
            R.id.btn_simple_behavior,
            R.id.btn_recycleView,
            R.id.btn_myCoordinatorLayout,
            R.id.btn_doubleBehaviorPreScrollActivity,
            R.id.btn_flingBehaviorActivity,
            R.id.btn_svgRefreshActivityV1,
            R.id.btn_svgRefreshActivityV2
    })
    public void onClick(View view) {
        switch (view.getId()) {
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
            default:
                break;
        }
    }
}
