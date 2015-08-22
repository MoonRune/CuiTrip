package com.cuitrip.app.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuitrip.service.R;
import com.lab.app.BaseFragment;

/**
 * Created by baziii on 15/8/22.
 */
public class ProgressingFragment extends BaseFragment {
    public static ProgressingFragment newInstance() {

        Bundle args = new Bundle();

        ProgressingFragment fragment = new ProgressingFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_progressing,null);
        return view;
    }
}
