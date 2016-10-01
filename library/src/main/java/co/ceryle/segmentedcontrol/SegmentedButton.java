/*
 * Copyright (C) 2016 Ege Aker <egeaker@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.ceryle.segmentedcontrol;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import com.ceryle.segmentedcontrol.R;

/**
 * Created by EGE on 07/09/2016.
 */

public class SegmentedButton extends Button {
    public SegmentedButton(Context context) {
        super(context);
        init(null);
    }

    public SegmentedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SegmentedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SegmentedButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        getAttributes(attrs);

        if (hasButtonImageTint)
            setImageTint(buttonImageTint);

        if (buttonImageScale != 1){
           scaleButtonDrawables(buttonImageScale);
            //scaleImage(getCompoundDrawables()[0], buttonImageScale);
        }

        setTransformationMethod(null);
    }

    public int getImageTint() {
        return buttonImageTint;
    }

    public boolean hasImageTint(){
        return hasButtonImageTint;
    }


    private int buttonImageTint;
    private boolean hasButtonImageTint;
    private float buttonImageScale;

    private void getAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SegmentedButton);
        buttonImageTint = typedArray.getColor(R.styleable.SegmentedButton_buttonImageTint, 0);
        hasButtonImageTint = typedArray.hasValue(R.styleable.SegmentedButton_buttonImageTint);
        buttonImageScale = typedArray.getFloat(R.styleable.SegmentedButton_buttonImageScale, 1);
        typedArray.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!changed) return;

        calcDrawablePosition();
    }


    // Pre-allocate objects for layout measuring
    private Rect textBounds = new Rect();
    private Rect drawableBounds = new Rect();

    private void calcDrawablePosition() {

        final CharSequence text = getText();
        if (!TextUtils.isEmpty(text)) {
            TextPaint textPaint = getPaint();
            textPaint.getTextBounds(text.toString(), 0, text.length(), textBounds);
        } else {
            textBounds.setEmpty();
        }

        final int width = getWidth() - (getPaddingLeft() + getPaddingRight());
        final int height = getWidth() - (getPaddingTop() + getPaddingBottom());

        final Drawable[] drawables = getCompoundDrawables();

        int offSet = 0;
        for (int i = 0; i < drawables.length; i++) {
            if (drawables[i] == null)
                continue;

            drawables[i].copyBounds(drawableBounds);
            switch (i) {
                case 0:
                    offSet = (width - (textBounds.width() + drawableBounds.width()) + getRightPaddingOffset()) / 2 - getCompoundDrawablePadding();
                    break;
                case 1:
                    offSet = (height - (textBounds.height() + drawableBounds.height()) + getBottomPaddingOffset()) / 2 + getCompoundDrawablePadding();
                    break;
                case 2:
                    offSet = ((textBounds.width() + drawableBounds.width()) - width + getLeftPaddingOffset()) / 2 + getCompoundDrawablePadding();
                    break;
                case 3:
                    offSet = ((textBounds.height() + drawableBounds.height()) - height + getTopPaddingOffset()) / 2 + getCompoundDrawablePadding();
                    break;
            }
            if (i % 2 == 0)
                drawableBounds.offset(offSet, 0);
            else
                drawableBounds.offset(0, offSet);

            drawables[i].setBounds(drawableBounds);
        }
    }

    public void scaleButtonDrawables(double fitFactor) {
        Drawable[] drawables = getCompoundDrawables();

        for (int i = 0; i < drawables.length; i++) {
            if (drawables[i] != null) {
                if (drawables[i] instanceof ScaleDrawable) {
                    drawables[i].setLevel(1);
                }
                ScaleDrawable sd = new ScaleDrawable(drawables[i], 0, drawables[i].getIntrinsicWidth(), drawables[i].getIntrinsicHeight());
                drawables[i].setBounds(0, 0, (int) (drawables[i].getIntrinsicWidth() * fitFactor), (int) (drawables[i].getIntrinsicHeight() * fitFactor));
                if (i == 0) {
                    setCompoundDrawables(drawables[i], drawables[1], drawables[2], drawables[3]);
                } else if (i == 1) {
                    setCompoundDrawables(drawables[0], sd.getDrawable(), drawables[2], drawables[3]);
                } else if (i == 2) {
                    setCompoundDrawables(drawables[0], drawables[1], sd.getDrawable(), drawables[3]);
                } else {
                    setCompoundDrawables(drawables[0], drawables[1], drawables[2], sd.getDrawable());
                }
            }
        }
    }

    public Drawable scaleImage (Drawable image, float scaleFactor) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        image = new BitmapDrawable(getResources(), bitmapResized);

        setCompoundDrawables(image, null, null, null);

        return image;

    }

    public void setImageTint(int color) {
        int pos = 0;
        Drawable drawable = null;

        if (getCompoundDrawables().length > 0) {
            for (int i = 0; i < getCompoundDrawables().length; i++) {
                if (getCompoundDrawables()[i] != null) {
                    pos = i;
                    drawable = getCompoundDrawables()[i];
                }
            }

            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));

                if (pos == 0)
                    setCompoundDrawables(drawable, null, null, null);
                else if (pos == 1)
                    setCompoundDrawables(null, drawable, null, null);
                else if (pos == 2)
                    setCompoundDrawables(null, null, drawable, null);
                else
                    setCompoundDrawables(null, null, null, drawable);
            }
        }
    }

}
