package co.ceryle.segmentedbutton;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class BackgroundView extends View {
    public BackgroundView(Context context) {
        super(context);
    }

    public BackgroundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = 0, h = 0;

        w = Math.max(w, getSuggestedMinimumWidth());
        h = Math.max(h, getSuggestedMinimumHeight());

        int widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
        int heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);
        setMeasuredDimension(widthSize, heightSize);
    }
}
