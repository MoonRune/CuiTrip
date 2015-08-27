package com.cuitrip.app.favorite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.ImageHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by baziii on 15/8/11.
 */
public class FavorityUnvaliableActivity extends BaseActivity {

    public static final String IMAGE = "IMAGE";
    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.ct_confirm_tv)
    TextView ctConfirmTv;

    public static void start(Context context, String iamge) {
        context.startActivity(new Intent(context, FavorityUnvaliableActivity.class)
                .putExtra(IMAGE, iamge));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_favorite_unvaliable);
        ButterKnife.inject(this);
        ImageHelper.displayCtImage(getIntent().getStringExtra(IMAGE), image, null);
    }

    @OnClick(R.id.ct_confirm_tv)
    public void onConfirm() {
        finish();
    }

}
