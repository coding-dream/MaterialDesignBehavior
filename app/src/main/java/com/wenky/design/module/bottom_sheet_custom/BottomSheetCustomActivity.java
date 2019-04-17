package com.wenky.design.module.bottom_sheet_custom;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import butterknife.BindView;

public class BottomSheetCustomActivity extends BaseActivity {
    @BindView(R.id.frame_layout)
    FrameLayout frame_layout;

    @Override
    public void initView() {
        final BottomSheetCustomBehavior behavior = BottomSheetCustomBehavior.from(frame_layout);
        behavior.setScrollLayoutCallback(new BottomSheetCustomBehavior.ScrollLayoutCallback() {
            @Override
            public void onStateChanged(@NonNull ViewGroup viewGroup, int oldState, int newState) {
                // 在这里的根据所切换的状态，可以控制其他视图的显示隐藏
            }

            @Override
            public void onSlide(@NonNull ViewGroup viewGroup, BottomSheetCustomBehavior behavior, int position) {
                // 这里通过位置的回调，可以设置一些渐变的变化，比如设置状态栏或标题栏的显示或隐藏
            }
        });
        frame_layout.post(new Runnable() {
            @Override
            public void run() {
                behavior.showCollapsed();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bottom_sheet_custom;
    }
}
