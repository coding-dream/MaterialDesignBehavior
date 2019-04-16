package com.wenky.design.module.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class CommonFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<String> titles;
    private List<Fragment> fragments;

    public CommonFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public CommonFragmentPagerAdapter(FragmentManager fm, List<String> titles, List<Fragment> fragments) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }

    public void setData(List<String> titles, List<Fragment> fragments) {
        this.titles = titles;
        this.fragments = fragments;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments != null ? fragments.get(position) : null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles != null ? titles.get(position) : "";
    }

    @Override
    public int getCount() {
        return fragments != null ? fragments.size() : 0;
    }
}
