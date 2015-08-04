/*
 * Copyright 2014 Napa Tech. All rights reserved.
 */
package com.umeng_social_sdk_res_lib;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by bqf on 5/28/14.
 */
public class SharePanelPopupWindow extends PopupWindow {

    private Context mContext;
    private int mAnimationStyle;

    public SharePanelPopupWindow(Context context, View view, int width, int height,
        boolean focusable, int animationStyle) {
        super(view, width, height, focusable);
        this.mContext = context;
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.gray_light_alpha)));
        this.mAnimationStyle = animationStyle;
        this.getContentView().setFocusableInTouchMode(true);
        this.getContentView().setFocusable(true);

        if (mAnimationStyle != 0) {
            this.setAnimationStyle(mAnimationStyle);
        }
        this.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (isShowing()) {
                        dismiss();
                    }
                    return true;
                }
                return false;
            }
        });
    }
}
