package com.wenky.design.module.snakebar;

import android.support.design.widget.Snackbar;
import android.view.View;
import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;
import butterknife.OnClick;

/**
 * Created by wl on 2019/5/5.
 */
public class CustomSnackBarActivity extends BaseActivity {

    @Override
    public void initView() {

    }

    @OnClick({R.id.snake_bar_raw, R.id.snake_bar_custom})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.snake_bar_raw:
                Snackbar.make(view, "原生SnakeBar", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.snake_bar_custom:
                CustomSnakeBar snakeBar = CustomSnakeBar.make(view, "自定义SnakeBar");
                snakeBar.show();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_snake_bar;
    }
}
