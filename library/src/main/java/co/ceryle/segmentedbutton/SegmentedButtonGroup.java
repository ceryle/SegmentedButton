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

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ceryle.segmentedbutton.R;

import java.util.ArrayList;

public class SegmentedButtonGroup extends RoundedCornerLayout {

    public SegmentedButtonGroup(Context context) {
        super(context);
        init(null);
    }

    public SegmentedButtonGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SegmentedButtonGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SegmentedButtonGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private LinearLayout mainGroup, rippleContainer, dividerContainer;
    private ImageView leftGroup, rightGroup;

    private void init(AttributeSet attrs) {
        getAttributes(attrs);

        setCornerRadius(radius);

        mainGroup = new LinearLayout(getContext());
        mainGroup.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mainGroup.setOrientation(LinearLayout.HORIZONTAL);
        addView(mainGroup);

        rightGroup = new ImageView(getContext());
        rightGroup.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        rightGroup.setScaleType(ImageView.ScaleType.MATRIX);
        addView(rightGroup);

        leftGroup = new ImageView(getContext());
        leftGroup.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        leftGroup.setScaleType(ImageView.ScaleType.MATRIX);
        addView(leftGroup);

        rippleContainer = new LinearLayout(getContext());
        rippleContainer.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        rippleContainer.setOrientation(LinearLayout.HORIZONTAL);
        rippleContainer.setClickable(true);
        rippleContainer.setFocusable(true);
        addView(rippleContainer);

        dividerContainer = new LinearLayout(getContext());
        dividerContainer.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        dividerContainer.setOrientation(LinearLayout.HORIZONTAL);
        dividerContainer.setClickable(false);
        dividerContainer.setFocusable(false);
        addView(dividerContainer);

        initInterpolations();
        setContainerAttrs();
        setDividerAttrs();
        setBorderAttrs();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int w1 = 0, w2 = 0;

        int pos = position + 1;
        for (int i = 0; i < pos; i++) {
            if (i != 0)
                w1 += buttons.get(i - 1).getWidth();
            w2 += buttons.get(i).getWidth();
        }

        leftGroup.getLayoutParams().width = w1;
        rightGroup.getLayoutParams().width = w2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);

        if (!hasWidth) {
            mainGroup.getLayoutParams().width = LayoutParams.MATCH_PARENT;
        }

        int groupWidth = mainGroup.getMeasuredWidth();

