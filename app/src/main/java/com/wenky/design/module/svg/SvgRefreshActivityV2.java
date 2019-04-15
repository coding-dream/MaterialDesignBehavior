package com.wenky.design.module.svg;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SvgRefreshActivityV2 extends BaseActivity {

    @BindView(R.id.myCoordinatorLayout)
    MyCoordinatorLayoutV2 myCoordinatorLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<String> mDatas;

    @Override
    public void initView() {
        testData();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        BaseQuickAdapter baseQuickAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_img_txt, mDatas) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_name, item);
            }
        };
        recyclerView.setAdapter(baseQuickAdapter);
        baseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
    }

    private void testData() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i <= 20; i++) {
            datas.add("menu: " + i);
        }
        mDatas = datas;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_svga_v2;
    }
}
