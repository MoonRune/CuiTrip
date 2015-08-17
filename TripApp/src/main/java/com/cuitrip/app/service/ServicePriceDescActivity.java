package com.cuitrip.app.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.cuitrip.model.ServiceInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.MessageUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/17.
 */
public class ServicePriceDescActivity extends BaseActivity {
    public static final String PRICE_TYPE_KEY = "PRICE_TYPE_KEY";
    public static final String PRICE_INCLUDE_KEY = "PRICE_INCLUDE_KEY";
    public static final String PRICE_UNINCLUDE_KEY = "PRICE_UNCLUDE_KEY";
    @InjectView(R.id.ct_price_type_tv)
    TextView ctPriceTypeTv;
    @InjectView(R.id.ct_price_type_desc_tv)
    TextView ctPriceTypeDescTv;
    @InjectView(R.id.ct_price_include_tv)
    TextView ctPriceIncludeTv;
    @InjectView(R.id.ct_price_uninclude_tv)
    TextView ctPriceUnincludeTv;

    public static void start(Context context, int priceType,String include ,String uninclude){
        context.startActivity(new Intent(context,ServicePriceDescActivity.class).putExtra(PRICE_TYPE_KEY,priceType)
        .putExtra(PRICE_INCLUDE_KEY,include)
        .putExtra(PRICE_UNINCLUDE_KEY,uninclude));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_service_price_desc);
        showActionBar(getString(R.string.ct_price_desc_title));
        ButterKnife.inject(this);
        int priceType = getIntent().getIntExtra(PRICE_TYPE_KEY, -1);
        switch (priceType){
            case ServiceInfo.PAYWAY_ALL:
                ctPriceTypeTv.setText(getString(R.string.ct_service_pay_way)+":"+getString(R.string.price_way_all));
                ctPriceTypeDescTv.setText(getString(R.string.ct_price_payway_all_detail_msg));
                break;
            case ServiceInfo.PAYWAY_PER:
                ctPriceTypeTv.setText(getString(R.string.ct_service_pay_way)+":"+getString(R.string.price_way_per));
                ctPriceTypeDescTv.setText(getString(R.string.ct_price_payway_per_detail_msg));
                break;
            case ServiceInfo.PAYWAY_FREE:
                ctPriceTypeTv.setText(getString(R.string.ct_service_pay_way)+":"+getString(R.string.price_way_free));
                ctPriceTypeDescTv.setText(getString(R.string.ct_price_payway_free_detail_msg));
                break;
            default:
                MessageUtils.showToast(getString(R.string.data_error));
        }
        ctPriceIncludeTv.setText(getIntent().getStringExtra(PRICE_INCLUDE_KEY));
        ctPriceUnincludeTv.setText(getIntent().getStringExtra(PRICE_UNINCLUDE_KEY));
    }
}
