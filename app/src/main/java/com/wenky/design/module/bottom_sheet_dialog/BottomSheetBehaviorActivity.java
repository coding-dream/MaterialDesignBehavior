package com.wenky.design.module.bottom_sheet_dialog;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by wl on 2019/2/27.
 */
public class BottomSheetBehaviorActivity extends BaseActivity {

    @BindView(R.id.layout_bottom_sheet)
    View layoutBottomSheet;

    @Override
    public void initView() {
        final BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        // 监听BottomSheetBehavior 状态的变化
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        // 下滑的时候是否可以隐藏
        sheetBehavior.setHideable(true);
        // 设置折叠时的高度
        sheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        // sheetBehavior.setPeekHeight(200);

        findViewById(R.id.btn_show_bottom_sheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }

            }
        });
        findViewById(R.id.btn_show_bottom_sheet_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyBottomSheetDialogFragment.display(BottomSheetBehaviorActivity.this);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_behavior_bottom_sheet;
    }
}