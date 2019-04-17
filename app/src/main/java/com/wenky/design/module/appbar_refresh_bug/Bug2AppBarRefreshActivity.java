package com.wenky.design.module.appbar_refresh_bug;

import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import butterknife.BindView;

/**
 * 已修复
 */
public class Bug2AppBarRefreshActivity extends BaseActivity {

    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

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
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_appbar_refresh_bug2;
    }
}
