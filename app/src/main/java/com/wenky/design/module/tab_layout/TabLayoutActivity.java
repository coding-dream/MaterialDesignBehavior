package com.wenky.design.module.tab_layout;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

/**
 * 另类处理协调滑动的方式（非常赞）
 */
public class TabLayoutActivity extends BaseActivity {

    private TabLayout mTab;
    private LinearLayout mHeader;
    private ViewPager mVp;

    @Override
    public void initView() {
        mTab = (TabLayout) findViewById(R.id.tab);
        mHeader = (LinearLayout) findViewById(R.id.header);
        mVp = (ViewPager) findViewById(R.id.vp);
        mVp.setAdapter(new MyVpAdapter(this));
        mTab.setupWithViewPager(mVp);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tab_layout;
    }
}
