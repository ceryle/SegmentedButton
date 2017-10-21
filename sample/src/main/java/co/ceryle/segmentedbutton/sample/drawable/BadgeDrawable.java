package co.ceryle.segmentedbutton.sample.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class BadgeDrawable extends Drawable {

    private Paint paint;
    private int color;
    private int width;
    private int height;
    private int borderWidth;
    private int borderRadius;

    private RectF rect;
    private Path path;
    private int count = 10;

    public BadgeDrawable(int color, int width, int height, int borderWidth, int borderRadius) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(32);

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        rect = new RectF();

        this.color = color;
        this.width = width;
        this.height = height;
        this.borderWidth = borderWidth;
        this.borderRadius = borderRadius;
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        path.reset();

        path.addRect(bounds.left, bounds.top, bounds.right, bounds.bottom, Path.Direction.CW);
        rect.set(bounds.left + borderWidth, bounds.top + borderWidth,
                bounds.right - borderWidth, bounds.bottom - borderWidth);
        path.addRoundRect(rect, borderRadius, borderRadius, Path.Direction.CW);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        paint.setColor(color);
        canvas.drawPath(path, paint);

        Rect textBounds = new Rect();
        String countString = String.valueOf(count);
        paint.getTextBounds(countString, 0, countString.length(), textBounds);
        canvas.drawText(
                countString,
                rect.right - (rect.right - rect.left) / 2 - textBounds.width() / 2,
                rect.top + textBounds.height() / 2 + (rect.bottom - rect.top) / 2,
                paint
        );
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
