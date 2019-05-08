package com.wenky.design.module.my_swipe_refresh.v2;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import org.jetbrains.anko.ToastsKt;

import butterknife.BindView;

public class MySwipeRefreshActivityV3 extends BaseActivity {

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.myRefreshLayoutV3)
    NonoRefreshLayout nonoRefreshLayout;
    @BindView(R.id.iv_animate)
    ImageView ivAnimate;

    private Handler mHandler = new Handler();

    @Override
    public void initView() {
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                nonoRefreshLayout.setRefreshing(true);
//            }
//        }, 500);

        nonoRefreshLayout.setOnRefreshListener(new NonoRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ToastsKt.toast(MySwipeRefreshActivityV3.this, "正在刷新数据~");
                requestData();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ivAnimate, "translationY", 0, 500);
                objectAnimator.setDuration(1500);
                objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                objectAnimator.start();
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
                            ToastsKt.toast(MySwipeRefreshActivityV3.this, "刷新完成~");
                        }
                    }
                }, 3000);
            }
        };
        thread.start();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_refresh_v3;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
