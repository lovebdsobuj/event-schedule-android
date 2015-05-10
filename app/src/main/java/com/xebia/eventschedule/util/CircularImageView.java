package com.xebia.eventschedule.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xebia.eventschedule.R;

import static android.graphics.Shader.TileMode.CLAMP;

/**
 * An ImageView that crops inside to be circular.
 *
 * Source: https://raw.githubusercontent.com/lopspower/CircularImageView/35b3238030a6b794387ef9d7f51645ba03c4014e/CircularImageView/src/com/mikhaellopez/circularimageview/CircularImageView.java
 * Licence: CC-BY-4.0, http://creativecommons.org/licenses/by/4.0/
 */
public class CircularImageView extends ImageView {

    private final Paint paint = new Paint();
    private final Paint paintBorder = new Paint();
    private int borderWidth;
    private int canvasSize;
    private Bitmap image;
    private BitmapShader shader;

    public CircularImageView(final Context context) {
        super(context);
        init(context, null, R.attr.circularImageViewStyle, 0);
    }

    public CircularImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.circularImageViewStyle, 0);
    }

    public CircularImageView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircularImageView(final Context context, final AttributeSet attrs, final int defStyleAttr,
                             final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        paint.setAntiAlias(true);
        paintBorder.setAntiAlias(true);

        // load the styled attributes and set their properties
        TypedArray attributes =
            context.obtainStyledAttributes(attrs, R.styleable.CircularImageView, defStyleAttr, defStyleRes);

        if (attributes.getBoolean(R.styleable.CircularImageView_border, true)) {
            int defaultBorderSize = (int) (4 * getContext().getResources().getDisplayMetrics().density + 0.5f);
            setBorderWidth(
                attributes.getDimensionPixelOffset(R.styleable.CircularImageView_border_width, defaultBorderSize));
            setBorderColor(attributes.getColor(R.styleable.CircularImageView_border_color, Color.WHITE));
        }

        if (attributes.getBoolean(R.styleable.CircularImageView_shadow, false)) {
            addShadow();
        }

        attributes.recycle();
    }

    public void setBorderWidth(final int borderWidth) {
        this.borderWidth = borderWidth;
        this.requestLayout();
        this.invalidate();
    }

    public void setBorderColor(final int borderColor) {
        paintBorder.setColor(borderColor);
        this.invalidate();
    }

    public void addShadow() {
        setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
        paintBorder.setShadowLayer(4.0f, 0.0f, 2.0f, Color.BLACK);
    }

    @Override
    public void setImageDrawable(@Nullable final Drawable drawable) {
        super.setImageDrawable(drawable);
        image = drawableToBitmap(drawable);
        shader = null;
    }

    @Override
    public void onDraw(@NonNull final Canvas canvas) {

        canvasSize = canvas.getWidth();
        if (canvas.getHeight() < canvasSize) {
            canvasSize = canvas.getHeight();
        }

        if (null == shader && null != image) {
            shader = new BitmapShader(Bitmap.createScaledBitmap(image, canvasSize, canvasSize, false), CLAMP, CLAMP);
            paint.setShader(shader);
        }

        // circleCenter is the x or y of the view's center
        // radius is the radius in pixels of the circle to be drawn
        // paint contains the shader that will texture the shape
        int circleCenter = (canvasSize - (borderWidth * 2)) / 2;
        canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth,
            ((canvasSize - (borderWidth * 2)) / 2) + borderWidth - 4.0f, paintBorder);
        canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth,
            ((canvasSize - (borderWidth * 2)) / 2) - 4.0f, paint);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(final int widthMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

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

    private int measureHeight(final int heightMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);

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

    @Nullable
    private Bitmap drawableToBitmap(@Nullable Drawable drawable) {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
            drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
