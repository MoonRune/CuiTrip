package com.cuitrip.app.base;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.cuitrip.app.MainApplication;

/**
 * Created by baziii on 15/8/24.
 */
public class BaseItemDecoration extends RecyclerView.ItemDecoration {

    private int spaceLeft;
    private int spaceRight;
    private int spaceTop;
    private int spaceBottom;

    public BaseItemDecoration(int space) {
        Resources resources = MainApplication.getInstance().getResources();
        spaceLeft = spaceBottom = spaceRight = spaceTop =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, space,
                        resources.getDisplayMetrics());
    }

    public BaseItemDecoration(int spaceLeft, int spaceRight, int spaceTop, int spaceBottom) {
        this.spaceLeft = spaceLeft;
        this.spaceRight = spaceRight;
        this.spaceTop = spaceTop;
        this.spaceBottom = spaceBottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        outRect.left = spaceLeft;
        outRect.right = spaceRight;
        outRect.bottom = spaceBottom;
        outRect.top = spaceTop;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

}
