package com.xebia.eventschedule.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

/**
 * Text span that puts extra margin to the left of a bit of text, rendered in a given color.
 */
public class AccentColorSpan implements LeadingMarginSpan {

    private final int colorBlipWidth;
    private final int leadingMarginWidth;
    private final Paint leadingMarginPaint;

    public AccentColorSpan(int leadingMarginColor, int colorBlipWidth, int colorBlipMarginRight) {
        this.colorBlipWidth = colorBlipWidth;
        this.leadingMarginWidth = colorBlipWidth + colorBlipMarginRight;
        this.leadingMarginPaint = new Paint();
        this.leadingMarginPaint.setStyle(Paint.Style.FILL);
        this.leadingMarginPaint.setColor(leadingMarginColor);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return leadingMarginWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas canvas, Paint paint, int x, int dir, int top,
                                  int baseline, int bottom, CharSequence text, int start,
                                  int end, boolean first, Layout layout) {
        int left, right;
        if (dir >= 0) {
            left = x;
            right = left + colorBlipWidth;
        } else {
            right = x;
            left = right - colorBlipWidth;
        }
        canvas.drawRect(left, 0, right, canvas.getHeight(), leadingMarginPaint);
    }
}
