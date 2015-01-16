package com.xebia.eventschedule.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;

import com.parse.ParseImageView;
import com.xebia.eventschedule.R;

/**
 * ImageView for creating circular images from Parse.com.
 * <p/>
 * Based on the CircularImageView project: https://github.com/lopspower/CircularImageView
 */
public class CircularParseImageView extends ParseImageView {

    private int borderWidth;
    private int canvasSize;
    private Bitmap image;
    private Paint paint;
    private Paint paintBorder;

    public CircularParseImageView(final Context context) {
        this(context, null);
    }

    public CircularParseImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.circularParseImageViewStyle);
    }

    public CircularParseImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (supportsCircularImages()) {
            // init paint
            paint = new Paint();
            paint.setAntiAlias(true);

            paintBorder = new Paint();
            paintBorder.setAntiAlias(true);

            // load the styled attributes and set their properties
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
                    R.styleable.CircularParseImageView, defStyle, 0);
            if (null == styledAttrs) {
                return;
            }

            if (styledAttrs.getBoolean(R.styleable.CircularParseImageView_border, true)) {
                setBorderWidth(styledAttrs.getColor(R.styleable
                        .CircularParseImageView_border_width, 4));
                setBorderColor(styledAttrs.getInt(R.styleable
                        .CircularParseImageView_border_color, Color.WHITE));
            }

            if (styledAttrs.getBoolean(R.styleable.CircularParseImageView_shadow, false)) {
                addShadow();
            }
        }
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        this.requestLayout();
        this.invalidate();
    }

    public void setBorderColor(int borderColor) {
        if (paintBorder != null) {
            paintBorder.setColor(borderColor);
            this.invalidate();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void addShadow() {
        setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
        paintBorder.setShadowLayer(4.0f, 0.0f, 2.0f, Color.BLACK);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!supportsCircularImages()) {
            super.onDraw(canvas);
            return;
        }

        // load the bitmap
        BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();
        if (bitmapDrawable != null)
            image = bitmapDrawable.getBitmap();

        // init shader
        if (image != null) {

            canvasSize = canvas.getWidth();
            if (canvas.getHeight() < canvasSize)
                canvasSize = canvas.getHeight();

            paint.setShader(new BitmapShader(Bitmap.createScaledBitmap(image, canvasSize,
                    canvasSize, false), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

            // circleCenter is the x or y of the view's center
            // radius is the radius in pixels of the circle to be drawn
            // paint contains the shader that will texture the shape
            int circleCenter = (canvasSize - (borderWidth * 2)) / 2;
            canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth,
                    ((canvasSize - (borderWidth * 2)) / 2) + borderWidth - 4.0f, paintBorder);
            canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth,
                    ((canvasSize - (borderWidth * 2)) / 2) - 4.0f, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!supportsCircularImages()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private boolean supportsCircularImages() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // The parent has determined an exact size for the child.
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // The parent has not imposed any constraint on the child.
            result = canvasSize;
        }

        return result;
    }

    private int measureHeight(int measureSpecHeight) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = canvasSize;
        }

        return (result + 2);
    }
}
