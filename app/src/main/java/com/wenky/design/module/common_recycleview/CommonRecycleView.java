package com.wenky.design.module.common_recycleview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wenky.design.R;

import java.util.ArrayList;
import java.util.List;

public class CommonRecycleView extends RecyclerView {

    public CommonRecycleView(Context context) {
        super(context);
        initView();
    }

    public CommonRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CommonRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        setLayoutManager(new LinearLayoutManager(getContext()));
        setItemLayout(R.layout.item_simple);
    }

    public void setItemLayout(int layoutId) {
        if (layoutId == 0) {
            layoutId = R.layout.item_simple;
        }
        BaseQuickAdapter<String, BaseViewHolder> baseQuickAdapter = new BaseQuickAdapter<String, BaseViewHolder>(layoutId) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_name, item);
            }
        };
        setAdapter(baseQuickAdapter);
        baseQuickAdapter.setNewData(mockData());
    }

    public List<String> mockData() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i <= 20; i++) {
            datas.add("item: " + i);
        }
        return datas;
    }
}
