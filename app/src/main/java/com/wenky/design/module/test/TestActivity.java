package com.wenky.design.module.test;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import butterknife.BindView;

public class TestActivity extends BaseActivity {

    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.tv_message)
    AppCompatTextView tvMessage;
    @BindView(R.id.button)
    Button button;

    @Override
    public void initView() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvMessage.setText(etInput.getText());
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_test;
    }
}