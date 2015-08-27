package com.lab.widget;

/**
 * Created by baziii on 15/8/20.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

class IcsLinearLayout extends LinearLayout {
    private static final int[] LL = new int[]{16843049, 16843561, 16843562};
    private static final int LL_DIVIDER = 0;
    private static final int LL_SHOW_DIVIDER = 1;
    private static final int LL_DIVIDER_PADDING = 2;
    private Drawable mDivider;
    private int mDividerWidth;
    private int mDividerHeight;
    private int mShowDividers;
    private int mDividerPadding;

    public IcsLinearLayout(Context context, int themeAttr) {
        super(context);
        TypedArray a = context.obtainStyledAttributes((AttributeSet) null, LL, themeAttr, 0);
        this.setDividerDrawable(a.getDrawable(0));
        this.mDividerPadding = a.getDimensionPixelSize(2, 0);
        this.mShowDividers = a.getInteger(1, 0);
        a.recycle();
    }

    public void setDividerDrawable(Drawable divider) {
        if (divider != this.mDivider) {
            this.mDivider = divider;
            if (divider != null) {
                this.mDividerWidth = divider.getIntrinsicWidth();
                this.mDividerHeight = divider.getIntrinsicHeight();
            } else {
                this.mDividerWidth = 0;
                this.mDividerHeight = 0;
            }

            this.setWillNotDraw(divider == null);
            this.requestLayout();
        }
    }

    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        int index = this.indexOfChild(child);
        int orientation = this.getOrientation();
        LayoutParams params = (LayoutParams) child.getLayoutParams();
        if (this.hasDividerBeforeChildAt(index)) {
            if (orientation == 1) {
                params.topMargin = this.mDividerHeight;
            } else {
                params.leftMargin = this.mDividerWidth;
            }
        }

        int count = this.getChildCount();
        if (index == count - 1 && this.hasDividerBeforeChildAt(count)) {
            if (orientation == 1) {
                params.bottomMargin = this.mDividerHeight;
            } else {
                params.rightMargin = this.mDividerWidth;
            }
        }

        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    protected void onDraw(Canvas canvas) {
        if (this.mDivider != null) {
            if (this.getOrientation() == VERTICAL) {
                this.drawDividersVertical(canvas);
            } else {
                this.drawDividersHorizontal(canvas);
            }
        }

        super.onDraw(canvas);
    }

    private void drawDividersVertical(Canvas canvas) {
        int count = this.getChildCount();

        for (int child = 0; child < count; ++child) {
            View bottom = this.getChildAt(child);
            if (bottom != null && bottom.getVisibility() != GONE && this.hasDividerBeforeChildAt(child)) {
                LayoutParams lp = (LayoutParams) bottom.getLayoutParams();
                int top = bottom.getTop() - lp.topMargin;
                this.drawHorizontalDivider(canvas, top);
            }
        }

        if (this.hasDividerBeforeChildAt(count)) {
            View var7 = this.getChildAt(count - 1);
            boolean var8 = false;
            int var9;
            if (var7 == null) {
                var9 = this.getHeight() - this.getPaddingBottom() - this.mDividerHeight;
            } else {
                var9 = var7.getBottom();
            }

            this.drawHorizontalDivider(canvas, var9);
        }

    }

    private void drawDividersHorizontal(Canvas canvas) {
        int count = this.getChildCount();

        for (int child = 0; child < count; ++child) {
            View right = this.getChildAt(child);
            if (right != null && right.getVisibility() != GONE && this.hasDividerBeforeChildAt(child)) {
                LayoutParams lp = (LayoutParams) right.getLayoutParams();
                int left = right.getLeft() - lp.leftMargin;
                this.drawVerticalDivider(canvas, left);
            }
        }

        if (this.hasDividerBeforeChildAt(count)) {
            View var7 = this.getChildAt(count - 1);
            boolean var8 = false;
            int var9;
            if (var7 == null) {
                var9 = this.getWidth() - this.getPaddingRight() - this.mDividerWidth;
            } else {
                var9 = var7.getRight();
            }

            this.drawVerticalDivider(canvas, var9);
        }

    }

    private void drawHorizontalDivider(Canvas canvas, int top) {
        this.mDivider.setBounds(this.getPaddingLeft() + this.mDividerPadding, top, this.getWidth() - this.getPaddingRight() - this.mDividerPadding, top + this.mDividerHeight);
        this.mDivider.draw(canvas);
    }

    private void drawVerticalDivider(Canvas canvas, int left) {
        this.mDivider.setBounds(left, this.getPaddingTop() + this.mDividerPadding, left + this.mDividerWidth, this.getHeight() - this.getPaddingBottom() - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }

    private boolean hasDividerBeforeChildAt(int childIndex) {
        if (childIndex != 0 && childIndex != this.getChildCount()) {
            if ((this.mShowDividers & 2) == 0) {
                return false;
            } else {
                boolean hasVisibleViewBefore = false;

                for (int i = childIndex - 1; i >= 0; --i) {
                    if (this.getChildAt(i).getVisibility() != GONE) {
                        hasVisibleViewBefore = true;
                        break;
                    }
                }

                return hasVisibleViewBefore;
            }
        } else {
            return false;
        }
    }
}
