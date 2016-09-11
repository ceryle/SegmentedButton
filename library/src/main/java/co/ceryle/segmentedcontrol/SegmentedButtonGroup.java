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
    private static final String TAG = "SegmentedButtonGroup";

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

    private LinearLayout mainGroup;
    private ImageView leftGroup, rightGroup;
    private RoundedCornerLayout roundedLayout;


    private void init(AttributeSet attrs) {
        getAttributes(attrs);
        inflate(getContext(), R.layout.ceryle_segmented_group, this);

        mainGroup = (LinearLayout) findViewById(R.id.main_view);
        leftGroup = (ImageView) findViewById(R.id.left_view);
        rightGroup = (ImageView) findViewById(R.id.right_view);
        roundedLayout = (RoundedCornerLayout) findViewById(R.id.ceryle_test_group_roundedCornerLayout);

        initInterpolations();
        setCardViewAttrs();
        setContainerAttrs();

    }

    private void initRippleViews() {
        LinearLayout rippleContainer = new LinearLayout(getContext());
        rippleContainer.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) buttonHeight));
        roundedLayout.addView(rippleContainer);

        // rippleContainer.setShowDividers(SHOW_DIVIDER_MIDDLE);
        // RoundHelper.makeDividerRound(rippleContainer, dividerColor, (int) dividerRadius, dividerSize);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            rippleContainer.setDividerPadding((int) dividerPadding);
        }*/

        for (int i = 0; i < buttons.size(); i++) {
            View view = new View(getContext());
            view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
            rippleContainer.addView(view);

            final int pos = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSegmentedButton(pos);
                }
            });

            if (hasRippleColor)
                RippleHelper.setRipple(view, rippleColor);
            else if (ripple)
                RippleHelper.setSelectableItemBackground(getContext(), view);
        }



        LinearLayout dividerContainer = new LinearLayout(getContext());
        dividerContainer.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) buttonHeight));

        dividerContainer.setShowDividers(SHOW_DIVIDER_MIDDLE);
        RoundHelper.makeDividerRound(dividerContainer, dividerColor, (int) dividerRadius, dividerSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            dividerContainer.setDividerPadding((int) dividerPadding);
        }

        for (int i = 0; i < buttons.size(); i++) {
            View view = new View(getContext());
            view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
            dividerContainer.addView(view);
        }
        roundedLayout.addView(dividerContainer);
    }


    private void setCardViewAttrs() {
        if (shadow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                roundedLayout.setElevation(shadowElevation);
            }
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) roundedLayout.getLayoutParams();
        if (shadowMargin != -1) {
            layoutParams.setMargins((int) shadowMargin, (int) shadowMargin, (int) shadowMargin, (int) shadowMargin);
            margin = (int) shadowMargin;
        } else {
            layoutParams.setMargins((int) shadowMarginLeft, (int) shadowMarginTop, (int) shadowMarginRight, (int) shadowMarginBottom);
            margin = (int) shadowMarginLeft + (int) shadowMarginRight;
        }
        roundedLayout.setRadius(radius);
    }

    private int margin;

    float buttonHeight;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (!changed) return;

        buttonWidth = (getWidth() - margin * 2) / (float) buttons.size();
        buttonHeight = (getHeight() - margin * 2);

        if (!callOnce) {
            initRippleViews();
            setAnimationAttrs();

            if (!isInEditMode())
                updateMovingViews();

            callOnce = true;
        }
    }

    private boolean callOnce = false;

    class ButtonAttribute {
        int imageTintColor, textColor;

        public void setImageTintColor(int imageTintColor) {
            this.imageTintColor = imageTintColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }
    }

    ArrayList<ButtonAttribute> buttonAttributes = new ArrayList<>();

    private void updateMovingViews() {
        mainGroup.setBackgroundColor(backgroundColor);
        leftGroup.setImageBitmap(getViewBitmap(mainGroup));

        for (int i = 0; i < buttons.size(); i++) {
            ButtonAttribute buttonAttribute = new ButtonAttribute();
            buttonAttribute.setTextColor(buttons.get(i).getCurrentTextColor());
            if (buttons.get(i) instanceof SegmentedButton)
                buttonAttribute.setImageTintColor(((SegmentedButton) buttons.get(i)).getImageTint());
            buttonAttributes.add(buttonAttribute);

            if (buttons.get(i) instanceof SegmentedButton)
                ((SegmentedButton) buttons.get(i)).setImageTint(selectorImageTint);
            buttons.get(i).setTextColor(selectorTextColor);
        }

        mainGroup.setBackgroundColor(selectorColor);
        rightGroup.setImageBitmap(getViewBitmap(mainGroup));

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setTextColor(buttonAttributes.get(i).textColor);

            if (buttons.get(i) instanceof SegmentedButton)
                ((SegmentedButton) buttons.get(i)).setImageTint(buttonAttributes.get(i).imageTintColor);
        }
        mainGroup.setBackgroundColor(backgroundColor);
    }

    private void toggleSegmentedButton(int position) {
        int leftWidth = (int) (buttonWidth * (position));
        int rightWidth = (int) (buttonWidth * (position + 1));
        AnimationCollapse.expand(leftGroup, interpolatorSelector, animateSelectorDuration, Math.max(0, leftWidth));
        AnimationCollapse.expand(rightGroup, interpolatorSelector, animateSelectorDuration, Math.max(0, rightWidth));

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
        }
    }

    private void setContainerAttrs() {
        RoundHelper.makeDividerRound(mainGroup, dividerColor, (int) dividerRadius, dividerSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mainGroup.setDividerPadding((int) dividerPadding);
        }
        if (isInEditMode())
            mainGroup.setBackgroundColor(backgroundColor);
    }

    ArrayList<Button> buttons = new ArrayList<>();

    private int selectorColor, animateSelector, animateSelectorDuration, position, backgroundColor, dividerColor, selectorImageTint, selectorTextColor, dividerSize, rippleColor;
    private float shadowElevation, shadowMargin, shadowMarginTop, shadowMarginBottom, shadowMarginLeft, shadowMarginRight, radius, dividerPadding, dividerRadius;
    private boolean shadow, ripple, hasRippleColor;

    /**
     * Custom attributes
     **/
    private void getAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SegmentedButtonGroup);

        dividerSize = (int) typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_dividerSize, 0);
        dividerColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_dividerColor, Color.WHITE);
        dividerPadding = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_dividerPadding, 0);
        dividerRadius = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_dividerRadius, 0);

        selectorTextColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_selectorTextColor, Color.GRAY);
        selectorImageTint = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_selectorImageTint, Color.GRAY);
        selectorColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_selectorColor, Color.GRAY);
        animateSelector = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateSelector, 0);
        animateSelectorDuration = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateSelectorDuration, 500);

        shadow = typedArray.getBoolean(R.styleable.SegmentedButtonGroup_sbg_shadow, false);
        shadowElevation = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowElevation, 0);
        shadowMargin = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMargin, -1);
        shadowMarginTop = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginTop, 0);
        shadowMarginBottom = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginBottom, 0);
        shadowMarginLeft = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginLeft, 0);
        shadowMarginRight = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginRight, 0);

        radius = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_radius, 0);
        position = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_position, 0);
        backgroundColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_backgroundColor, Color.WHITE);

        ripple = typedArray.getBoolean(R.styleable.SegmentedButtonGroup_sbg_ripple, false);
        hasRippleColor = typedArray.hasValue(R.styleable.SegmentedButtonGroup_sbg_rippleColor);
        rippleColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_rippleColor, Color.GRAY);

        typedArray.recycle();
    }

    float buttonWidth = 0;
    private boolean isAnimationAlreadySet = false;

    private void setAnimationAttrs() {
        if (buttons.size() > 0 && !isAnimationAlreadySet) {
            isAnimationAlreadySet = true;
            int leftWidth = (int) (buttonWidth * position);
            int rightWidth = (int) (buttonWidth * (position + 1));
            AnimationCollapse.expand(leftGroup, interpolatorSelector, 0, Math.max(0, leftWidth));
            AnimationCollapse.expand(rightGroup, interpolatorSelector, 0, Math.max(0, rightWidth));
        }
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
        int width = view.getWidth();
        int height = view.getHeight();

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
}