        if (groupWidth > 0) {
            width = groupWidth;
        }

        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), heightMeasureSpec);
    }

    private boolean hasWidth = true;

    private void setBorderAttrs() {
        if (borderSize > 0) {
            setStroke(true);
            setStrokeColor(borderColor);
            setStrokeSize(borderSize);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        sizeChanged = true;
    }

    private boolean sizeChanged = false;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (!isInEditMode() && sizeChanged) {
            sizeChanged = false;
            updateViews();
        }
    }

    private void setBackgroundColor(View v, Drawable d, int c) {
        if (null != d) {
            BackgroundHelper.setBackground(v, d);
        } else {
            v.setBackgroundColor(c);
        }
    }

    private void setDividerAttrs() {
        if (!hasDivider)
            return;
        dividerContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        // Divider Views
        RoundHelper.makeDividerRound(dividerContainer, dividerColor, dividerRadius, dividerSize, dividerBackgroundDrawable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            dividerContainer.setDividerPadding(dividerPadding);
        }
    }

    /**
     * Call it when you change your SegmentedButton or its Group
     */
    public void updateViews() {
        ArrayList<ButtonAttributes> attrs = new ArrayList<>();
        // Stage - I
        setBackgroundColor(mainGroup, backgroundDrawable, backgroundColor);


        boolean hasButtonBackgroundColor = false;
        // Stage - II
        leftGroup.setImageBitmap(getViewBitmap(mainGroup));
        for (int i = 0; i < buttons.size(); i++) {
            SegmentedButton b = buttons.get(i);

            ButtonAttributes attr = new ButtonAttributes();

            attr.setTextColor(b, textColorOnSelection, hasTextColorOnSelection);
            attr.setTintColor(b, drawableTintOnSelection, hasDrawableTintOnSelection);

            attrs.add(attr);


            if (b.hasSelectorColor()) {
                hasButtonBackgroundColor = true;
            }
        }

        if (hasButtonBackgroundColor) {
            for (SegmentedButton b : buttons) {
                b.setBackgroundColor(b.getSelectorColor());
            }
            setBackgroundColor(mainGroup, selectorBackgroundDrawable, Color.TRANSPARENT);
        } else
            setBackgroundColor(mainGroup, selectorBackgroundDrawable, selectorColor);

        rightGroup.setImageBitmap(getViewBitmap(mainGroup));

        // Stage - III
        for (int i = 0; i < buttons.size(); i++) {
            ButtonAttributes.setAttributes(buttons.get(i), attrs.get(i));
            buttons.get(i).setBackgroundColor(Color.TRANSPARENT);
        }
        setBackgroundColor(mainGroup, backgroundDrawable, backgroundColor);
    }

    private void toggle(int position, int duration, boolean isToggledByTouch) {
        int w1 = 0, w2 = 0;
        int pos = position + 1;
        for (int i = 0; i < pos; i++) {
            if (i != 0)
                w1 += buttons.get(i - 1).getWidth();
            w2 += buttons.get(i).getWidth();
        }

        expand(leftGroup, interpolatorSelector, duration, w1);
        expand(rightGroup, interpolatorSelector, duration, w2);

        if (null != onClickedButtonListener && isToggledByTouch)
            onClickedButtonListener.onClickedButton(position);

        if (null != onPositionChangedListener)
            onPositionChangedListener.onPositionChanged(position);

        this.position = position;
    }

    private void expand(final View v, Interpolator interpolator, int duration, int targetWidth) {
        int prevWidth = v.getWidth();

        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevWidth, targetWidth);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().width = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof SegmentedButton) {
            SegmentedButton button = (SegmentedButton) child;

            int width = button.hasWeight() ? 0 : button.getButtonWidth();
            int height = LayoutParams.MATCH_PARENT;
            float weight = button.getWeight();

            int buttonWidth = button.getButtonWidth();
            if (buttonWidth == LayoutParams.WRAP_CONTENT) {
                width = 0;
                weight = 1;
            }

            mainGroup.addView(child, new LinearLayout.LayoutParams(width, height, weight));

            hasWidth = button.hasWidth();

            buttons.add(button);

            // RIPPLE
            BackgroundView rippleView = new BackgroundView(getContext());
            rippleView.setLayoutParams(new LinearLayout.LayoutParams(width, height, weight));

            final int pos = buttons.size() - 1;
            rippleView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickable && enabled)
                        toggle(pos, animateSelectorDuration, true);
                }
            });
            setRipple(rippleView, enabled && clickable);
            rippleContainer.addView(rippleView);

            ripples.add(rippleView);

            if (!hasDivider)
                return;

            if (button.hasWidth())
                width -= pos == 0 ? dividerSize / 2f : dividerSize;

            BackgroundView dividerView = new BackgroundView(getContext());
            dividerView.setLayoutParams(new LinearLayout.LayoutParams(width, height, weight));
            dividerContainer.addView(dividerView);
        } else
            super.addView(child, index, params);
    }

    private ArrayList<View> ripples = new ArrayList<>();

    private void setRipple(View v, boolean isClickable) {
        if (isClickable) {
            if (hasRippleColor)
                RippleHelper.setRipple(v, rippleColor);
            else if (ripple)
                RippleHelper.setSelectableItemBackground(getContext(), v);
            else {
                for (Button button : buttons) {
                    if (button instanceof SegmentedButton && ((SegmentedButton) button).hasRipple())
                        RippleHelper.setRipple(v, ((SegmentedButton) button).getRippleColor());
                }
            }
        } else {
            BackgroundHelper.setBackground(v, null);
        }
    }

    private void setContainerAttrs() {
        if (isInEditMode())
            mainGroup.setBackgroundColor(backgroundColor);
    }

    private ArrayList<SegmentedButton> buttons = new ArrayList<>();

    private int selectorColor, animateSelector, animateSelectorDuration, position, backgroundColor, dividerColor,
            drawableTintOnSelection, textColorOnSelection, dividerSize, rippleColor, dividerPadding, dividerRadius, borderSize, borderColor;
    private float radius;
    private boolean clickable, enabled, ripple, hasRippleColor, hasDivider, hasDrawableTintOnSelection, hasTextColorOnSelection;

    private Drawable backgroundDrawable, selectorBackgroundDrawable, dividerBackgroundDrawable;

    /**
     * Get attributes
     **/
    private void getAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SegmentedButtonGroup);

        hasDivider = typedArray.hasValue(R.styleable.SegmentedButtonGroup_sbg_dividerSize);
        dividerSize = typedArray.getDimensionPixelSize(R.styleable.SegmentedButtonGroup_sbg_dividerSize, 0);
        dividerColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_dividerColor, Color.WHITE);
        dividerPadding = typedArray.getDimensionPixelSize(R.styleable.SegmentedButtonGroup_sbg_dividerPadding, 0);
        dividerRadius = typedArray.getDimensionPixelSize(R.styleable.SegmentedButtonGroup_sbg_dividerRadius, 0);

        textColorOnSelection = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_selectorTextColor, Color.GRAY);
        hasTextColorOnSelection = typedArray.hasValue(R.styleable.SegmentedButtonGroup_sbg_selectorTextColor);
        drawableTintOnSelection = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_selectorImageTint, Color.GRAY);
        hasDrawableTintOnSelection = typedArray.hasValue(R.styleable.SegmentedButtonGroup_sbg_selectorImageTint);
        selectorColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_selectorColor, Color.GRAY);
        animateSelector = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateSelector, 0);
        animateSelectorDuration = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateSelectorDuration, 500);

        radius = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_radius, 0);
        position = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_position, 0);
        backgroundColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_backgroundColor, Color.WHITE);

        ripple = typedArray.getBoolean(R.styleable.SegmentedButtonGroup_sbg_ripple, false);
        hasRippleColor = typedArray.hasValue(R.styleable.SegmentedButtonGroup_sbg_rippleColor);
        rippleColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_rippleColor, Color.GRAY);

        borderSize = typedArray.getDimensionPixelSize(R.styleable.SegmentedButtonGroup_sbg_borderSize, 0);
        borderColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_borderColor, Color.BLACK);

        backgroundDrawable = typedArray.getDrawable(R.styleable.SegmentedButtonGroup_sbg_backgroundDrawable);
        selectorBackgroundDrawable = typedArray.getDrawable(R.styleable.SegmentedButtonGroup_sbg_selectorBackgroundDrawable);
        dividerBackgroundDrawable = typedArray.getDrawable(R.styleable.SegmentedButtonGroup_sbg_dividerBackgroundDrawable);

        enabled = typedArray.getBoolean(R.styleable.SegmentedButtonGroup_sbg_enabled, true);

        try {
            clickable = typedArray.getBoolean(R.styleable.SegmentedButtonGroup_android_clickable, true);
        } catch (Exception ex) {
            Log.d("SegmentedButtonGroup", ex.toString());
        }

        typedArray.recycle();
    }

    private Interpolator interpolatorSelector;

    private void initInterpolations() {
        ArrayList<Class> interpolatorList = new ArrayList<Class>() {{
            add(FastOutSlowInInterpolator.class);
            add(BounceInterpolator.class);
            add(LinearInterpolator.class);
            add(DecelerateInterpolator.class);
            add(CycleInterpolator.class);
            add(AnticipateInterpolator.class);
            add(AccelerateDecelerateInterpolator.class);
            add(AccelerateInterpolator.class);
            add(AnticipateOvershootInterpolator.class);
            add(FastOutLinearInInterpolator.class);
            add(LinearOutSlowInInterpolator.class);
            add(OvershootInterpolator.class);
        }};

        try {
            interpolatorSelector = (Interpolator) interpolatorList.get(animateSelector).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final static int FastOutSlowInInterpolator = 0;
    public final static int BounceInterpolator = 1;
    public final static int LinearInterpolator = 2;
    public final static int DecelerateInterpolator = 3;
    public final static int CycleInterpolator = 4;
    public final static int AnticipateInterpolator = 5;
    public final static int AccelerateDecelerateInterpolator = 6;
    public final static int AccelerateInterpolator = 7;
    public final static int AnticipateOvershootInterpolator = 8;
    public final static int FastOutLinearInInterpolator = 9;
    public final static int LinearOutSlowInInterpolator = 10;
    public final static int OvershootInterpolator = 11;

    private OnPositionChangedListener onPositionChangedListener;

    /**
     * @param onPositionChangedListener set your instance that you have created to listen any position change
     */
    public void setOnPositionChangedListener(OnPositionChangedListener onPositionChangedListener) {
        this.onPositionChangedListener = onPositionChangedListener;
    }

    /**
     * Use this listener if you want to know any position change.
     * Listener is called when one of segmented button is clicked or setPosition is called.
     */
    public interface OnPositionChangedListener {
        void onPositionChanged(int position);
    }

    private OnClickedButtonListener onClickedButtonListener;

    /**
     * @param onClickedButtonListener set your instance that you have created to listen clicked positions
     */
    public void setOnClickedButtonListener(OnClickedButtonListener onClickedButtonListener) {
        this.onClickedButtonListener = onClickedButtonListener;
    }

    /**
     * Use this listener if  you want to know which button is clicked.
     * Listener is called when one of segmented button is clicked
     */
    public interface OnClickedButtonListener {
        void onClickedButton(int position);
    }

    /**
     * Crate a bitmap from the given view
     */
    private Bitmap getViewBitmap(View view) {
        // setContainerAttrs();

        //Get the dimensions of the view so we can re-layout the view at its current size
        //and create a bitmap of the same size
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        int measuredWidth = MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        //Cause the view to re-layout
        view.measure(measuredWidth, measuredHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create a bitmap backed Canvas to draw the view into
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
        view.draw(c);
        return b;
    }

    /**
     * @param position is used to select one of segmented buttons
     * @param duration determines how long animation takes to finish
     */
    public void setPosition(final int position, final int duration) {
        this.position = position;
        post(new Runnable() {
            @Override
            public void run() {
                toggle(position, duration, false);
            }
        });
    }

    /**
     * @param position      is used to select one of segmented buttons
     * @param withAnimation if true default animation will perform
     */
    public void setPosition(final int position, final boolean withAnimation) {
        this.position = position;
        post(new Runnable() {
            @Override
            public void run() {
                if (withAnimation)
                    toggle(position, animateSelectorDuration, false);
                else
                    toggle(position, 0, false);
            }
        });
    }


    /**
     * @param selectorColor sets color to selector
     *                      default: Color.GRAY
     */
    public void setSelectorColor(int selectorColor) {
        this.selectorColor = selectorColor;
    }

    /**
     * @param backgroundColor sets background color of whole layout including buttons on top of it
     *                        default: Color.WHITE
     */
    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * @param drawableTintOnSelection sets button's drawable tint when selector is on the button
     *                                default: Color.GRAY
     */
    public void setDrawableTintOnSelection(int drawableTintOnSelection) {
        this.drawableTintOnSelection = drawableTintOnSelection;
    }

    /**
     * @param textColorOnSelection sets button's text color when selector is on the button
     *                             default: Color.GRAY
     */
    public void setTextColorOnSelection(int textColorOnSelection) {
        this.textColorOnSelection = textColorOnSelection;
    }

    /**
     * @param ripple applies android's default ripple on layout
     */
    public void setRipple(boolean ripple) {
        this.ripple = ripple;
    }

    /**
     * @param rippleColor sets ripple color and adds ripple when a button is hovered
     *                    default: Color.GRAY
     */
    public void setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
    }

    /**
     * @param hasRippleColor if true ripple will be shown.
     *                       if setRipple(boolean) is also set to false, there will be no ripple
     */
    public void setRippleColor(boolean hasRippleColor) {
        this.hasRippleColor = hasRippleColor;
    }

    /**
     * @param radius determines how round layout's corners should be
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * @param dividerPadding adjusts divider's top and bottom distance to its container
     */
    public void setDividerPadding(int dividerPadding) {
        this.dividerPadding = dividerPadding;
    }

    /**
     * @param animateSelectorDuration sets how long selector animation should last
     */
    public void setSelectorAnimationDuration(int animateSelectorDuration) {
        this.animateSelectorDuration = animateSelectorDuration;
    }

    /**
     * @param animateSelector is used to give an animation to selector with the given interpolator constant
     */
    public void setSelectorAnimation(int animateSelector) {
        this.animateSelector = animateSelector;
    }

    /**
     * @param interpolatorSelector is used to give an animation to selector with the given one of android's interpolator.
     *                             Ex: {@link FastOutSlowInInterpolator}, {@link BounceInterpolator}, {@link LinearInterpolator}
     */
    public void setInterpolatorSelector(Interpolator interpolatorSelector) {
        this.interpolatorSelector = interpolatorSelector;
    }

    /**
     * @param dividerColor changes divider's color with the given one
     *                     default: Color.WHITE
     */
    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        RoundHelper.makeDividerRound(dividerContainer, dividerColor, dividerRadius, dividerSize, dividerBackgroundDrawable);
    }

    /**
     * @param dividerSize sets thickness of divider
     *                    default: 0
     */
    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
        RoundHelper.makeDividerRound(dividerContainer, dividerColor, dividerRadius, dividerSize, dividerBackgroundDrawable);
    }

    /**
     * @param dividerRadius determines how round divider should be
     *                      default: 0
     */
    public void setDividerRadius(int dividerRadius) {
        this.dividerRadius = dividerRadius;
        RoundHelper.makeDividerRound(dividerContainer, dividerColor, dividerRadius, dividerSize, dividerBackgroundDrawable);
    }

    /**
     * @param hasDivider if true divider will be shown.
     */
    public void setDivider(boolean hasDivider) {
        this.hasDivider = hasDivider;
    }

    /**
     * @param borderSize sets thickness of border
     *                   default: 0
     */
    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    /**
     * @param borderColor sets border color to the given one
     *                    default: Color.BLACK
     */
    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }


    /**
     * @param hasDrawableTintOnSelection if it is set to true it will give tint on chosen button's drawable when selection occurs
     */
    public void setDrawableTintOnSelection(boolean hasDrawableTintOnSelection) {
        this.hasDrawableTintOnSelection = hasDrawableTintOnSelection;
    }

    /**
     * @param hasTextColorOnSelection if it is set to true it text color will change when selection occurs
     */
    public void setTextColorOnSelection(boolean hasTextColorOnSelection) {
        this.hasTextColorOnSelection = hasTextColorOnSelection;
    }

    /**
     * GETTERS
     **/
    public int getTextColorOnSelection() {
        return textColorOnSelection;
    }

    public int getDividerSize() {
        return dividerSize;
    }

    public int getRippleColor() {
        return rippleColor;
    }

    public int getSelectorColor() {
        return selectorColor;
    }

    public int getSelectorAnimation() {
        return animateSelector;
    }

    public int getSelectorAnimationDuration() {
        return animateSelectorDuration;
    }

    public int getPosition() {
        return position;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public int getDrawableTintOnSelection() {
        return drawableTintOnSelection;
    }

    public float getRadius() {
        return radius;
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public float getDividerRadius() {
        return dividerRadius;
    }

    public boolean isHasDivider() {
        return hasDivider;
    }

    public boolean isHasRippleColor() {
        return hasRippleColor;
    }

    public boolean isRipple() {
        return ripple;
    }

    public Interpolator getInterpolatorSelector() {
        return interpolatorSelector;
    }

    private void setRippleState(boolean state) {
        for (View v : ripples) {
            setRipple(v, state);
        }
    }

    private void setEnabledAlpha(boolean enabled) {
        float alpha = 1f;
        if (!enabled)
            alpha = 0.5f;

        setAlpha(alpha);
    }


    /**
     * @param enabled set it to:
     *                false, if you want buttons to be unclickable and add grayish looking which gives disabled look,
     *                true, if you want buttons to be clickable and remove grayish looking
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setRippleState(enabled);
        setEnabledAlpha(enabled);
    }

    /**
     * @param clickable set it to:
     *                  false for unclickable buttons,
     *                  true for clickable buttons
     */
    @Override
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
        setRippleState(clickable);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("state", super.onSaveInstanceState());
        bundle.putInt("position", position);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            position = bundle.getInt("position");
            state = bundle.getParcelable("state");
        }
        super.onRestoreInstanceState(state);
    }
}
