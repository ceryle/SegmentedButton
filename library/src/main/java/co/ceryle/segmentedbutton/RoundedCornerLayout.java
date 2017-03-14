/*
 * This class is used to create a round corner layout.
 * I used Sushant's solution which was shared on stackoverflow.com
 * Also, in order to fix elevation problem, I used Ed George's solution.
 * Thank you both :)
 *
 * Those solutions can be found below link
 * http://stackoverflow.com/questions/26074784/how-to-make-a-view-in-android-with-rounded-corners
 *
 * I extended this by adding border.
 */

package co.ceryle.segmentedbutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

public class RoundedCornerLayout extends FrameLayout {

    public RoundedCornerLayout(Context context) {
        super(context);
        init(context);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RoundedCornerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private float cornerRadius;
    private int strokeColor = Color.GRAY, strokeSize;
    private boolean hasStroke = false;

    private void init(Context context) {
        cornerRadius = ConversionHelper.dpToPx(context, 1);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ButtonOutlineProvider());
        }
    }

    //Fixes incorrect outline drawn by default
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class ButtonOutlineProvider extends ViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), cornerRadius);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int count = canvas.save();

        final Path path = new Path();
        path.addRoundRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), cornerRadius, cornerRadius, Path.Direction.CW);
        canvas.clipPath(path, Region.Op.REPLACE);
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(count);

        if (hasStroke) {
            Paint p_stroke = new Paint();
            p_stroke.setAntiAlias(true);
            p_stroke.setColor(strokeColor);
            p_stroke.setStyle(Paint.Style.STROKE);
            p_stroke.setStrokeWidth(strokeSize);
            Rect r = canvas.getClipBounds();

            int size = strokeSize / 2;
            Rect outline = new Rect(size, size, r.right - size, r.bottom - size);
            canvas.drawRoundRect(new RectF(outline), cornerRadius, cornerRadius, p_stroke);
        }
    }

    public void setCornerRadius(float radius) {
        cornerRadius = radius;
    }

    public void setStroke(boolean hasStroke) {
        this.hasStroke = hasStroke;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setStrokeSize(int strokeSize) {
        this.strokeSize = strokeSize;
    }
}