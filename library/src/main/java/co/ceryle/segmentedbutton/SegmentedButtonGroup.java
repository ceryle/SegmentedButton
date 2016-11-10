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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
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

import co.ceryle.segmentedbutton.util.AnimationCollapse;
import co.ceryle.segmentedbutton.util.RippleHelper;
import co.ceryle.segmentedbutton.util.RoundHelper;

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


        View borderView = findViewById(R.id.border);

        initInterpolations();
        setShadowAttrs();
        setContainerAttrs();
        setDividerAttrs();

        leftBitmapParams = (FrameLayout.LayoutParams) leftGroup.getLayoutParams();
        rightBitmapParams = (FrameLayout.LayoutParams) rightGroup.getLayoutParams();
        borderParams = (RelativeLayout.LayoutParams) borderView.getLayoutParams();
        borderParams.setMargins(margin - borderSize, margin - borderSize, margin - borderSize, margin - borderSize);

        if (borderSize > 0) {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(borderColor);
            gd.setCornerRadius(radius + 3);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                borderView.setBackground(gd);
            } else
                borderView.setBackgroundDrawable(gd);
        }
    }


    private FrameLayout.LayoutParams leftBitmapParams, rightBitmapParams;
    private RelativeLayout.LayoutParams borderParams;

    private void setShadowAttrs() {
        if (shadow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                roundedLayout.setElevation(shadowElevation);
            }
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) roundedLayout.getLayoutParams();
        if (shadowMargin != -1) {
            layoutParams.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
            margin = shadowMargin;
        } else {
            layoutParams.setMargins(shadowMarginLeft, shadowMarginTop, shadowMarginRight, shadowMarginBottom);
            margin = shadowMarginLeft + shadowMarginRight;
        }


        if (margin < 1 && borderSize > 0) {
            layoutParams.setMargins(borderSize, borderSize, borderSize, borderSize);
            margin = borderSize;
        }

        roundedLayout.setRadius(radius);
    }

    private int margin;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!changed) return;

        float buttonHeight = (getHeight() - margin * 2);

        // rippleParams.height = (int) buttonHeight;

        for (int i = 0; i < btnAttrs.size(); i++) {
            btnAttrs.get(i).getRippleView().getLayoutParams().height = (int) buttonHeight;
        }

        int w1 = 0, w2 = 0;

        if (hasWidth) {
            refreshButtonsWidth();
            int pos = position + 1;
            for (int i = 0; i < pos; i++) {
                if (i != 0)
                    w1 += btnAttrs.get(i - 1).getWidth();
                w2 += btnAttrs.get(i).getWidth();
            }
        } else {
            // TODO EGE
            buttonWidth = (int) ((getWidth() - margin * 2) / (float) buttons.size());
            w1 = (int) buttonWidth * position;
            w2 = (int) (buttonWidth * (position + 1));
        }

        leftBitmapParams.width = w1;
        leftBitmapParams.height = (int) buttonHeight;
        rightBitmapParams.width = w2;
        rightBitmapParams.height = (int) buttonHeight;

        borderParams.width = getWidth() + borderSize;
        borderParams.height = (int) buttonHeight + borderSize * 2;
    }

    private void refreshButtonsWidth() {
        for (int i = 0; i < btnAttrs.size(); i++) {
            ButtonAttributes attrs = btnAttrs.get(i);
            SegmentedButton button = (SegmentedButton) buttons.get(i);

            attrs.setWidth(button.getWidth());
        }
    }

    private float buttonWidth = 0;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        sizeChanged = true;
    }

    String TAG = "ABCDEFG";

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (layoutSize == 0) {
            for (ButtonAttributes b : btnAttrs) {
                if (b.hasWidth()) {
                    layoutSize += b.getWidth();
                    getLayoutParams().width = layoutSize;
                }
                if (b.hasWeight()) {
                    getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    break;
                }
            }
        }
    }

    private int layoutSize = 0;

    private boolean sizeChanged = false;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (!isInEditMode() && sizeChanged) {
            updateViews();
            sizeChanged = false;
        }
    }

    private void setBackgroundGradient(View v) {
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xFFFFFFFF, 0xFF000000});
        gd.setCornerRadius(0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackground(gd);
        } else {
            v.setBackgroundDrawable(gd);
        }
    }

    private void setBackgroundColor(View v, Drawable d, int c) {
        if (null != d) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                v.setBackground(d);
            } else {
                v.setBackgroundDrawable(d);
            }
        } else {
            v.setBackgroundColor(c);
        }
    }

    private void setDividerAttrs() {
        if (!hasDivider)
            return;
        dividerContainer.setShowDividers(SHOW_DIVIDER_MIDDLE);
        // Divider Views
        RoundHelper.makeDividerRound(dividerContainer, dividerColor, dividerRadius, dividerSize, dividerBackgroundDrawable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            dividerContainer.setDividerPadding(dividerPadding);
        }
    }

    private ArrayList<ButtonAttributes> btnAttrs = new ArrayList<>();

    public void updateViews() {
        ArrayList<ButtonAttributes> buttonAttributes = new ArrayList<>();

        setBackgroundColor(mainGroup, backgroundDrawable, backgroundColor);
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

        setBackgroundColor(mainGroup, selectorBackgroundDrawable, selectorColor);
        rightGroup.setImageBitmap(getViewBitmap(mainGroup));

        // set attrs back
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
        setBackgroundColor(mainGroup, backgroundDrawable, backgroundColor);
    }

    private void toggle(int position, int duration) {
        int w1 = 0, w2 = 0;
        if (hasWidth) {
            int pos = position + 1;
            for (int i = 0; i < pos; i++) {
                if (i != 0)
                    w1 += btnAttrs.get(i - 1).getWidth();
                w2 += btnAttrs.get(i).getWidth();
            }
        } else {
            w1 = (int) (buttonWidth * position);
            w2 = (int) (buttonWidth * (position + 1));
        }
        AnimationCollapse.expand(leftGroup, interpolatorSelector, duration, Math.max(0, w1));
        AnimationCollapse.expand(rightGroup, interpolatorSelector, duration, Math.max(0, w2));

        if (null != onClickedButtonPosition)
            onClickedButtonPosition.onClickedButtonPosition(position);
    }

    private boolean hasWidth = false;

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mainGroup == null) {
            super.addView(child, index, params);
        } else {
            child.setClickable(false);
            child.setFocusable(false);

            mainGroup.addView(child, index, params);

            ButtonAttributes buttonAttributes = new ButtonAttributes();

            if (child instanceof SegmentedButton) {
                SegmentedButton s = (SegmentedButton) child;
                if (s.hasButtonWeight()) {
                    buttonAttributes.setHasWeight(true);
                    buttonAttributes.setWeight(s.getButtonWeight());
                } else if (s.getButtonWidth() > 0) {
                    buttonAttributes.setHasWidth(true);
                    buttonAttributes.setWidth(s.getButtonWidth());
                    hasWidth = true;
                } else {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
                    child.setLayoutParams(param);

                    buttonAttributes.setHasWeight(true);
                    buttonAttributes.setWeight(1);

                }
                buttons.add((SegmentedButton) child);

            } else {
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
                child.setLayoutParams(param);
                buttons.add((Button) child);

                buttonAttributes.setHasWeight(true);
                buttonAttributes.setWeight(1);
            }
            btnAttrs.add(buttonAttributes);

            child.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            initForeground(buttons.size() - 1);
        }
    }

    private void initForeground(final int pos) {
        ButtonAttributes attrs = btnAttrs.get(pos);

        // Ripple Views
        View rippleView = new View(getContext());
        btnAttrs.get(pos).setRippleView(rippleView);
        rippleView.setLayoutParams(new LinearLayout.LayoutParams(attrs.getWidth(), 0, attrs.getWeight()));

        rippleContainer.addView(rippleView);

        rippleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(pos, animateSelectorDuration);
            }
        });

        if (hasRippleColor)
            RippleHelper.setRipple(rippleView, rippleColor);
        else if (ripple)
            RippleHelper.setSelectableItemBackground(getContext(), rippleView);
        else {
            for (Button button : buttons) {
                if (button instanceof SegmentedButton && ((SegmentedButton) button).hasRippleColor())
                    RippleHelper.setRipple(rippleView, ((SegmentedButton) button).getRippleColor());
            }
        }

        /**
         *
         * **/
        if (!hasDivider)
            return;

        View dividerView = new View(getContext());
        btnAttrs.get(pos).setDividerView(dividerView);

        dividerView.setLayoutParams(new LinearLayout.LayoutParams(attrs.getWidth() - dividerSize, 0, attrs.getWeight()));
        dividerContainer.addView(dividerView);
    }


    private void setContainerAttrs() {
        if (isInEditMode())
            mainGroup.setBackgroundColor(backgroundColor);
    }

    private ArrayList<Button> buttons = new ArrayList<>();

    private int selectorColor, animateSelector, animateSelectorDuration, position, backgroundColor, dividerColor, selectorImageTint, selectorTextColor, dividerSize, rippleColor, dividerPadding, dividerRadius, shadowMargin, shadowMarginTop, shadowMarginBottom, shadowMarginLeft, shadowMarginRight, borderSize, borderColor;
    private float shadowElevation, radius;
    private boolean shadow, ripple, hasRippleColor, hasDivider, hasSelectorImageTint;

    private Drawable backgroundDrawable, selectorBackgroundDrawable, dividerBackgroundDrawable;

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

        borderSize = (int) typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_borderSize, 0);
        borderColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_borderColor, Color.BLACK);

        backgroundDrawable = typedArray.getDrawable(R.styleable.SegmentedButtonGroup_sbg_backgroundDrawable);
        selectorBackgroundDrawable = typedArray.getDrawable(R.styleable.SegmentedButtonGroup_sbg_selectorBackgroundDrawable);
        dividerBackgroundDrawable = typedArray.getDrawable(R.styleable.SegmentedButtonGroup_sbg_dividerBackgroundDrawable);

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
}