package com.wenky.design.module.snakebar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
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

    /**
     * 可省（不需要）
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Work around our backwards-compatible refactoring of Snackbar and inner content
        // being inflated against snackbar's parent (instead of against the snackbar itself).
        // Every child that is width=MATCH_PARENT is remeasured again and given the full width
        // minus the paddings.
        int childCount = getChildCount();
        int availableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT) {
                child.measure(MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(),
                                MeasureSpec.EXACTLY));
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CustomSnakeBar.enableSnakeBar = true;
    }
}