package com.wenky.design.module.viewpager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wenky.design.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TitleFragment extends Fragment {

    private static final String ARGS_TITLE = "args_title";
    private String title;
    private Unbinder unbinder;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_title, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        parseArguments();
        initView();
    }

    private void initView() {
        tvTitle.setText(title);
    }

    private void parseArguments() {
        Bundle args = getArguments();
        if (args != null) {
            title = args.getString(ARGS_TITLE);
        }
    }

    public static TitleFragment newFragment(String title) {
        Bundle args = new Bundle();
        args.putString(ARGS_TITLE, title);
        TitleFragment fragment = new TitleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
