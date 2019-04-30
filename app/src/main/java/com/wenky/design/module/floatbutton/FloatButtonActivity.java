package com.wenky.design.module.floatbutton;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by wl on 2019/4/30.
 *
 * onDependentViewChanged 依赖滑动案例
 */
public class FloatButtonActivity extends BaseActivity {

    @BindView(R.id.button)
    Button button;

    @Override
    public void initView() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(button, "弹起来吧", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_float_button;
    }
}
