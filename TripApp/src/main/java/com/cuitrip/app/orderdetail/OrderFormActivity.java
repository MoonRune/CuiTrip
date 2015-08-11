package com.cuitrip.app.orderdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.cuitrip.app.OrderFragment;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/11.
 */
public class OrderFormActivity extends BaseActivity {

    public static final String TAG="OrderFormActivity";
    public static final String ORDER_ID="OrderFormActivity.ORDER_ID";
    @InjectView(R.id.ct_view_pager_indicator)
    IconPageIndicator mViewPagerIndicator;
    @InjectView(R.id.ct_view_pager)
    ViewPager mViewPager;
    protected static final String[] CONTENT = new String[] { "This", "Is", "A",  };

    public static void start(Context context,String orderId){
        Intent intent = new Intent(context,OrderFormActivity.class);
        intent.putExtra(ORDER_ID,orderId);
        context.startActivity(intent);
    }

    public class OrderViewsAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {
        public OrderViewsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getIconResId(int i) {
            switch (i) {
                case 0:
                    return R.drawable.ct_mennsage;
                case 1:
                    return R.drawable.ct_order;
                default:
                    return R.drawable.ct_my;
            }

        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return OrderFragment.newInstance(OrderFragment.TYPE_FINDER);
                case 1:
                    return OrderFragment.newInstance(OrderFragment.TYPE_TRAVEL);
                default:
                    return OrderFragment.newInstance(OrderFragment.TYPE_FINDER);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_order_form);
        ButterKnife.inject(this);
        mViewPager.setAdapter(new OrderViewsAdapter(getSupportFragmentManager())) ;
        mViewPagerIndicator.setViewPager(mViewPager);

    }
}
