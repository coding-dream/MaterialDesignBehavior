package com.wenky.design.module.my_swipe_refresh.v2;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.Button;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import org.jetbrains.anko.ToastsKt;

import butterknife.BindView;

public class MySwipeRefreshActivityV4 extends BaseActivity {

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.myRefreshLayoutV3)
    NonoRefreshLayout nonoRefreshLayout;

    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    private boolean mCanFingerScrollDown;

    private Handler mHandler = new Handler();

    @Override
    public void initView() {
        // fixConflict1();
        fixConflict2();
//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                nonoRefreshLayout.setRefreshing(true);
//            }
//        }, 500);

        nonoRefreshLayout.setOnRefreshListener(new NonoRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ToastsKt.toast(MySwipeRefreshActivityV4.this, "正在刷新数据~");
                requestData();
            }
        });
    }

    private void requestData() {
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 注意：测试存在延时执行代码的一定要判空处理且 onDestroy时移除消息防止崩溃
                        if (nonoRefreshLayout != null) {
                            nonoRefreshLayout.setRefreshing(false);
                            ToastsKt.toast(MySwipeRefreshActivityV4.this, "刷新完成~");
                        }
                    }
                }, 3000);
            }
        };
        thread.start();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
