package com.wenky.design.module.snakebar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wenky.design.R;
import com.wenky.design.util.LogHelper;

/**
 * Created by wl on 2019/4/30.
 */
public class SnackBarChildView extends FrameLayout {

    private TextView mMessageView;
    private Button mActionView;
    private CustomSnakeBar mSnakeBar;

    public SnackBarChildView(Context context) {
        this(context, null);
    }

    public SnackBarChildView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.include_design_layout_snackbar, this);
        mMessageView = findViewById(R.id.snackbar_text);
        mActionView = findViewById(R.id.snackbar_action);
        mActionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSnakeBar.hide();
            }
        });
    }

    public void animateContentIn(int delay, int duration) {
        if (mMessageView == null || mActionView == null) {
            return;
        }
        LogHelper.d("animateContentIn");
        mMessageView.setAlpha(0f);
        mMessageView.animate().alpha(1f).setDuration(duration)
                .setStartDelay(delay).start();

        if (mActionView.getVisibility() == VISIBLE) {
            mActionView.setAlpha(0f);
            mActionView.animate().alpha(1f).setDuration(duration)
                    .setStartDelay(delay).start();
        }
    }

    public void animateContentOut(int delay, int duration) {
        if (mMessageView == null || mActionView == null) {
            return;
        }
        mMessageView.setAlpha(1f);
        mMessageView.animate().alpha(0f).setDuration(duration)
                .setStartDelay(delay).start();

        if (mActionView.getVisibility() == VISIBLE) {
            mActionView.setAlpha(1f);
            mActionView.animate().alpha(0f).setDuration(duration)
                    .setStartDelay(delay).start();
        }
    }

    public void setSnakeBar(CustomSnakeBar snakeBar) {
        this.mSnakeBar = snakeBar;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CustomSnakeBar.enableSnakeBar = true;
    }
}