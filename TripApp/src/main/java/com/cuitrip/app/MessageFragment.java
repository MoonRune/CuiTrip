package com.cuitrip.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckedTextView;

import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseFragment;


public class MessageFragment extends BaseFragment {
    private static final String TAG = "MessageFragment";

    private View mContentView;
    private CheckedTextView mMessageFinder;
    private CheckedTextView mMessageTravel;
    private View mFinderMessageNew;
    private ViewPager mPager;
    private MessageFragmentPagerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
        }
    }

    public void onResume() {
        super.onResume();
        showActionBar(getString(R.string.ct_message));
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void validateLogin() {
        if (LoginInstance.isLogin(getActivity())) {
            showTabs();
        } else {
            hideTabs();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            validateLogin();
        }
    }

    public void hideTabs() {
        getView().findViewById(R.id.tabs).setVisibility(View.GONE);
    }

    public void showTabs() {
        getView().findViewById(R.id.tabs).setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.ct_message_fragment, container, false);
            mMessageFinder = (CheckedTextView) mContentView.findViewById(R.id.message_finder);
            mMessageTravel = (CheckedTextView) mContentView.findViewById(R.id.message_travel);
            mFinderMessageNew = mContentView.findViewById(R.id.message_new);
            mPager = (ViewPager) mContentView.findViewById(R.id.viewpager);
            mPager.addOnPageChangeListener(mOnPageChangeListener);
            mAdapter = new MessageFragmentPagerAdapter(getFragmentManager());
            mPager.setAdapter(mAdapter);

            mMessageFinder.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mPager.setCurrentItem(1);
                    mFinderMessageNew.setVisibility(View.GONE);
                }
            });

            mMessageTravel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mPager.setCurrentItem(0);
                }
            });
        } else {
            ViewParent parent = mContentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mContentView);
            }
        }
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validateLogin();
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                mMessageFinder.setChecked(false);
                mMessageTravel.setChecked(true);
            } else {
                mMessageFinder.setChecked(true);
                mFinderMessageNew.setVisibility(View.GONE);
                mMessageTravel.setChecked(false);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    class MessageFragmentPagerAdapter extends FragmentPagerAdapter {
        public MessageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return MessageListFragment.newInstance(UserInfo.USER_TRAVEL);
            } else {
                MessageListFragment fragment = MessageListFragment.newInstance(UserInfo.USER_FINDER);
                fragment.setOnNewMessageCallback(new MessageListFragment.OnNewMessageCallback() {

                    @Override
                    public void onNewMessage(boolean hasNew) {
                        if (hasNew && !mMessageFinder.isChecked()) {
                            mFinderMessageNew.setVisibility(View.VISIBLE);
                        }
                    }
                });
                return fragment;
            }
        }
    }

}
