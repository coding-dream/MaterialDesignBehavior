package com.wenky.design.module.my_swipe_refresh.v3;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by wl on 2019/6/27.
 *
 * 修复：首项item无高度下拉刷新失效问题
 */
public class NonoRefreshActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private View headerView;

    private Handler handler = new Handler();
    private BaseQuickAdapter<String, BaseViewHolder> headerBaseQuickAdapter;
    private BaseQuickAdapter<String, BaseViewHolder> baseQuickAdapter;

    @Override
    public void initView() {
        initHeaderView();

        baseQuickAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_img_txt) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_name, item);
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(baseQuickAdapter);

        // 罪魁祸首：同时设置了EmptyView + HeaderView，然后HeaderView开始高度为0（无内容），最终导致 mTarget.canScrollVertically(-1) 判断有误不能下拉刷新。
        // 修改HeaderView的最小高度为1dp即可解决，暂时不追究细节原因。
        baseQuickAdapter.setHeaderAndEmpty(true);
        baseQuickAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.nn_common_empty, null));

        baseQuickAdapter.addHeaderView(headerView);


        delayLoadData();
    }

    private void delayLoadData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> datas1 = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    datas1.add("header: " + i);
                }
                headerBaseQuickAdapter.setNewData(datas1);

                // ===============================

                List<String> datas2 = new ArrayList<>();
                for (int i = 0; i <= 20; i++) {
                    datas2.add("content: " + i);
                }
                baseQuickAdapter.setNewData(datas2);
            }
        }, 5000);
    }

    private void initHeaderView() {
        headerView = LayoutInflater.from(this).inflate(R.layout.nn_refresh_header, null, false);
        RecyclerView headerRv = headerView.findViewById(R.id.recyclerView);
        headerBaseQuickAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_img_txt) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_name, item);
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        headerRv.setLayoutManager(layoutManager);
        headerRv.setAdapter(headerBaseQuickAdapter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_nono_refresh;
    }
}
