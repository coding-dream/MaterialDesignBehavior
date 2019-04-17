package com.wenky.design.module.svg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
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

/**
 * Created by wl on 2019/2/13.
 */
public class MyCoordinatorLayoutV2 extends CoordinatorLayout implements NestedScrollingChild {

    private Context mContext;
    private int maxScrollLength = 0;

    private SVGAImageView svgaImageView;
    private ImageView ivRefresh;
    private View scrollView;

    private CollapsingToolbarLayoutState state;
    private ContentRefreshBehavior contentRefreshBehavior;
    private boolean isRefreshing;

    private NestedScrollingChildHelper mScrollingChildHelper;

    private final int[] mScrollConsumed = new int[2];

    private final int[] mScrollOffset = new int[2];

    private boolean canScrollHorizontally = false;

    private boolean canScrollVertically = true;

    private int mLastY;

    /**
     * =================================== 依次重写NestedScrollingChild 的9个接口 ===================================
     * setNestedScrollingEnabled
     * isNestedScrollingEnabled
     * startNestedScroll
     * stopNestedScroll
     * hasNestedScrollingParent
     * dispatchNestedScroll
     * dispatchNestedPreScroll
     * dispatchNestedFling
     * dispatchNestedPreFling
     * =================================== 依次重写NestedScrollingChild 的9个接口 ===================================
     */
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        getScrollingChildHelper().setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return getScrollingChildHelper().isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return getScrollingChildHelper().startNestedScroll(axes);
    }
    @Override
    public void stopNestedScroll() {
        getScrollingChildHelper().stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return getScrollingChildHelper().hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return getScrollingChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }
    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return getScrollingChildHelper().dispatchNestedFling(velocityX, velocityY, consumed);
    }
    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return getScrollingChildHelper().dispatchNestedPreFling(velocityX, velocityY);
    }
    // =====================================================================================

    /**
     * 入口点: 注意使用event.getRawY() 而不是 event.getY();
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) event.getRawY();

                // 嵌套滑动的轴: 1. ViewCompat.SCROLL_AXIS_NONE 都支持 2. ViewCompat.SCROLL_AXIS_HORIZONTAL 仅支持水平 3. ViewCompat.SCROLL_AXIS_VERTICAL 仅支持垂直
                int nestedScrollAxis = ViewCompat.SCROLL_AXIS_NONE;
                if (canScrollHorizontally) {
                    nestedScrollAxis |= ViewCompat.SCROLL_AXIS_HORIZONTAL;
                }
                if (canScrollVertically) {
                    nestedScrollAxis |= ViewCompat.SCROLL_AXIS_VERTICAL;
                }
                startNestedScroll(nestedScrollAxis);
                break;
            case MotionEvent.ACTION_MOVE:
                int currentY = (int) event.getRawY();
                // 因为RecycleView等组件传入的deltay 正值表示向上移动,负值表示向下移动,所以这里也统一
                int deltaY = -(currentY - mLastY);
                mLastY = currentY;

                int remainY = deltaY;
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    remainY = deltaY - mScrollConsumed[1];
                }
                // 剩下的自己消费 或者 消耗一部分, 剩下的再交给 NestedParent处理: dispatchNestedScroll()
                scrollBy(0, remainY);
                // 源码: dispatchNestedPreScroll:
                // 解释下这里的mScrollConsumed 和 mScrollOffset 均是一个初始化的数组[0,0].
                // 而mScrollOffset是在调用dispatchNestedPreScroll内通过 Helper对象帮助我们设置的.
                // 通过在NestedScrollParent.onNestedPreScroll 调用前后获取两次 mView.getLocationInWindow(offsetInWindow) 的差值 设置(不需要我们设置)
                // NestedScrollingChildHelper中的mView就是Helper对象初始化时候指定的. mScrollingChildHelper = new NestedScrollingChildHelper(this)
                // LogUtils.d("mScrollOffset: " + mScrollOffset[0] + " " + mScrollOffset[1]);
                // 通过打印 mScrollOffset 的值: 每次都是 +1 -1 等结果, 此参数的意义不大.
                break;
            case MotionEvent.ACTION_UP:
                stopNestedScroll();
                break;
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                // 为了程序简单,未处理 Fling, 可以参考:
                // http://hanks.pub/2016/08/31/nestedscrollchild/
                // https://github.com/hanks-zyh
                break;
            default:
                break;
        }
        return true;
    }

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
        mScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        initView();
    }

    private void initView() {
        inflate(mContext, R.layout.layout_refresh_v2, this);
        ivRefresh = findViewById(R.id.iv_refresh);
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


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 因为RecycleView是后面添加的，必须放在此处
        scrollView = findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isRefreshing;
            }
        });
        CoordinatorLayout.LayoutParams lp = (LayoutParams) scrollView.getLayoutParams();
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
        ivRefresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefresh(false);
            }
        }, 4000);
    }

    public void setRefresh(boolean showRefresh){
        if (showRefresh) {
            playLoadingView();
            ToastsKt.toast(mContext, "开始刷新");
        } else {
            ivRefresh.setVisibility(View.VISIBLE);
            svgaImageView.setVisibility(View.INVISIBLE);
            stopLoadingView();
            contentRefreshBehavior.hideRefreshView();
            ToastsKt.toast(mContext, "刷新成功");
        }
        isRefreshing = showRefresh;
        LogHelper.d("setRefresh: " + showRefresh);
    }

    private void stopLoadingView() {
        svgaImageView.stopAnimation();
    }

    private NestedScrollingChildHelper getScrollingChildHelper() {
        if (mScrollingChildHelper == null) {
            mScrollingChildHelper = new NestedScrollingChildHelper(this);
        }
        return mScrollingChildHelper;
    }
}