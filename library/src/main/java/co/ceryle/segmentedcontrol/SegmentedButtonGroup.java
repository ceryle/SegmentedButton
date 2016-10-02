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
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
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

import com.ceryle.segmentedcontrol.R;

import java.util.ArrayList;

import co.ceryle.segmentedcontrol.util.AnimationCollapse;
import co.ceryle.segmentedcontrol.util.RippleHelper;
import co.ceryle.segmentedcontrol.util.RoundHelper;

/**
 * Created by EGE on 20.8.2016.
 */
public class SegmentedButtonGroup extends LinearLayout {

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
    private RoundedCornerLayout roundedLayout;

    private void init(AttributeSet attrs) {
        getAttributes(attrs);
        inflate(getContext(), R.layout.ceryle_segmented_group, this);

        mainGroup = (LinearLayout) findViewById(R.id.main_view);
        leftGroup = (ImageView) findViewById(R.id.left_view);
        rightGroup = (ImageView) findViewById(R.id.right_view);
        roundedLayout = (RoundedCornerLayout) findViewById(R.id.ceryle_test_group_roundedCornerLayout);

        rippleContainer = (LinearLayout) findViewById(R.id.rippleContainer);
        dividerContainer = (LinearLayout) findViewById(R.id.dividerContainer);

        initInterpolations();
        setCardViewAttrs();
        setContainerAttrs();

        // pre init rippleParams
        rippleParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
        leftBitmapParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rightBitmapParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams rippleParams;
    private FrameLayout.LayoutParams leftBitmapParams, rightBitmapParams;

    private void setCardViewAttrs() {
        if (shadow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                roundedLayout.setElevation(shadowElevation);
            }
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) roundedLayout.getLayoutParams();
        if (shadowMargin != -1) {
            layoutParams.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
            margin = shadowMargin;
        } else {
            layoutParams.setMargins(shadowMarginLeft, shadowMarginTop, shadowMarginRight, shadowMarginBottom);
            margin = shadowMarginLeft + shadowMarginRight;
        }
        roundedLayout.setRadius(radius);
    }

