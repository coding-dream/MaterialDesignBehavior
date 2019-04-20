package com.wenky.design.module.my_swipe_refresh.v2;

import android.view.View;
import android.widget.Button;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import butterknife.BindView;

public class MySwipeRefreshActivityV3 extends BaseActivity {

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.myRefreshLayoutV3)
    NonoRefreshLayout nonoRefreshLayout;

    @Override
    public void initView() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nonoRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_refresh_v3;
    }
}
