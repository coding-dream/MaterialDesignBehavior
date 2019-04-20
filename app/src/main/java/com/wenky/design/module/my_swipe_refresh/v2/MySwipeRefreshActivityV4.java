package com.wenky.design.module.my_swipe_refresh.v2;

import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.Button;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import butterknife.BindView;

public class MySwipeRefreshActivityV4 extends BaseActivity {

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.myRefreshLayoutV3)
    NonoRefreshLayout nonoRefreshLayout;

    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    private boolean mCanFingerScrollDown;

    @Override
    public void initView() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nonoRefreshLayout.setRefreshing(false);
            }
        });

        fixConflict1();
        fixConflict2();
    }

    private void fixConflict1() {
        // 方式一：解决非Nested进制下（AppBarLayout）与MyRefreshLayoutV3滑动冲突的方式一（就是不拦截 onInterceptTouchEvent）
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    nonoRefreshLayout.setEnabled(true);
                } else {
                    nonoRefreshLayout.setEnabled(false);
                }
            }
        });
    }

    private void fixConflict2() {
        // 方式二：完美修复法
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    // AppBarLayout已经完全显示，不能继续下拉了，触发下拉刷新。
                    mCanFingerScrollDown = false;
                } else {
                    // AppLayout隐藏状态或未完全显示（手指还可以继续向下滑动，那么就不能下拉刷新）
                    mCanFingerScrollDown = true;
                }
            }
        });
        nonoRefreshLayout.setOnFingerDownCallback(new NonoRefreshLayout.OnCanFingerDownCallback() {
            @Override
            public boolean canFingerScrollDown(NonoRefreshLayout parent, @Nullable View child) {
                return mCanFingerScrollDown;
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_refresh_v4;
    }
}
