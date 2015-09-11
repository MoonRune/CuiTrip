package com.cuitrip.model;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.cuitrip.app.RelationActivity;

/**
 * Created by baziii on 15/9/11.
 */
public class JumpConcat {
    Activity activity;

    public JumpConcat(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void jumpConcat() {
        RelationActivity.start(activity);
    }

    public void destroy() {
        activity = null;
    }


}
