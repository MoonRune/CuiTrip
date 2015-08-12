package com.cuitrip.app.base;

import android.annotation.TargetApi;
import android.text.TextUtils;

import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;

/**
 * Created by baziii on 15/8/12.
 */
public class CtException extends Throwable {

    public CtException() {
    }

    public CtException(String detailMessage) {
        super(TextUtils.isEmpty(detailMessage)? PlatformUtil.getInstance().getString(R.string.load_error):detailMessage);
    }

    public CtException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    public CtException(Throwable cause) {
        super(cause);
    }

    @TargetApi(19)
    public CtException(String detailMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(detailMessage, cause, enableSuppression, writableStackTrace);
    }
}
