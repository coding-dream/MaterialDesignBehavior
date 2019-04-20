package com.wenky.design.module.my_swipe_refresh.v2;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.wenky.design.R;

import org.jetbrains.annotations.NotNull;

public class NonoRefreshView extends FrameLayout {

    private final Context mContext;
    private View ivRefresh;
    private SVGAImageView svgaImageView;

    public NonoRefreshView(Context context) {
        super(context);
        this.mContext = context;
        initView(context);
    }

    public NonoRefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.layout_refresh_view, this);
        ivRefresh = findViewById(R.id.iv_refresh);
        svgaImageView = findViewById(R.id.svg_loading_refresh);
    }

    /**
     * 播放动画
     */
    public void playLoadingView() {
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

        svgaParser.parse("loading.svga", new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity svgaVideoEntity) {
                svgaImageView.setVisibility(View.VISIBLE);
                ivRefresh.setVisibility(View.INVISIBLE);
                svgaImageView.setVideoItem(svgaVideoEntity);
                svgaImageView.startAnimation();
            }

            @Override
            public void onError() {

            }
        });
    }

    public void stopLoadingView() {
        svgaImageView.stopAnimation();
        ivRefresh.setVisibility(View.VISIBLE);
        svgaImageView.setVisibility(View.INVISIBLE);
    }
}
