package com.lab.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.cuitrip.service.R;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.R.attr;

/**
 * Created by baziii on 15/8/20.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public class IconPageIndicator extends HorizontalScrollView implements PageIndicator {
    private final IcsLinearLayout mIconsLayout;
    private ViewPager mViewPager;
    private OnPageChangeListener mListener;
    private Runnable mIconSelector;
    private int mSelectedIndex;

    public IconPageIndicator(Context context) {
        this(context, (AttributeSet) null);
    }

    public IconPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setHorizontalScrollBarEnabled(false);
        this.mIconsLayout = new IcsLinearLayout(context, attr.vpiIconPageIndicatorStyle);
        this.addView(this.mIconsLayout, new LayoutParams(-2, -1, 17));
    }

    private void animateToIcon(int position) {
        final View iconView = this.mIconsLayout.getChildAt(position);
        if (this.mIconSelector != null) {
            this.removeCallbacks(this.mIconSelector);
        }

        this.mIconSelector = new Runnable() {
            public void run() {
                int scrollPos = iconView.getLeft() - (IconPageIndicator.this.getWidth() - iconView.getWidth()) / 2;
                IconPageIndicator.this.smoothScrollTo(scrollPos, 0);
                IconPageIndicator.this.mIconSelector = null;
            }
        };
        this.post(this.mIconSelector);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mIconSelector != null) {
            this.post(this.mIconSelector);
        }

    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mIconSelector != null) {
            this.removeCallbacks(this.mIconSelector);
        }

    }

    public void onPageScrollStateChanged(int arg0) {
        if (this.mListener != null) {
            this.mListener.onPageScrollStateChanged(arg0);
        }

    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (this.mListener != null) {
            this.mListener.onPageScrolled(arg0, arg1, arg2);
        }

    }

    public void onPageSelected(int arg0) {
        this.setCurrentItem(arg0);
        if (this.mListener != null) {
            this.mListener.onPageSelected(arg0);
        }

    }

    public void setViewPager(ViewPager view) {
        if (this.mViewPager != view) {
            if (this.mViewPager != null) {
                this.mViewPager.setOnPageChangeListener((OnPageChangeListener) null);
            }

            PagerAdapter adapter = view.getAdapter();
            if (adapter == null) {
                throw new IllegalStateException("ViewPager does not have adapter instance.");
            } else {
                this.mViewPager = view;
                view.setOnPageChangeListener(this);
                this.notifyDataSetChanged();
            }
        }
    }

    public void setViewPager(ViewPager view, int initialPosition) {
        this.setViewPager(view);
        this.setCurrentItem(initialPosition);
    }

    public void setCurrentItem(int item) {
        if (this.mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        } else {
            this.mSelectedIndex = item;
            this.mViewPager.setCurrentItem(item);
            int tabCount = this.mIconsLayout.getChildCount();

            for (int i = 0; i < tabCount; ++i) {
                View child = this.mIconsLayout.getChildAt(i);
                boolean isSelected = i == item;
                child.setSelected(isSelected);
                if (isSelected) {
                    this.animateToIcon(item);
                }
            }

        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mListener = listener;
    }

    public void notifyDataSetChanged() {
        mIconsLayout.removeAllViews();
        IconPagerAdapter iconAdapter = (IconPagerAdapter) mViewPager.getAdapter();
        int count = iconAdapter.getCount();
        for (int i = 0; i < count; i++) {
            ImageView view = new ImageView(getContext(), null, R.attr.vpiIconPageIndicatorStyle);
            view.setImageResource(iconAdapter.getIconResId(i));
            view.setTag("" + i);
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    int viewPosition = Integer.parseInt(v.getTag().toString());

                    mViewPager.setCurrentItem(viewPosition);
                }
            });
            mIconsLayout.addView(view);
        }
        if (mSelectedIndex > count) {
            mSelectedIndex = count - 1;
        }
        setCurrentItem(mSelectedIndex);
        requestLayout();
    }
}

