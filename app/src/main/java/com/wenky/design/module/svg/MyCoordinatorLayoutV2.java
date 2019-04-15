package com.wenky.design.module.svg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.wenky.design.R;

import org.jetbrains.anko.ToastsKt;
import org.jetbrains.annotations.NotNull;

/**
 * Created by wl on 2019/2/13.
 */
public class MyCoordinatorLayoutV2 extends CoordinatorLayout {

    private Context mContext;
    private int maxScrollLength = 0;

    private SVGAImageView svgaImageView;
    private RefreshLoadingView refreshLoadingView;
    private RecyclerView recyclerView;

    private CollapsingToolbarLayoutState state;
    private ContentRefreshBehavior contentRefreshBehavior;
    private boolean isRefreshing;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    public MyCoordinatorLayoutV2(@NonNull Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public MyCoordinatorLayoutV2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public MyCoordinatorLayoutV2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        inflate(mContext, R.layout.layout_refresh_v2, this);
        refreshLoadingView = findViewById(R.id.refreshLoadingView);
        svgaImageView = findViewById(R.id.svg_loading_refresh);
    }

    /**
     * 播放动画
     */
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
                ToastsKt.toast(mContext, "animator finished");
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
        CoordinatorLayout.LayoutParams lp = (LayoutParams) recyclerView.getLayoutParams();
        contentRefreshBehavior = (ContentRefreshBehavior) lp.getBehavior();
        contentRefreshBehavior.setCollapsingLayoutStateCallback(new ContentRefreshBehavior.CollapsingLayoutStateCallback() {
            @Override
            public void expanded() {
                requestData();
            }

            @Override
            public void collapsed() {

            }

            @Override
            public void internediate() {

            }
        });
    }

    /**
     * 模拟网络请求，请求成功后结束动画，并隐藏动画View
     */
    private void requestData() {
        setRefresh(true);
        refreshLoadingView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefresh(false);
            }
        }, 5000);
    }

    public void setRefresh(boolean showRefresh){
        if (showRefresh) {
            refreshLoadingView.setCanRotate(true);
            refreshLoadingView.postInvalidate();
            playLoadingView();
            contentRefreshBehavior.setEnable(false);
        } else {
            refreshLoadingView.setCanRotate(false);
            refreshLoadingView.postInvalidate();
            stopLoadingView();
            contentRefreshBehavior.hideRefreshView();
            contentRefreshBehavior.setEnable(true);
        }
        isRefreshing = showRefresh;
    }

    private void stopLoadingView() {
        svgaImageView.stopAnimation();
    }
}