    private int margin;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!changed) return;

        buttonWidth = (getWidth() - margin * 2) / (float) buttons.size();
        float buttonHeight = (getHeight() - margin * 2);

        rippleParams.height = (int) buttonHeight;

        leftBitmapParams.width = (int) (buttonWidth * (position));
        leftBitmapParams.height = (int) buttonHeight;
        rightBitmapParams.width = (int) (buttonWidth * (position + 1));
        rightBitmapParams.height = (int) buttonHeight;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        for (int i = 0; i < rippleViews.size(); i++) {
            rippleViews.get(i).setLayoutParams(rippleParams);
            if (hasDivider)
                dividerViews.get(i).setLayoutParams(rippleParams);
        }

        leftGroup.setLayoutParams(leftBitmapParams);
        rightGroup.setLayoutParams(rightBitmapParams);
        sizeChanged = true;
    }

    private boolean sizeChanged = false;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (!isInEditMode() && sizeChanged) {
            updateViews();
            sizeChanged = false;
        }
    }


    public void updateViews() {
        mainGroup.setBackgroundColor(backgroundColor);
        leftGroup.setImageBitmap(getViewBitmap(mainGroup));

        for (Button button : buttons) {

            ButtonAttributes btnAttr = new ButtonAttributes();
            btnAttr.setTextColor(button.getCurrentTextColor());
            button.setTextColor(selectorTextColor);

            if (button instanceof SegmentedButton) {
                SegmentedButton sButton = (SegmentedButton) button;

                // save
                btnAttr.setTintColor(sButton.getImageTint());
                btnAttr.setTintColor(sButton.hasImageTint());

                // change
                if (hasSelectorImageTint)
                    sButton.setImageTint(selectorImageTint); // group
                else if (sButton.hasSelectorTint())
                    sButton.setImageTint(sButton.getSelectedImageTint()); // personal

                if (sButton.hasSelectedTextColor())
                    sButton.setTextColor(sButton.getSelectedTextColor());
            }
            buttonAttributes.add(btnAttr);
        }

        mainGroup.setBackgroundColor(selectorColor);
        rightGroup.setImageBitmap(getViewBitmap(mainGroup));

        // set back
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            ButtonAttributes attr = buttonAttributes.get(i);

            button.setTextColor(attr.getTextColor());

            if (button instanceof SegmentedButton) {
                SegmentedButton sButton = (SegmentedButton) button;

                if (buttonAttributes.get(i).hasTintColor())
                    sButton.setImageTint(buttonAttributes.get(i).getTintColor());
                else
                    sButton.removeImageTint();
            }
        }
        mainGroup.setBackgroundColor(backgroundColor);
    }

    private ArrayList<ButtonAttributes> buttonAttributes = new ArrayList<>();


    private void toggle(int position, int duration) {
        int leftWidth = (int) (buttonWidth * (position));
        int rightWidth = (int) (buttonWidth * (position + 1));
        AnimationCollapse.expand(leftGroup, interpolatorSelector, duration, Math.max(0, leftWidth));
        AnimationCollapse.expand(rightGroup, interpolatorSelector, duration, Math.max(0, rightWidth));

        if (null != onClickedButtonPosition)
            onClickedButtonPosition.onClickedButtonPosition(position);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mainGroup == null) {
            super.addView(child, index, params);
        } else {
            child.setClickable(false);
            child.setFocusable(false);

            mainGroup.addView(child, index, params);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
            child.setLayoutParams(param);
            child.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

            if (child instanceof SegmentedButton)
                buttons.add((SegmentedButton) child);
            else
                buttons.add((Button) child);

            initRippleViews(buttons.size() - 1);
        }
    }


    private ArrayList<View> rippleViews = new ArrayList<>();
    private ArrayList<View> dividerViews = new ArrayList<>();

    private void initRippleViews(final int pos) {
        // Ripple Views
        View view = new View(getContext());
        view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
        rippleContainer.addView(view);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(pos, animateSelectorDuration);
            }
        });
        rippleViews.add(view);

        if (hasRippleColor)
            RippleHelper.setRipple(view, rippleColor);
        else if (ripple)
            RippleHelper.setSelectableItemBackground(getContext(), view);


        if (!hasDivider)
            return;
        // Divider Views
        dividerContainer.setShowDividers(SHOW_DIVIDER_MIDDLE);
        RoundHelper.makeDividerRound(dividerContainer, dividerColor, dividerRadius, dividerSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            dividerContainer.setDividerPadding(dividerPadding);
        }

        View dividerView = new View(getContext());
        dividerView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
        dividerContainer.addView(dividerView);

        dividerViews.add(dividerView);
    }


    private void setContainerAttrs() {
        RoundHelper.makeDividerRound(mainGroup, dividerColor, dividerRadius, dividerSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mainGroup.setDividerPadding(dividerPadding);
        }
        if (isInEditMode())
            mainGroup.setBackgroundColor(backgroundColor);
    }

    private ArrayList<Button> buttons = new ArrayList<>();

    private int selectorColor, animateSelector, animateSelectorDuration, position, backgroundColor, dividerColor, selectorImageTint, selectorTextColor, dividerSize, rippleColor, dividerPadding, dividerRadius, shadowMargin, shadowMarginTop, shadowMarginBottom, shadowMarginLeft, shadowMarginRight;
    private float shadowElevation, radius;
    private boolean shadow, ripple, hasRippleColor, hasDivider, hasSelectorImageTint;

    /**
     * Custom attributes
     **/
    private void getAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SegmentedButtonGroup);

        hasDivider = typedArray.hasValue(R.styleable.SegmentedButtonGroup_sbg_dividerSize);
        dividerSize = (int) typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_dividerSize, 0);
        dividerColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_dividerColor, Color.WHITE);
        dividerPadding = (int) typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_dividerPadding, 0);
        dividerRadius = (int) typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_dividerRadius, 0);

        selectorTextColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_selectorTextColor, Color.GRAY);
        selectorImageTint = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_selectorImageTint, Color.GRAY);
        hasSelectorImageTint = typedArray.hasValue(R.styleable.SegmentedButtonGroup_sbg_selectorImageTint);
        selectorColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_selectorColor, Color.GRAY);
        animateSelector = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateSelector, 0);
        animateSelectorDuration = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateSelectorDuration, 500);

        shadow = typedArray.getBoolean(R.styleable.SegmentedButtonGroup_sbg_shadow, false);
        shadowElevation = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowElevation, 0);
        shadowMargin = (int) typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMargin, -1);
        shadowMarginTop = (int) typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginTop, 0);
        shadowMarginBottom = (int) typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginBottom, 0);
        shadowMarginLeft = (int) typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginLeft, 0);
        shadowMarginRight = (int) typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginRight, 0);

        radius = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_radius, 0);
        position = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_position, 0);
        backgroundColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_backgroundColor, Color.WHITE);

        ripple = typedArray.getBoolean(R.styleable.SegmentedButtonGroup_sbg_ripple, false);
        hasRippleColor = typedArray.hasValue(R.styleable.SegmentedButtonGroup_sbg_rippleColor);
        rippleColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_rippleColor, Color.GRAY);

        typedArray.recycle();
    }

    private float buttonWidth = 0;
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


    private OnClickedButtonPosition onClickedButtonPosition;

    public void setOnClickedButtonPosition(OnClickedButtonPosition onClickedButtonPosition) {
        this.onClickedButtonPosition = onClickedButtonPosition;
    }

    public interface OnClickedButtonPosition {
        void onClickedButtonPosition(int position);
    }

    /**
     * Draw the view into a bitmap.
     */
    private Bitmap getViewBitmap(View view) {
        // setContainerAttrs();

        //Get the dimensions of the view so we can re-layout the view at its current size
        //and create a bitmap of the same size
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

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
     * SETTERS
     **/

    /*
    public void setSelectorColor(int selectorColor) {
        this.selectorColor = selectorColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setSelectorImageTint(int selectorImageTint) {
        this.selectorImageTint = selectorImageTint;
    }

    public void setSelectorTextColor(int selectorTextColor) {
        this.selectorTextColor = selectorTextColor;
    }

    public void setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
    }
    public void setShadowElevation(float shadowElevation) {
        this.shadowElevation = shadowElevation;
    }

    public void setShadowMargin(int shadowMargin) {
        this.shadowMargin = shadowMargin;
    }

    public void setShadowMarginTop(int shadowMarginTop) {
        this.shadowMarginTop = shadowMarginTop;
    }

    public void setShadowMarginBottom(int shadowMarginBottom) {
        this.shadowMarginBottom = shadowMarginBottom;
    }

    public void setShadowMarginLeft(int shadowMarginLeft) {
        this.shadowMarginLeft = shadowMarginLeft;
    }

    public void setShadowMarginRight(int shadowMarginRight) {
        this.shadowMarginRight = shadowMarginRight;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setDividerPadding(int dividerPadding) {
        this.dividerPadding = dividerPadding;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public void setHasDivider(boolean hasDivider) {
        this.hasDivider = hasDivider;
    }

    public void setHasRippleColor(boolean hasRippleColor) {
        this.hasRippleColor = hasRippleColor;
    }

    public void setRipple(boolean ripple) {
        this.ripple = ripple;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }
    */
    public void setSelectorAnimationDuration(int animateSelectorDuration) {
        this.animateSelectorDuration = animateSelectorDuration;
    }

    public void setSelectorAnimation(int animateSelector) {
        this.animateSelector = animateSelector;
    }

    public void setInterpolatorSelector(Interpolator interpolatorSelector) {
        this.interpolatorSelector = interpolatorSelector;
    }

    public void setPosition(final int position, final int duration) {
        this.position = position;
        post(new Runnable() {
            @Override
            public void run() {
                toggle(position, duration);
            }
        });
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        RoundHelper.makeDividerRound(dividerContainer, dividerColor, dividerRadius, dividerSize);
    }

    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
        RoundHelper.makeDividerRound(dividerContainer, dividerColor, dividerRadius, dividerSize);
    }

    public void setDividerRadius(int dividerRadius) {
        this.dividerRadius = dividerRadius;
        RoundHelper.makeDividerRound(dividerContainer, dividerColor, dividerRadius, dividerSize);
    }


    /**
     * GETTERS
     **/
    public float getShadowMarginTop() {
        return shadowMarginTop;
    }

    public int getSelectorTextColor() {
        return selectorTextColor;
    }

    public float getShadowElevation() {
        return shadowElevation;
    }

    public float getShadowMargin() {
        return shadowMargin;
    }

    public float getShadowMarginBottom() {
        return shadowMarginBottom;
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

    public int getSelectorImageTint() {
        return selectorImageTint;
    }

    public float getShadowMarginLeft() {
        return shadowMarginLeft;
    }

    public float getShadowMarginRight() {
        return shadowMarginRight;
    }

    public float getRadius() {
        return radius;
    }

    @Override
    public int getDividerPadding() {
        return dividerPadding;
    }

    public float getDividerRadius() {
        return dividerRadius;
    }

    public boolean isShadow() {
        return shadow;
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

    public int getMargin() {
        return margin;
    }
}