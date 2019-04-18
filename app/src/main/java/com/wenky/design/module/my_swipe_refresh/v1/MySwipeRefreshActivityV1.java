package com.wenky.design.module.my_swipe_refresh.v1;

import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.MotionEvent;
import android.view.View;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import butterknife.BindView;

public class MySwipeRefreshActivityV1 extends BaseActivity {

    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.swipeRefreshLayout)
    MySwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.scrollView)
    NestedScrollView nestedScrollView;

    @Override
    public void initView() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return swipeRefreshLayout.isRefreshing();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_swipe_v1;
    }
}
