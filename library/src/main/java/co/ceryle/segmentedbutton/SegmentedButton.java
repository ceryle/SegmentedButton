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
package co.ceryle.segmentedbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.ceryle.segmentedbutton.R;

public class SegmentedButton extends AppCompatButton {

    private Context context;

    public SegmentedButton(Context context) {
        super(context);
        init(context, null);
    }

    public SegmentedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SegmentedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        getAttributes(attrs);

        if (hasButtonImageTint)
            setDrawableTint(imageTint);

        if (buttonImageScale != 1)
            scaleDrawable(buttonImageScale);

        setTransformationMethod(null);
        setTypeface(typeface);
        setClickable(false);
        setFocusable(false);
        setBackgroundColor(Color.TRANSPARENT);
    }

    private int imageTint, selectedImageTint, selectedTextColor, rippleColor, buttonWidth, selectorColor;
    private boolean hasButtonImageTint, hasSelectedImageTint, hasTextColorOnSelection, hasRipple, hasWidth, hasWeight, hasSelectorColor;
    private float buttonImageScale, buttonWeight;
    private String typeface;

    private void getAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SegmentedButton);
        imageTint = typedArray.getColor(R.styleable.SegmentedButton_sb_imageTint, -1);
        hasButtonImageTint = typedArray.hasValue(R.styleable.SegmentedButton_sb_imageTint);
        buttonImageScale = typedArray.getFloat(R.styleable.SegmentedButton_sb_imageScale, 1);

        selectedImageTint = typedArray.getColor(R.styleable.SegmentedButton_sb_selectedImageTint, 0);
        hasSelectedImageTint = typedArray.hasValue(R.styleable.SegmentedButton_sb_selectedImageTint);

        selectedTextColor = typedArray.getColor(R.styleable.SegmentedButton_sb_selectedTextColor, 0);
        hasTextColorOnSelection = typedArray.hasValue(R.styleable.SegmentedButton_sb_selectedTextColor);

        rippleColor = typedArray.getColor(R.styleable.SegmentedButton_sb_rippleColor, 0);
        hasRipple = typedArray.hasValue(R.styleable.SegmentedButton_sb_rippleColor);

        typeface = typedArray.getString(R.styleable.SegmentedButton_sb_typeface);

        selectorColor = typedArray.getColor(R.styleable.SegmentedButton_sb_selectorColor, Color.TRANSPARENT);
        hasSelectorColor = typedArray.hasValue(R.styleable.SegmentedButton_sb_selectorColor);

        try {
            hasWeight = typedArray.hasValue(R.styleable.SegmentedButton_android_layout_weight);
            buttonWeight = typedArray.getFloat(R.styleable.SegmentedButton_android_layout_weight, 0);

            buttonWidth = typedArray.getDimensionPixelSize(R.styleable.SegmentedButton_android_layout_width, 0);

        } catch (Exception ex) {
            hasWeight = true;
            buttonWeight = 1;
        }
        hasWidth = !hasWeight && buttonWidth > 0;

        typedArray.recycle();
    }

    public int getSelectorColor() {
        return selectorColor;
    }

    public boolean hasSelectorColor() {
        return hasSelectorColor;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!changed) return;

        if (buttonImageScale != 1)
            scaleDrawable(buttonImageScale);

        drawButton();
    }

    // Pre-allocate objects for layout measuring
    private Rect textBounds = new Rect();
    private Rect drawableBounds = new Rect();

    private void drawButton() {

        final CharSequence text = getText();
        if (!TextUtils.isEmpty(text)) {
            TextPaint textPaint = getPaint();
            textPaint.getTextBounds(text.toString(), 0, text.length(), textBounds);
        } else {
            textBounds.setEmpty();
        }

        final int width = getWidth() - (getPaddingLeft() + getPaddingRight());
        final int height = getHeight() - (getPaddingTop() + getPaddingBottom());

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
                    offSet = (height - (textBounds.height() + drawableBounds.height()) + getBottomPaddingOffset()) / 2 - getCompoundDrawablePadding();
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


    /**
     * @param location is .ttf file's path in assets folder. Example: 'fonts/my_font.ttf'
     */
    public void setTypeface(String location) {
        if (null != location && !location.equals("")) {
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), location);
            setTypeface(typeface);
        }
    }


    /**
     * @param scale sets button's drawable size. It multiplies drawable's width and height with the given variable.
     */
    public void scaleDrawable(double scale) {
        Drawable[] drawables = getCompoundDrawables();

        for (int i = 0; i < drawables.length; i++) {
            if (drawables[i] != null) {
                if (drawables[i] instanceof ScaleDrawable) {
                    drawables[i].setLevel(1);
                }
                ScaleDrawable sd = new ScaleDrawable(drawables[i], 0, drawables[i].getIntrinsicWidth(), drawables[i].getIntrinsicHeight());
                drawables[i].setBounds(0, 0, (int) (drawables[i].getIntrinsicWidth() * scale), (int) (drawables[i].getIntrinsicHeight() * scale));

                setDrawable(sd.getDrawable(), i);
            }
        }
    }

    /**
     * Constants gives available positions for drawable to set
     */
    public final static int DRAWABLE_LEFT = 0;
    public final static int DRAWABLE_TOP = 1;
    public final static int DRAWABLE_RIGHT = 2;
    public final static int DRAWABLE_BOTTOM = 3;

    /**
     * Sets button's drawable by given drawable object and its position
     *
     * @param drawable is directly set to button's drawable
     * @param position specifies button's drawable position relative to text position.
     *                 These values can be given to position:
     *                 {@link #DRAWABLE_LEFT} sets drawable to the left of button's text
     *                 {@link #DRAWABLE_TOP} sets drawable to the top of button's text
     *                 {@link #DRAWABLE_RIGHT} sets drawable to the right of button's text
     *                 {@link #DRAWABLE_BOTTOM} sets drawable to the bottom of button's text
     */
    public void setDrawable(Drawable drawable, int position) {
        if (drawable != null) {
            if (position == 0)
                setCompoundDrawables(drawable, null, null, null);
            else if (position == 1)
                setCompoundDrawables(null, drawable, null, null);
            else if (position == 2)
                setCompoundDrawables(null, null, drawable, null);
            else
                setCompoundDrawables(null, null, null, drawable);
        }
    }

    /**
     * Sets button's drawable by given drawable id and its position
     *
     * @param drawableId is used to get drawable object
     * @param position   specifies button's drawable position relative to text position.
     *                   These values can be given to position:
     *                   {@link #DRAWABLE_LEFT} sets drawable to the left of button's text
     *                   {@link #DRAWABLE_TOP} sets drawable to the top of button's text
     *                   {@link #DRAWABLE_RIGHT} sets drawable to the right of button's text
     *                   {@link #DRAWABLE_BOTTOM} sets drawable to the bottom of button's text
     */
    public void setDrawable(int drawableId, int position) {
        setDrawable(ContextCompat.getDrawable(context, drawableId), position);
    }

    /**
     * removes drawable's tint if it has any color
     */
    public void removeDrawableTint() {
        for (int i = 0; i < getCompoundDrawables().length; i++) {
            if (getCompoundDrawables()[i] != null)
                getCompoundDrawables()[i].clearColorFilter();
        }
    }

    /**
     * If button has any drawable, it sets drawable's tint color without changing drawable's position.
     *
     * @param color is used to set drawable's tint color
     */
    public void setDrawableTint(int color) {
        int pos = 0;
        Drawable drawable = null;

        if (getCompoundDrawables().length > 0) {
            for (int i = 0; i < getCompoundDrawables().length; i++) {
                if (getCompoundDrawables()[i] != null) {
                    pos = i;
                    drawable = getCompoundDrawables()[i];
                }
            }
            if (drawable != null)
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            setDrawable(drawable, pos);
        }
    }

    /**
     * If button has any drawable, it sets drawable's tint color without changing drawable's position.
     *
     * @param drawableId is used to get drawable object
     * @param position   specifies button's drawable position relative to text position.
     *                   These values can be given to position:
     *                   {@link #DRAWABLE_LEFT} sets drawable to the left of button's text
     *                   {@link #DRAWABLE_TOP} sets drawable to the top of button's text
     *                   {@link #DRAWABLE_RIGHT} sets drawable to the right of button's text
     *                   {@link #DRAWABLE_BOTTOM} sets drawable to the bottom of button's text
     * @param color      is used to set drawable's tint color
     */
    public void setDrawableTint(int drawableId, int position, int color) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        setDrawableTint(drawable, position, color);
    }

    /**
     * If button has any drawable, it sets drawable's tint color without changing drawable's position.
     *
     * @param drawable is directly set to button's drawable
     * @param position specifies button's drawable position relative to text position.
     *                 These values can be given to position:
     *                 {@link #DRAWABLE_LEFT} sets drawable to the left of button's text
     *                 {@link #DRAWABLE_TOP} sets drawable to the top of button's text
     *                 {@link #DRAWABLE_RIGHT} sets drawable to the right of button's text
     *                 {@link #DRAWABLE_BOTTOM} sets drawable to the bottom of button's text
     * @param color    is used to set drawable's tint color
     */
    public void setDrawableTint(Drawable drawable, int position, int color) {
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        setDrawable(drawable, position);
    }


    /**
     * @return button's current ripple color
     */
    public int getRippleColor() {
        return rippleColor;
    }

    /**
     * @return true if the button has a ripple effect
     */
    public boolean hasRipple() {
        return hasRipple;
    }

    /**
     * @return button's text color when selector is on the button
     */
    public int getTextColorOnSelection() {
        return selectedTextColor;
    }

    /**
     * @param selectedTextColor set button's text color when selector is on the button
     */
    public void setTextColorOnSelection(int selectedTextColor) {
        this.selectedTextColor = selectedTextColor;
    }

    /**
     * @return drawable's tint color when selector is on the button
     */
    public int getDrawableTintOnSelection() {
        return selectedImageTint;
    }

    /**
     * @return drawable's tint color
     */
    public int getDrawableTint() {
        return imageTint;
    }

    /**
     * @return drawable's scale. Default scale is 1.0
     */
    public float getDrawableScale() {
        return buttonImageScale;
    }

    /**
     * @return true if button's drawable is not empty
     */
    public boolean hasDrawableTint() {
        return hasButtonImageTint;
    }

    /**
     * sets whether drawable should have tint or not
     */
    public void hasDrawableTint(boolean hasTint) {
        this.hasButtonImageTint = hasTint;
    }

    /**
     * @return true if button's drawable has tint when selector is on the button
     */
    public boolean hasDrawableTintOnSelection() {
        return hasSelectedImageTint;
    }

    /**
     *
     */
    boolean hasWeight() {
        return hasWeight;
    }

    float getWeight() {
        return buttonWeight;
    }

    int getButtonWidth() {
        return buttonWidth;
    }

    boolean hasWidth() {
        return hasWidth;
    }

    boolean hasTextColorOnSelection() {
        return hasTextColorOnSelection;
    }
}
