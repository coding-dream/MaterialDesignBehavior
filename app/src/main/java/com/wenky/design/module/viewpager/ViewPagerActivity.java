package com.wenky.design.module.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.wenky.design.R;
import com.wenky.design.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * NestedScrolling View可以在CoordinatorLayout的任意层级的布局中（不一定必须是CoordinatorLayout的直接子View）
 *
 * 如：NestedScrollingChildHelper.startNestedScroll(int, int)
 *
 * ViewParent p = mView.getParent();
 * View child = mView;
 * while (p != null) {
 *     if (ViewParentCompat.onStartNestedScroll(p, child, mView, axes, type)) {
 *         // 设置NestedScrollingParent2（CoordinatorLayout），只设置一次，后面获取用的就是此处的p
 *         setNestedScrollingParentForType(type, p);
 *         // while遍历 最终找到后，此处的p = CoordinatorLayout，child = ViewPager，mView = NestedScrollView
 *         ViewParentCompat.onNestedScrollAccepted(p, child, mView, axes, type);
 *         return true;
 *     }
 *     if (p instanceof View) {
 *         // 每遍历一次，重新设置当前的child
 *         child = (View) p;
 *     }
 *     // 循环遍历直到找到最上层的CoordinatorLayout
 *     p = p.getParent();
 * }
 *
 * 设置NestedScrollingParent2（CoordinatorLayout）
 *
 * 获取NestedScrollingParent2（CoordinatorLayout）
 * final ViewParent parent = getNestedScrollingParentForType(type);
 */
public class ViewPagerActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    public void initView() {
        List<String> titles = new ArrayList<>();
        List<Fragment> fragments = new ArrayList<>();
        titles.add("标题一");
        titles.add("标题二");

        fragments.add(TitleFragment.newFragment("内容一"));
        fragments.add(TitleFragment.newFragment("内容二"));

        viewPager.setAdapter(new CommonFragmentPagerAdapter(getSupportFragmentManager(), titles, fragments));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_view_pager;
    }
}