package com.wenky.design;

import android.view.View;
import com.wenky.design.base.BaseActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void initView() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @OnClick({R.id.btn_appbar, R.id.btn_simple_behavior, R.id.btn_recycleView})
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
            default:
                break;
        }
    }
}
