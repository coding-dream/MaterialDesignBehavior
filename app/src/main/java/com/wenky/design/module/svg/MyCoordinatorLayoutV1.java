package com.wenky.design.module.svg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.wenky.design.R;
import com.wenky.design.util.LogHelper;

import org.jetbrains.anko.ToastsKt;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Created by wl on 2019/2/13.
 */
public class MyCoordinatorLayoutV1 extends CoordinatorLayout {

    private Context mContext;
    private int maxScrollLength = 0;

    private AppBarLayout appBarLayout;
    private SVGAImageView svgaImageView;
    private ImageView ivRefresh;

    private boolean isRefreshing;

    private CollapsingToolbarLayoutState state;
    private RecyclerView recyclerView;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    public MyCoordinatorLayoutV1(@NonNull Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public MyCoordinatorLayoutV1(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public MyCoordinatorLayoutV1(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        inflate(mContext, R.layout.layout_refresh, this);
        ivRefresh = findViewById(R.id.iv_refresh);
        svgaImageView = findViewById(R.id.svg_loading_refresh);
        appBarLayout = findViewById(R.id.appBarLayout);

        appBarLayout.setExpanded(false);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float degree = Math.abs(verticalOffset) / (float)appBarLayout.getTotalScrollRange();
                LogHelper.d("verticalOffset: " + verticalOffset + " " + "degree: " + degree);
                ivRefresh.setRotation(360 * (1 - degree));

                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;
                        LogHelper.d("state = CollapsingToolbarLayoutState.EXPANDED");
                        handleAppLayoutState();
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        state = CollapsingToolbarLayoutState.COLLAPSED;
                        LogHelper.d("state = CollapsingToolbarLayoutState.COLLAPSED");
                        handleAppLayoutState();
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;
                    }
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                if (state == CollapsingToolbarLayoutState.INTERNEDIATE) {
                    appBarLayout.setExpanded(false, true);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void handleAppLayoutState() {
        switch (state) {
            case EXPANDED:
                requestData();
                break;
            case COLLAPSED:

                break;
            case INTERNEDIATE:
                break;
            default:
                break;
        }
    }

    /**
     * 模拟网络请求，请求成功后结束动画，并隐藏动画View
     */
    private void requestData() {
        setRefresh(true);
        ivRefresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefresh(false);
            }
        }, 2000);
    }

    public void setRefresh(boolean showRefresh){
        ToastsKt.toast(mContext, "showRefresh " +showRefresh + " " + new Random().nextInt(100));
        if (showRefresh) {
            playLoadingView();
            ivRefresh.setVisibility(View.GONE);
        } else {
            ivRefresh.setVisibility(View.VISIBLE);
            stopLoadingView();
            appBarLayout.setExpanded(false, true);
        }
        isRefreshing = showRefresh;
        LogHelper.d("isRefreshing: " + isRefreshing);
    }

    private void playLoadingView() {
        if (svgaImageView.isAnimating()) {
            return;
        }
        SVGAParser svgaParser = new SVGAParser(mContext);
        svgaImageView.setLoops(5);
        svgaImageView.setCallback(new SVGACallback() {

            @Override
            public void onPause() {

            }

            @Override
            public void onFinished() {
                // 动画播放完成

            }

            @Override
            public void onRepeat() {

            }

            @Override
            public void onStep(int i, double v) {

            }
        });

        svgaParser.parse("refresh_loading.svga", new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity svgaVideoEntity) {
                svgaImageView.setVisibility(View.VISIBLE);
                svgaImageView.setVideoItem(svgaVideoEntity);
                svgaImageView.startAnimation();
            }

            @Override
            public void onError() {

            }
        });
    }

    private void stopLoadingView() {
        svgaImageView.stopAnimation();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 因为RecycleView是后面添加的，必须放在此处
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isRefreshing;
            }
        });
    }
}