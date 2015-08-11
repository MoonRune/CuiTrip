package com.cuitrip.app.favorite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by baziii on 15/8/11.
 */
public class FavorityUnvaliableActivity extends BaseActivity {

    public static void start(Context context){
        context.startActivity(new Intent(context,FavorityUnvaliableActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_favorite_unvaliable);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.ct_confirm_tv)
    public void onConfirm() {
        finish();
    }

}
