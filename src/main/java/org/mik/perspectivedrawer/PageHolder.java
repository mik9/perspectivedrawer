package org.mik.perspectivedrawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Holder layout with ability to draw transparent color over it childes.
 */
public class PageHolder extends FrameLayout {
    private int mForegroundColor = 0;

    public PageHolder(Context context) {
        this(context, null);
    }

    public PageHolder(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageHolder(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Set color to draw over childes.
     *
     * @param color the color
     */
    public void setForegroundColor(int color) {
        mForegroundColor = color;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(1, Paint.ANTI_ALIAS_FLAG));
        super.draw(canvas);
        canvas.drawColor(mForegroundColor);
    }
}
