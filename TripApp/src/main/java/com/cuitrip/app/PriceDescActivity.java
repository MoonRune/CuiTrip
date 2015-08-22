package com.cuitrip.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.MessageUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/13.
 */
public class PriceDescActivity extends BaseActivity {
    public static final int REQUEST_CODE = 1;
    public static final String INCLUDE_KEY = "PriceDescActivity.INCLUDE_KEY";
    public static final String UNINCLUDE_KEY = "PriceDescActivity.UNINCLUDE_KEY";
    public static final String IS_FINDER = "PriceDescActivity.IS_FINDER";
    @InjectView(R.id.ct_price_include_et)
    EditText ctPriceIncludeEt;
    @InjectView(R.id.ct_price_uninclude_et)
    EditText ctPriceUnincludeEt;

    public static void start(Activity activity, String include, String uninclude,boolean isFinder) {
        Intent intent = new Intent(activity, PriceDescActivity.class).putExtra(INCLUDE_KEY, include).putExtra(UNINCLUDE_KEY, uninclude)
                .putExtra(IS_FINDER, isFinder);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static boolean isModifyed(int requestCode, int resultCode, Intent data) {
        return (requestCode == REQUEST_CODE && resultCode == RESULT_OK);
    }

    public static String getInclude(Intent intent) {
        return intent.getStringExtra(INCLUDE_KEY);
    }

    public static String getUninclude(Intent intent) {
        return intent.getStringExtra(UNINCLUDE_KEY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(getString(R.string.ct_price_desc_title));
        setContentView(R.layout.ct_price_desc);
        ButterKnife.inject(this);
        ctPriceIncludeEt.setText(getInclude(getIntent()));
        ctPriceUnincludeEt.setText(getUninclude(getIntent()));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_OK:
                trySetValue();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void disableAll() {
        ctPriceIncludeEt.setEnabled(false);
        ctPriceUnincludeEt.setEnabled(false);

    }

    public void enableAll() {
        ctPriceIncludeEt.setEnabled(true);
        ctPriceUnincludeEt.setEnabled(true);
    }

    public void trySetValue() {
        if (!ctPriceIncludeEt.isEnabled()) {
            return;
        }
        if (TextUtils.isEmpty(ctPriceIncludeEt.getText().toString())) {
            MessageUtils.showToast(getString(R.string.ct_order_price_include_empty_msg));
            return;
        }
        if (TextUtils.isEmpty(ctPriceUnincludeEt.getText().toString())) {
            MessageUtils.showToast(getString(R.string.ct_order_price_uninclude_empty_msg));
            return;
        }
        disableAll();
        passValue();
        finish();
    }

    public void passValue() {
        Intent intent = new Intent();
        intent.putExtra(INCLUDE_KEY, ctPriceIncludeEt.getText().toString());
        intent.putExtra(UNINCLUDE_KEY, ctPriceUnincludeEt.getText().toString());
        setResult(RESULT_OK, intent);
    }


}
