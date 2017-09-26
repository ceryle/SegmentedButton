/*
 * Copyright (C) 2016 ceryle
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SegmentedButton extends View {

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

    private float mClipAmount;
    private boolean clipLeftToRight;

    private TextPaint mTextPaint;
    private StaticLayout mStaticLayout, mStaticLayoutOverlay;
    private Rect mTextBounds = new Rect();
    private int mRadius, mBorderSize;
    private boolean hasBorderLeft, hasBorderRight;

    // private RectF rectF = new RectF();

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        getAttributes(attrs);

        initText();
        initBitmap();
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
    }

    void setSelectorColor(int color) {
        mPaint.setColor(color);
    }

    void setSelectorRadius(int radius) {
        mRadius = radius;
    }

    void setBorderSize(int borderSize) {
        mBorderSize = borderSize;
    }

    void hasBorderLeft(boolean hasBorderLeft) {
        this.hasBorderLeft = hasBorderLeft;
    }

    void hasBorderRight(boolean hasBorderRight) {
        this.hasBorderRight = hasBorderRight;
    }

    private RectF mRectF;
    private Paint mPaint;

    private void initText() {
        if (!hasText)
            return;

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);

        if (hasTextTypefacePath)
            setTypeface(textTypefacePath);
        else if (null != textTypeface) {
            setTypeface(textTypeface);
        }

        // default to a single line of text
        int width = (int) mTextPaint.measureText(text);
        mStaticLayout = new StaticLayout(text, mTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
        mStaticLayoutOverlay = new StaticLayout(text, mTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
    }

    private void initBitmap() {
        if (hasDrawable) {
            mDrawable = ContextCompat.getDrawable(context, drawable);
        }

        if (hasDrawableTint) {
            mBitmapNormalColor = new PorterDuffColorFilter(drawableTint, PorterDuff.Mode.SRC_IN);
        }

        if (hasDrawableTintOnSelection)
            mBitmapClipColor = new PorterDuffColorFilter(drawableTintOnSelection, PorterDuff.Mode.SRC_IN);
    }
    
    private void measureTextWidth(int width) {
        if (!hasText)
            return;

        int bitmapWidth = hasDrawable && drawableGravity.isHorizontal() ? mDrawable.getIntrinsicWidth() : 0;

        int textWidth = width - (bitmapWidth + getPaddingLeft() + getPaddingRight());

        if (textWidth < 0)
            return;

        mStaticLayout = new StaticLayout(text, mTextPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
        mStaticLayoutOverlay = new StaticLayout(text, mTextPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthRequirement = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightRequirement = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int bitmapWidth = hasDrawable ? mDrawable.getIntrinsicWidth() : 0;
        int textWidth = hasText ? mStaticLayout.getWidth() : 0;

        int height = getPaddingTop() + getPaddingBottom();
        int bitmapHeight = hasDrawable ? mDrawable.getIntrinsicHeight() : 0;
        int textHeight = hasText ? mStaticLayout.getHeight() : 0;

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                if (width < widthRequirement) {
                    width = widthRequirement;
                    measureTextWidth(width);
                }
                break;
            case MeasureSpec.AT_MOST:

                if (drawableGravity.isHorizontal()) {
                    width = textWidth + bitmapWidth + drawablePadding;
                } else {
                    width = Math.max(bitmapWidth, textWidth);
                }
                width += getPaddingLeft() * 2 + getPaddingRight() * 2;

                /*
                if (width > widthRequirement) {
                    width = widthRequirement;
                    measureTextWidth(width);
                }*/
                break;
            case MeasureSpec.UNSPECIFIED:
                width = textWidth + bitmapWidth;
                break;
        }

        if (hasText)
            mTextPaint.getTextBounds(text, 0, text.length(), mTextBounds);


        switch (heightMode) {
            case MeasureSpec.EXACTLY:

                if (drawableGravity.isHorizontal()) {
                    height = heightRequirement;
                    int h = Math.max(textHeight, bitmapHeight) + getPaddingTop() + getPaddingBottom();
                    if (heightRequirement < h) {
                        height = h;
                    }
                } else {
                    int h = textHeight + bitmapHeight + getPaddingTop() + getPaddingBottom();
                    if (heightRequirement < h)
                        height = h;
                    else
                        height = heightRequirement + getPaddingTop() - getPaddingBottom();
                }
                break;

            case MeasureSpec.AT_MOST:
                int vHeight;
                if (drawableGravity.isHorizontal()) {
                    vHeight = Math.max(textHeight, bitmapHeight);
                } else {
                    vHeight = textHeight + bitmapHeight + drawablePadding;
                }

                height = vHeight + getPaddingTop() * 2 + getPaddingBottom() * 2;

                break;
            case MeasureSpec.UNSPECIFIED:
                // height = heightMeasureSpec;
                break;
        }

        calculate(width, height);
        setMeasuredDimension(width, height);
    }

    private float text_X = 0.0f, text_Y = 0.0f, bitmap_X = 0.0f, bitmap_Y = 0.0f;

    private void calculate(int width, int height) {
        float textHeight = 0, textWidth = 0, textBoundsWidth = 0;
        if (hasText) {
            textHeight = mStaticLayout.getHeight();
            textWidth = mStaticLayout.getWidth();
            textBoundsWidth = mTextBounds.width();
        }

        float bitmapHeight = 0, bitmapWidth = 0;
        if (hasDrawable) {
            bitmapHeight = mDrawable.getIntrinsicHeight();
            bitmapWidth = mDrawable.getIntrinsicWidth();
        }


        if (drawableGravity.isHorizontal()) {
            if (height > Math.max(textHeight, bitmapHeight)) {
                text_Y = height / 2f - textHeight / 2f + getPaddingTop() - getPaddingBottom();
                bitmap_Y = height / 2f - bitmapHeight / 2f + getPaddingTop() - getPaddingBottom();
            } else if (textHeight > bitmapHeight) {
                text_Y = getPaddingTop();
                bitmap_Y = text_Y + textHeight / 2f - bitmapHeight / 2f;
            } else {
                bitmap_Y = getPaddingTop();
                text_Y = bitmap_Y + bitmapHeight / 2f - textHeight / 2f;
            }

            text_X = getPaddingLeft();
            bitmap_X = textWidth;

            float remainingSpace = width - (textBoundsWidth + bitmapWidth);
            if (remainingSpace > 0) {
                remainingSpace /= 2f;
            }

            if (drawableGravity == DrawableGravity.RIGHT) {
                text_X = remainingSpace + getPaddingLeft() - getPaddingRight() - drawablePadding / 2f;
                bitmap_X = text_X + textBoundsWidth + drawablePadding;
            } else if (drawableGravity == DrawableGravity.LEFT) {
                bitmap_X = remainingSpace + getPaddingLeft() - getPaddingRight() - drawablePadding / 2f;
                text_X = bitmap_X + bitmapWidth + drawablePadding;
            }
        } else {


            if (drawableGravity == DrawableGravity.TOP) {
                bitmap_Y = getPaddingTop() - getPaddingBottom() - drawablePadding / 2f;

                float vHeight = (height - (textHeight + bitmapHeight)) / 2f;

                if (vHeight > 0)
                    bitmap_Y += vHeight;

                text_Y = bitmap_Y + bitmapHeight + drawablePadding;

            } else if (drawableGravity == DrawableGravity.BOTTOM) {
                text_Y = getPaddingTop() - getPaddingBottom() - drawablePadding / 2f;

                float vHeight = height - (textHeight + bitmapHeight);
                if (vHeight > 0)
                    text_Y += vHeight / 2f;

                bitmap_Y = text_Y + textHeight + drawablePadding;
            }


            if (width > Math.max(textBoundsWidth, bitmapWidth)) {
                text_X = width / 2f - textBoundsWidth / 2f + getPaddingLeft() - getPaddingRight();
                bitmap_X = width / 2f - bitmapWidth / 2f + getPaddingLeft() - getPaddingRight();
            } else if (textBoundsWidth > bitmapWidth) {
                text_X = getPaddingLeft();
                bitmap_X = text_X + textBoundsWidth / 2f - bitmapWidth / 2f;
            } else {
                bitmap_X = getPaddingLeft();
                text_X = bitmap_X + bitmapWidth / 2f - textBoundsWidth / 2f;
            }
        }
    }

    private PorterDuffColorFilter mBitmapNormalColor, mBitmapClipColor;

    private Drawable mDrawable;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        canvas.save();

        if (clipLeftToRight)
            canvas.translate(-width * (mClipAmount - 1), 0);
        else
            canvas.translate(width * (mClipAmount - 1), 0);


        mRectF.set(hasBorderLeft ? mBorderSize : 0, mBorderSize, hasBorderRight ? width - mBorderSize : width, height - mBorderSize);
        canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);

        canvas.restore();

        canvas.save();

        if (hasText) {
            canvas.translate(text_X, text_Y);
            if (hasTextColorOnSelection)
                mTextPaint.setColor(textColor);
            mStaticLayout.draw(canvas);

            canvas.restore();
        }
        canvas.save();

        // Bitmap normal
        if (hasDrawable) {
            drawDrawableWithColorFilter(canvas, mBitmapNormalColor);
        }
        // NORMAL -end


        // CLIPPING
        if (clipLeftToRight) {
            canvas.clipRect(width * (1 - mClipAmount), 0, width, height);
        } else {
            canvas.clipRect(0, 0, width * mClipAmount, height);
        }

        // CLIP -start
        // Text clip
        canvas.save();

        if (hasText) {
            canvas.translate(text_X, text_Y);
            if (hasTextColorOnSelection)
                mTextPaint.setColor(textColorOnSelection);
            mStaticLayoutOverlay.draw(canvas);
            canvas.restore();
        }

        // Bitmap clip
        if (hasDrawable) {
            drawDrawableWithColorFilter(canvas, mBitmapClipColor);
        }
        // CLIP -end

        canvas.restore();
    }

    private void drawDrawableWithColorFilter(Canvas canvas, ColorFilter colorFilter){
        int drawableX = (int)bitmap_X;
        int drawableY = (int)bitmap_Y;
        int drawableWidth = mDrawable.getIntrinsicWidth();
        if (hasDrawableWidth) {
            drawableWidth = this.drawableWidth;
        }
        int drawableHeight = mDrawable.getIntrinsicHeight();
        if (hasDrawableHeight) {
            drawableHeight = this.drawableHeight;
        }
        mDrawable.setColorFilter(colorFilter);
        mDrawable.setBounds(drawableX, drawableY, drawableX + drawableWidth, drawableY + drawableHeight);
        mDrawable.draw(canvas);
    }

    public void clipToLeft(float clip) {
        clipLeftToRight = false;
        mClipAmount = 1.0f - clip;
        invalidate();
    }

    public void clipToRight(float clip) {
        clipLeftToRight = true;
        mClipAmount = clip;
        invalidate();
    }

    private int drawableTintOnSelection, textColorOnSelection, textColor, rippleColor, buttonWidth,
            drawable, drawableTint, drawableWidth, drawableHeight, drawablePadding;
    private boolean hasTextColorOnSelection, hasRipple, hasWidth, hasWeight, hasDrawableTintOnSelection,
            hasDrawableWidth, hasDrawableHeight, hasDrawableTint, hasTextTypefacePath;
    private float buttonWeight, textSize;
    private String textTypefacePath, text;
    private Typeface textTypeface;

    private void getAttributes(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SegmentedButton);

        drawableTintOnSelection = ta.getColor(R.styleable.SegmentedButton_sb_drawableTint_onSelection, Color.WHITE);
        hasDrawableTintOnSelection = ta.hasValue(R.styleable.SegmentedButton_sb_drawableTint_onSelection);

        textColorOnSelection = ta.getColor(R.styleable.SegmentedButton_sb_textColor_onSelection, Color.WHITE);
        hasTextColorOnSelection = ta.hasValue(R.styleable.SegmentedButton_sb_textColor_onSelection);

        rippleColor = ta.getColor(R.styleable.SegmentedButton_sb_rippleColor, 0);
        hasRipple = ta.hasValue(R.styleable.SegmentedButton_sb_rippleColor);

        text = ta.getString(R.styleable.SegmentedButton_sb_text);
        hasText = ta.hasValue(R.styleable.SegmentedButton_sb_text);
        textSize = ta.getDimension(R.styleable.SegmentedButton_sb_textSize, ConversionHelper.spToPx(getContext(), 14));
        textColor = ta.getColor(R.styleable.SegmentedButton_sb_textColor, Color.GRAY);
        textTypefacePath = ta.getString(R.styleable.SegmentedButton_sb_textTypefacePath);
        hasTextTypefacePath = ta.hasValue(R.styleable.SegmentedButton_sb_textTypefacePath);
        int typeface = ta.getInt(R.styleable.SegmentedButton_sb_textTypeface, 1);
        switch (typeface) {
            case 0:
                textTypeface = Typeface.MONOSPACE;
                break;
            case 1:
                textTypeface = Typeface.DEFAULT;
                break;
            case 2:
                textTypeface = Typeface.SANS_SERIF;
                break;
            case 3:
                textTypeface = Typeface.SERIF;
                break;
        }

        try {
            hasWeight = ta.hasValue(R.styleable.SegmentedButton_android_layout_weight);
            buttonWeight = ta.getFloat(R.styleable.SegmentedButton_android_layout_weight, 0);

            buttonWidth = ta.getDimensionPixelSize(R.styleable.SegmentedButton_android_layout_width, 0);

        } catch (Exception ex) {
            hasWeight = true;
            buttonWeight = 1;
        }
        hasWidth = !hasWeight && buttonWidth > 0;


        drawable = ta.getResourceId(R.styleable.SegmentedButton_sb_drawable, 0);
        drawableTint = ta.getColor(R.styleable.SegmentedButton_sb_drawableTint, -1);
        drawableWidth = ta.getDimensionPixelSize(R.styleable.SegmentedButton_sb_drawableWidth, -1);
        drawableHeight = ta.getDimensionPixelSize(R.styleable.SegmentedButton_sb_drawableHeight, -1);
        drawablePadding = ta.getDimensionPixelSize(R.styleable.SegmentedButton_sb_drawablePadding, 0);

        hasDrawable = ta.hasValue(R.styleable.SegmentedButton_sb_drawable);
        hasDrawableTint = ta.hasValue(R.styleable.SegmentedButton_sb_drawableTint);
        hasDrawableWidth = ta.hasValue(R.styleable.SegmentedButton_sb_drawableWidth);
        hasDrawableHeight = ta.hasValue(R.styleable.SegmentedButton_sb_drawableHeight);

        drawableGravity = DrawableGravity.getById(ta.getInteger(R.styleable.SegmentedButton_sb_drawableGravity, 0));


        ta.recycle();
    }

    /**
     * Typeface.NORMAL: 0
     * Typeface.BOLD: 1
     * Typeface.ITALIC: 2
     * Typeface.BOLD_ITALIC: 3
     *
     * @param typeface you can use above variations using the bitwise OR operator
     */
    public void setTypeface(Typeface typeface) {
        mTextPaint.setTypeface(typeface);
    }

    /**
     * @param location is .ttf file's path in assets folder. Example: 'fonts/my_font.ttf'
     */
    public void setTypeface(String location) {
        if (null != location && !location.equals("")) {
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), location);
            mTextPaint.setTypeface(typeface);
        }
    }

    /**
     * GRAVITY
     */

    private DrawableGravity drawableGravity;

    public enum DrawableGravity {
        LEFT(0),
        TOP(1),
        RIGHT(2),
        BOTTOM(3);

        private int intValue;

        DrawableGravity(int intValue) {
            this.intValue = intValue;
        }

        private int getIntValue() {
            return intValue;
        }

        public static DrawableGravity getById(int id) {
            for (DrawableGravity e : values()) {
                if (e.intValue == id) return e;
            }
            return null;
        }

        public boolean isHorizontal() {
            return intValue == 0 || intValue == 2;
        }
    }

    private boolean hasDrawable, hasText;


    /**
     * Sets button's drawable by given drawable object and its position
     *
     * @param resId is your drawable's resource id
     */
    public void setDrawable(int resId) {
        setDrawable(ContextCompat.getDrawable(context, resId));
    }

    /**
     * Sets button's drawable by given drawable object and its position
     *
     * @param drawable is your drawable object
     */
    public void setDrawable(Drawable drawable){
        mDrawable = drawable;
        hasDrawable = true;
        requestLayout();
    }

    /**
     * Sets button's drawable by given drawable id and its position
     *
     * @param gravity specifies button's drawable position relative to text position.
     *                These values can be given to position:
     *                {DrawableGravity.LEFT} sets drawable to the left of button's text
     *                {DrawableGravity.TOP} sets drawable to the top of button's text
     *                {DrawableGravity.RIGHT} sets drawable to the right of button's text
     *                {DrawableGravity.BOTTOM} sets drawable to the bottom of button's text
     */
    public void setGravity(DrawableGravity gravity) {
        drawableGravity = gravity;
    }

    /**
     * removes drawable's tint
     */
    public void removeDrawableTint() {
        hasDrawableTint = false;
    }

    public void removeDrawableTintOnSelection() {
        hasDrawableTintOnSelection = false;
    }

    public void removeTextColorOnSelection() {
        hasTextColorOnSelection = false;
    }

    /**
     * If button has any drawable, it sets drawable's tint color without changing drawable's position.
     *
     * @param color is used to set drawable's tint color
     */
    public void setDrawableTint(int color) {
        drawableTint = color;
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
        return textColorOnSelection;
    }

    /**
     * @param textColorOnSelection set button's text color when selector is on the button
     */
    public void setTextColorOnSelection(int textColorOnSelection) {
        this.textColorOnSelection = textColorOnSelection;
    }

    /**
     * @return drawable's tint color when selector is on the button
     */
    public int getDrawableTintOnSelection() {
        return drawableTintOnSelection;
    }

    /**
     * @return drawable's tint color
     */
    public int getDrawableTint() {
        return drawableTint;
    }

    /**
     * @return true if button's drawable is not empty
     */
    public boolean hasDrawableTint() {
        return hasDrawableTint;
    }

    /**
     * @return true if button's drawable has tint when selector is on the button
     */
    public boolean hasDrawableTintOnSelection() {
        return hasDrawableTintOnSelection;
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
