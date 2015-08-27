package com.lab.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.malinskiy.superrecyclerview.swipe.SwipeLayout;

/**
 * Created by baziii on 15/8/10.
 */
public class CanClickSwipeLayout extends SwipeLayout {
    public static final String TAG = "CanClickSwipeLayout";

    public CanClickSwipeLayout(Context context) {
        super(context);
    }

    public CanClickSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CanClickSwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }
    };

    GestureDetector mGestureDetector = new GestureDetector(getContext(), mSimpleOnGestureListener);

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = mGestureDetector.onTouchEvent(ev);
        if (result) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }
}

