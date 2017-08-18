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

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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

    private boolean draggable = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float selectorWidth, offsetX;
        int position = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:

                selectorWidth = (float) getWidth() / numberOfButtons / 2f;
                offsetX = ((event.getX() - selectorWidth) * numberOfButtons) / getWidth();
                position = (int) Math.floor(offsetX + 0.5);

                toggledPositionOffset = lastPositionOffset = offsetX;

                toggle(position, animateSelectorDuration, true);

                break;
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:

                if (!draggable)
                    break;

                selectorWidth = (float) getWidth() / numberOfButtons / 2f;

                offsetX = ((event.getX() - selectorWidth) * numberOfButtons) / (float) getWidth();
                position = (int) Math.floor(offsetX);
                offsetX -= position;

                if (event.getRawX() - selectorWidth < getLeft()) {
                    offsetX = 0;
                    animateViews(position + 1, offsetX);
                    break;
                }
                if (event.getRawX() + selectorWidth > getRight()) {
                    offsetX = 1;
                    animateViews(position - 1, offsetX);
                    break;
                }

                animateViews(position, offsetX);

                break;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class ButtonOutlineProvider extends ViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), radius);
        }
    }

    private void init(AttributeSet attrs) {
        getAttributes(attrs);
        setWillNotDraw(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ButtonOutlineProvider());
        }

        setClickable(true);

        buttons = new ArrayList<>();

        FrameLayout container = new FrameLayout(getContext());
        container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(container);

        mainGroup = new LinearLayout(getContext());
        mainGroup.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mainGroup.setOrientation(LinearLayout.HORIZONTAL);
        container.addView(mainGroup);

        rippleContainer = new LinearLayout(getContext());
        rippleContainer.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        rippleContainer.setOrientation(LinearLayout.HORIZONTAL);
        rippleContainer.setClickable(false);
        rippleContainer.setFocusable(false);
        rippleContainer.setPadding(borderSize, borderSize, borderSize, borderSize);
        container.addView(rippleContainer);

        dividerContainer = new LinearLayout(getContext());
        dividerContainer.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        dividerContainer.setOrientation(LinearLayout.HORIZONTAL);
        dividerContainer.setClickable(false);
        dividerContainer.setFocusable(false);
        container.addView(dividerContainer);

        initInterpolations();
        setContainerAttrs();
        setDividerAttrs();

        rectF = new RectF();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private RectF rectF;
    private Paint paint;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = canvas.getWidth();
        float height = canvas.getHeight();

        rectF.set(0, 0, width, height);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(backgroundColor);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        if (borderSize > 0) {
            float bSize = borderSize / 2f;
            rectF.set(0 + bSize, 0 + bSize, width - bSize, height - bSize);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(borderColor);
            paint.setStrokeWidth(borderSize);
            canvas.drawRoundRect(rectF, radius, radius, paint);
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

    private int numberOfButtons = 0;

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {

        if (child instanceof SegmentedButton) {
            SegmentedButton button = (SegmentedButton) child;
            final int position = numberOfButtons++;

            button.setSelectorColor(selectorColor);
            button.setSelectorRadius(radius);
            button.setBorderSize(borderSize);

            if (position == 0)
                button.hasBorderLeft(true);

            if (position > 0)
                buttons.get(position - 1).hasBorderRight(false);

            button.hasBorderRight(true);


            mainGroup.addView(child, params);
            buttons.add(button);

            if (this.position == position) {
                button.clipToRight(1);

                lastPosition = toggledPosition = position;
                lastPositionOffset = toggledPositionOffset = (float) position;
            }

            // RIPPLE
            BackgroundView rippleView = new BackgroundView(getContext());
            if (!draggable) {
                rippleView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickable && enabled)
                            toggle(position, animateSelectorDuration, true);
                    }
                });
            }

            setRipple(rippleView, enabled && clickable);
            rippleContainer.addView(rippleView,
                    new LinearLayout.LayoutParams(button.getButtonWidth(), ViewGroup.LayoutParams.MATCH_PARENT, button.getWeight()));
            ripples.add(rippleView);

            if (!hasDivider)
                return;

            BackgroundView dividerView = new BackgroundView(getContext());
            dividerContainer.addView(dividerView,
                    new LinearLayout.LayoutParams(button.getButtonWidth(), ViewGroup.LayoutParams.MATCH_PARENT, button.getWeight()));
        } else
            super.addView(child, index, params);
    }

    private ArrayList<BackgroundView> ripples = new ArrayList<>();

    private void setRipple(View v, boolean isClickable) {
        if (isClickable) {
            if (hasRippleColor)
                RippleHelper.setRipple(v, rippleColor, radius);
            else if (ripple)
                RippleHelper.setSelectableItemBackground(getContext(), v);
            else {
                for (View button : buttons) {
                    if (button instanceof SegmentedButton && ((SegmentedButton) button).hasRipple())
                        RippleHelper.setRipple(v, ((SegmentedButton) button).getRippleColor(), radius);
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

    private ArrayList<SegmentedButton> buttons;

    private int selectorColor, animateSelector, animateSelectorDuration, position, backgroundColor, dividerColor, radius,
            dividerSize, rippleColor, dividerPadding, dividerRadius, borderSize, borderColor;
    private boolean clickable, enabled, ripple, hasRippleColor, hasDivider;

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

        selectorColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_selectorColor, Color.GRAY);
        animateSelector = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateSelector, 0);
        animateSelectorDuration = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateSelectorDuration, 500);

        radius = typedArray.getDimensionPixelSize(R.styleable.SegmentedButtonGroup_sbg_radius, 0);
        position = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_position, 0);
        backgroundColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_backgroundColor, Color.TRANSPARENT);

        ripple = typedArray.getBoolean(R.styleable.SegmentedButtonGroup_sbg_ripple, false);
        hasRippleColor = typedArray.hasValue(R.styleable.SegmentedButtonGroup_sbg_rippleColor);
        rippleColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_rippleColor, Color.GRAY);

        borderSize = typedArray.getDimensionPixelSize(R.styleable.SegmentedButtonGroup_sbg_borderSize, 0);
        borderColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_borderColor, Color.BLACK);


        backgroundDrawable = typedArray.getDrawable(R.styleable.SegmentedButtonGroup_sbg_backgroundDrawable);
        selectorBackgroundDrawable = typedArray.getDrawable(R.styleable.SegmentedButtonGroup_sbg_selectorBackgroundDrawable);
        dividerBackgroundDrawable = typedArray.getDrawable(R.styleable.SegmentedButtonGroup_sbg_dividerBackgroundDrawable);

        enabled = typedArray.getBoolean(R.styleable.SegmentedButtonGroup_sbg_enabled, true);

        draggable = typedArray.getBoolean(R.styleable.SegmentedButtonGroup_sbg_draggable, false);

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
     * @param position is used to select one of segmented buttons
     */
    public void setPosition(int position) {
        this.position = position;

        if (null == buttons) {
            lastPosition = toggledPosition = position;
            lastPositionOffset = toggledPositionOffset = (float) position;
        } else {
            toggle(position, animateSelectorDuration, false);
        }
    }

    /**
     * @param position is used to select one of segmented buttons
     * @param duration determines how long animation takes to finish
     */
    public void setPosition(int position, int duration) {
        this.position = position;

        if (null == buttons) {
            lastPosition = toggledPosition = position;
            lastPositionOffset = toggledPositionOffset = (float) position;
        } else {
            toggle(position, duration, false);
        }
    }

    /**
     * @param position      is used to select one of segmented buttons
     * @param withAnimation if true default animation will perform
     */
    public void setPosition(int position, boolean withAnimation) {
        this.position = position;

        if (null == buttons) {
            lastPosition = toggledPosition = position;
            lastPositionOffset = toggledPositionOffset = (float) position;
        } else {
            if (withAnimation)
                toggle(position, animateSelectorDuration, false);
            else
                toggle(position, 1, false);
        }
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
    public void setRadius(int radius) {
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

            setPosition(position, false);
        }
        super.onRestoreInstanceState(state);
    }

    /**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */

    private int toggledPosition = 0;
    private float toggledPositionOffset = 0;

    private void toggle(int position, int duration, boolean isToggledByTouch) {
        if (!draggable && toggledPosition == position)
            return;

        toggledPosition = position;

        ValueAnimator animator = ValueAnimator.ofFloat(toggledPositionOffset, position);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = toggledPositionOffset = (float) animation.getAnimatedValue();

                int position = (int) animatedValue;
                float positionOffset = animatedValue - position;

                animateViews(position, positionOffset);

                invalidate();
            }
        });
        animator.setInterpolator(interpolatorSelector);
        animator.setDuration(duration);
        animator.start();


        if (null != onClickedButtonListener && isToggledByTouch)
            onClickedButtonListener.onClickedButton(position);

        if (null != onPositionChangedListener)
            onPositionChangedListener.onPositionChanged(position);

        this.position = position;
    }

    private int lastPosition = 0;
    private float lastPositionOffset = 0;

    private void animateViews(int position, float positionOffset) {
        float realPosition = position + positionOffset;
        float lastRealPosition = lastPosition + lastPositionOffset;


        if (realPosition == lastRealPosition) {
            return;
        }

        int nextPosition = position + 1;
        if (positionOffset == 0.0f) {
            if (lastRealPosition <= realPosition) {
                nextPosition = position - 1;
            }
        }

        if (lastPosition > position) {
            if (lastPositionOffset > 0f) {
                toNextPosition(nextPosition + 1, 1);
            }
        }

        if (lastPosition < position) {
            if (lastPositionOffset < 1.0f) {
                toPosition(position - 1, 0);
            }
        }

        toNextPosition(nextPosition, 1.0f - positionOffset);
        toPosition(position, 1.0f - positionOffset);

        lastPosition = position;
        lastPositionOffset = positionOffset;
    }

    private void toPosition(int position, float clip) {
        if (position >= 0 && position < numberOfButtons)
            buttons.get(position).clipToRight(clip);
    }

    private void toNextPosition(int position, float clip) {
        if (position >= 0 && position < numberOfButtons)
            buttons.get(position).clipToLeft(clip);
    }
}
