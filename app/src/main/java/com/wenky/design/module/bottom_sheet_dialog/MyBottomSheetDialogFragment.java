package com.wenky.design.module.bottom_sheet_dialog;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.wenky.design.R;

/**
 * Created by wl on 2019/2/27.
 */
public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 自定义布局
        View rootView = inflater.inflate(R.layout.dialog_bottom_sheet, container, false);
        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 解决dialog黑屏问题
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        return dialog;
    }

    public static void display(AppCompatActivity context) {
        MyBottomSheetDialogFragment dialogFragment = new MyBottomSheetDialogFragment();
        dialogFragment.show(context.getSupportFragmentManager(), MyBottomSheetDialogFragment.class.getSimpleName());
    }
}