package com.lab.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cuitrip.service.R;

/**
 * Created on 7/25.
 */
public class TextEditSplit extends EditText {
    public TextEditSplit(Context context) {
        super(context);
        init();
    }

    public TextEditSplit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextEditSplit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        int dp_10 = getResources().getDimensionPixelOffset(R.dimen.ct_dp_10);
        int dp_3 = getResources().getDimensionPixelOffset(R.dimen.ct_dp_3);
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
        setTextSize(15);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setPadding(0, dp_10, 0, dp_10);
        setLineSpacing(dp_3, 1.2f);
        setTextColor(getResources().getColor(R.color.ct_text_title));
    }
}
