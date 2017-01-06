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
import android.graphics.drawable.GradientDrawable;
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
    private FrameLayout.LayoutParams leftBitmapParams, rightBitmapParams;

    private void init(AttributeSet attrs) {
        getAttributes(attrs);
        inflate(getContext(), R.layout.ceryle_segmented_group, this);

        mainGroup = (LinearLayout) findViewById(R.id.main_view);
        leftGroup = (ImageView) findViewById(R.id.left_view);
        rightGroup = (ImageView) findViewById(R.id.right_view);
        roundedLayout = (RoundedCornerLayout) findViewById(R.id.ceryle_test_group_roundedCornerLayout);
        rippleContainer = (LinearLayout) findViewById(R.id.rippleContainer);
        dividerContainer = (LinearLayout) findViewById(R.id.dividerContainer);

        leftBitmapParams = (FrameLayout.LayoutParams) leftGroup.getLayoutParams();
        rightBitmapParams = (FrameLayout.LayoutParams) rightGroup.getLayoutParams();

        initInterpolations();
        setShadowAttrs();
        setContainerAttrs();
        setDividerAttrs();
        setBorderAttrs();
    }

    private RelativeLayout.LayoutParams borderParams;

    private View borderView;

    private void setBorderAttrs() {
        borderView = findViewById(R.id.border);
        borderParams = (RelativeLayout.LayoutParams) borderView.getLayoutParams();
        borderParams.setMargins(margin - borderSize, margin - borderSize, margin - borderSize, margin - borderSize);

        if (borderSize > 0) {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(borderColor);
            gd.setCornerRadius(radius + 3); // TODO

            Util.setBackground(borderView, gd);
        }
    }

    private int margin;

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

    private float buttonWidth = 0;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!changed) return;

        float buttonHeight = (getHeight() - margin * 2);

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        sizeChanged = true;
    }

    private int layoutSize = 0;

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

    private boolean sizeChanged = false;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (!isInEditMode() && sizeChanged) {
            updateViews();
            sizeChanged = false;
        }
    }

    private void setBackgroundColor(View v, Drawable d, int c) {
        if (null != d) {
            Util.setBackground(v, d);
        } else {
            v.setBackgroundColor(c);
        }
    }

    private void setDividerAttrs() {
        if (!hasDivider)
            return;
        dividerContainer.setShowDividers(SHOW_DIVIDER_MIDDLE);
        // Divider Views
        Util.roundDivider(dividerContainer, dividerColor, dividerRadius, dividerSize, dividerBackgroundDrawable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            dividerContainer.setDividerPadding(dividerPadding);
        }
    }

    private ArrayList<ButtonAttributes> btnAttrs = new ArrayList<>();

    /**
     * Call it when you change your SegmentedButton or its Group
     */
    public void updateViews() {
        // Stage - I
        setBackgroundColor(mainGroup, backgroundDrawable, backgroundColor);

        // State - II
        leftGroup.setImageBitmap(getViewBitmap(mainGroup));
        for (int i = 0; i < buttons.size(); i++) {
            Button b = buttons.get(i);
            ButtonAttributes attrs = btnAttrs.get(i);

            attrs.setTextColor(b, textColorOnSelection, hasTextColorOnSelection);
            attrs.setTintColor(b, drawableTintOnSelection, hasDrawableTintOnSelection);
        }

        setBackgroundColor(mainGroup, selectorBackgroundDrawable, selectorColor);
        rightGroup.setImageBitmap(getViewBitmap(mainGroup));

        // State - III
        for (int i = 0; i < buttons.size(); i++) {
            ButtonAttributes.setAttributes(buttons.get(i), btnAttrs.get(i));
        }
        setBackgroundColor(mainGroup, backgroundDrawable, backgroundColor);
    }

    private void toggle(int position, int duration, boolean isToggledByTouch) {
        int w1 = 0, w2 = 0;
        if (hasWidth) {
            int pos = position + 1;
            for (int i = 0; i < pos; i++) {
                if (i != 0)
                    w1 += btnAttrs.get(i - 1).getWidth();
                w2 += btnAttrs.get(i).getWidth();
            }

            w2 -= dividerSize;

        } else {
            w1 = (int) (buttonWidth * position);
            w2 = (int) (buttonWidth * (position + 1));
        }
        expand(leftGroup, interpolatorSelector, duration, Math.max(0, w1));
        expand(rightGroup, interpolatorSelector, duration, Math.max(0, w2));

        if (null != onClickedButtonPosition && isToggledByTouch)
            onClickedButtonPosition.onClickedButtonPosition(position);

        if (null != onPositionChanged)
            onPositionChanged.onPositionChanged(position);

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
                if (s.hasWeight()) {
                    buttonAttributes.setHasWeight(true);
                    buttonAttributes.setWeight(s.getWeight());
                    hasWidth = true;
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


    ArrayList<View> ripples = new ArrayList<>();

    private void initForeground(final int pos) {
        ButtonAttributes attrs = btnAttrs.get(pos);

        /**
         * Ripple
         * **/
        View rippleView = new View(getContext());
        attrs.setRippleView(rippleView);
        rippleView.setLayoutParams(new LinearLayout.LayoutParams(attrs.getWidth(), 0, attrs.getWeight()));

        rippleContainer.addView(rippleView);

        rippleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickable && enabled)
                    toggle(pos, animateSelectorDuration, true);
            }
        });

        setRipple(rippleView, enabled && clickable);
        if (!enabled) {
            setEnabledAlpha(enabled);
        }

        ripples.add(rippleView);

        /**
         * Divider
         * **/
        if (!hasDivider)
            return;

        View dividerView = new View(getContext());
        dividerView.setLayoutParams(new LinearLayout.LayoutParams(attrs.getWidth() - dividerSize, 0, attrs.getWeight()));
        dividerContainer.addView(dividerView);
    }

    private void setRipple(View v, boolean isClickable) {
        if (isClickable) {
            if (hasRippleColor)
                Util.setRipple(v, rippleColor);
            else if (ripple)
                Util.setSelectableItemBackground(getContext(), v);
            else {
                for (Button button : buttons) {
                    if (button instanceof SegmentedButton && ((SegmentedButton) button).hasRipple())
                        Util.setRipple(v, ((SegmentedButton) button).getRippleColor());
                }
            }
        } else {
            Util.setBackground(v, null);
        }
    }

    private void setContainerAttrs() {
        if (isInEditMode())
            mainGroup.setBackgroundColor(backgroundColor);
    }

    private ArrayList<Button> buttons = new ArrayList<>();

    private int selectorColor, animateSelector, animateSelectorDuration, position, backgroundColor, dividerColor, drawableTintOnSelection, textColorOnSelection, dividerSize, rippleColor, dividerPadding, dividerRadius, shadowMargin, shadowMarginTop, shadowMarginBottom, shadowMarginLeft, shadowMarginRight, borderSize, borderColor;
    private float shadowElevation, radius;
    private boolean clickable, enabled, shadow, ripple, hasRippleColor, hasDivider, hasDrawableTintOnSelection, hasTextColorOnSelection;

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

        shadow = typedArray.getBoolean(R.styleable.SegmentedButtonGroup_sbg_shadow, false);
        shadowElevation = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowElevation, 0);
        shadowMargin = typedArray.getDimensionPixelSize(R.styleable.SegmentedButtonGroup_sbg_shadowMargin, -1);
        shadowMarginTop = typedArray.getDimensionPixelSize(R.styleable.SegmentedButtonGroup_sbg_shadowMarginTop, 0);
        shadowMarginBottom = typedArray.getDimensionPixelSize(R.styleable.SegmentedButtonGroup_sbg_shadowMarginBottom, 0);
        shadowMarginLeft = typedArray.getDimensionPixelSize(R.styleable.SegmentedButtonGroup_sbg_shadowMarginLeft, 0);
        shadowMarginRight = typedArray.getDimensionPixelSize(R.styleable.SegmentedButtonGroup_sbg_shadowMarginRight, 0);

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


    private OnPositionChanged onPositionChanged;

    /**
     * @param onPositionChanged set your instance that you have created to listen any position change
     */
    public void setOnPositionChanged(OnPositionChanged onPositionChanged) {
        this.onPositionChanged = onPositionChanged;
    }

    /**
     * Use this listener if you want to know any position change.
     * Listener is called when one of segmented button is clicked or setPosition is called.
     */
    public interface OnPositionChanged {
        void onPositionChanged(int position);
    }


    private OnClickedButtonPosition onClickedButtonPosition;

    /**
     * @param onClickedButtonPosition set your instance that you have created to listen clicked positions
     */
    public void setOnClickedButtonPosition(OnClickedButtonPosition onClickedButtonPosition) {
        this.onClickedButtonPosition = onClickedButtonPosition;
    }

    /**
     * Use this listener if  you want to know which button is clicked.
     * Listener is called when one of segmented button is clicked
     */
    public interface OnClickedButtonPosition {
        void onClickedButtonPosition(int position);
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
     * @param shadow if it is set to true, it will show android's default shadow
     */
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    /**
     * @param shadowElevation adds elevation to layout. elevation is not working for pre-21 devices
     *                        default: 0
     */
    public void setShadowElevation(float shadowElevation) {
        this.shadowElevation = shadowElevation;
    }

    /**
     * @param shadowMargin to make elevation visible this margin should be used
     */
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
        Util.roundDivider(dividerContainer, dividerColor, dividerRadius, dividerSize, dividerBackgroundDrawable);
    }

    /**
     * @param dividerSize sets thickness of divider
     *                    default: 0
     */
    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
        Util.roundDivider(dividerContainer, dividerColor, dividerRadius, dividerSize, dividerBackgroundDrawable);
    }

    /**
     * @param dividerRadius determines how round divider should be
     *                      default: 0
     */
    public void setDividerRadius(int dividerRadius) {
        this.dividerRadius = dividerRadius;
        Util.roundDivider(dividerContainer, dividerColor, dividerRadius, dividerSize, dividerBackgroundDrawable);
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


    public void setDrawableTintOnSelection(boolean hasDrawableTintOnSelection) {
        this.hasDrawableTintOnSelection = hasDrawableTintOnSelection;
    }

    public void setTextColorOnSelection(boolean hasTextColorOnSelection) {
        this.hasTextColorOnSelection = hasTextColorOnSelection;
    }

    /**
     * GETTERS
     **/
    public float getShadowMarginTop() {
        return shadowMarginTop;
    }

    public int getTextColorOnSelection() {
        return textColorOnSelection;
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

    public int getDrawableTintOnSelection() {
        return drawableTintOnSelection;
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

    private void setRippleState(boolean state) {
        for (View v : ripples) {
            setRipple(v, state);
        }
    }

    private void setEnabledAlpha(boolean enabled) {
        float alpha = 1f;
        if (!enabled)
            alpha = 0.5f;

        dividerContainer.setAlpha(alpha);
        borderView.setAlpha(alpha);
        roundedLayout.setAlpha(alpha);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setRippleState(enabled);
        setEnabledAlpha(enabled);
    }

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
