package com.seart.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
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
import android.view.animation.PathInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by EGE on 20.8.2016.
 */
public class SegmentedButtonGroup extends RelativeLayout {
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
        init(attrs);
    }

    private LinearLayout mainGroup, leftGroup, rightGroup;

    private void init(AttributeSet attrs) {
        getAttributes(attrs);

        View view = inflate(getContext(), R.layout.test_group, this);

        mainGroup = (LinearLayout) view.findViewById(R.id.main_view);
        leftGroup = (LinearLayout) view.findViewById(R.id.left_view);
        rightGroup = (LinearLayout) view.findViewById(R.id.right_view);

        initInterpolations();
    }

    private LinearLayout.LayoutParams buttonParams;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            if (null == buttonParams)
                buttonParams = new LinearLayout.LayoutParams(getWidth() / segmentedButtons.size(), LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < segmentedButtonsLeft.size(); i++) {
                segmentedButtonsLeft.get(i).setLayoutParams(buttonParams);
                segmentedButtonsRight.get(i).setLayoutParams(buttonParams);
            }
        }
    }

    private void toggleSegmentedButton(int position) {
        final int width = getWidth() / segmentedButtons.size();

        AnimationCollapse.expand(leftGroup,interpolatorSelector, animateSelectorDuration, width * (position - 1));
        AnimationCollapse.expand(rightGroup,interpolatorSelector, animateSelectorDuration, width * position);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mainGroup == null || leftGroup == null || rightGroup == null) {
            super.addView(child, index, params);
        } else {
            SegmentedButton segmentedButton_main = (SegmentedButton) child;

            mainGroup.addView(child, index, params);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
            segmentedButton_main.setLayoutParams(param);

            segmentedButtons.add(segmentedButton_main);

            final int c = segmentedButtons.size();
            segmentedButton_main.setOnClickedButton(new SegmentedButton.OnClickedButton() {
                @Override
                public void onClickedButton(View view) {
                    toggleSegmentedButton(c);
                }
            });

            if (!isInEditMode()) {

                // Left button group
                SegmentedButton segmentedButton_left = new SegmentedButton(getContext());
                segmentedButton_left.clone(segmentedButton_main);

                leftGroup.addView(segmentedButton_left, index, params);
                segmentedButton_left.setLayoutParams(param);
                segmentedButtonsLeft.add(segmentedButton_left);

                segmentedButton_left.setOnClickedButton(new SegmentedButton.OnClickedButton() {
                    @Override
                    public void onClickedButton(View view) {
                        toggleSegmentedButton(c);
                    }
                });

                // Right button group
                SegmentedButton segmentedButton_right = new SegmentedButton(getContext());
                segmentedButton_right.clone(segmentedButton_main);
                segmentedButton_right.setSelectorColor(selectorColor);

                // segmentedButton_right.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                segmentedButton_right.setImageColor(ContextCompat.getColor(getContext(), android.R.color.black));

                rightGroup.addView(segmentedButton_right, index, params);
                segmentedButton_right.setLayoutParams(param);
                segmentedButtonsRight.add(segmentedButton_right);

                segmentedButton_right.setOnClickedButton(new SegmentedButton.OnClickedButton() {
                    @Override
                    public void onClickedButton(View view) {
                        toggleSegmentedButton(c);
                    }
                });
            }
        }
    }

    ArrayList<SegmentedButton> segmentedButtons = new ArrayList<>();
    ArrayList<SegmentedButton> segmentedButtonsLeft = new ArrayList<>();
    ArrayList<SegmentedButton> segmentedButtonsRight = new ArrayList<>();

    private int dividerColor, selectorColor, animateImages, animateTexts, animateImagesDuration
            , animateTextsDuration, animateSelector, animateSelectorDuration, animateImagesExit, animateImagesExitDuration
            , animateTextsExit, animateTextsExitDuration, position;

    private float dividerSize, dividerRadius, dividerPadding, shadowElevation,
            shadowMargin, shadowMarginTop, shadowMarginBottom, shadowMarginLeft, shadowMarginRight, radius;

    private boolean shadow;

    private int lastPosition = 0;

    private void getAttributes(AttributeSet attrs) {
        /** GET ATTRIBUTES FROM XML **/
        // Custom attributes
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SegmentedButtonGroup);

        selectorColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_selectorColor, Color.BLUE);
        animateSelector = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateSelector, 0);
        animateSelectorDuration = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateSelectorDuration, 500);

        dividerSize = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_dividerSize, 3);
        dividerRadius = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_dividerRadius, 0);
        dividerPadding = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_dividerPadding, 30);
        dividerColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_dividerColor, Color.GRAY);

        shadow = typedArray.getBoolean(R.styleable.SegmentedButtonGroup_sbg_shadow, false);
        shadowElevation = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowElevation, 0);
        shadowMargin = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMargin, -1);
        shadowMarginTop = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginTop, 0);
        shadowMarginBottom = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginBottom, 0);
        shadowMarginLeft = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginLeft, 0);
        shadowMarginRight = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginRight, 0);
        radius = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_radius, 0);

        animateImages = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateImages_enter, 0);
        animateImagesExit = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateImages_exit, 0);
        animateImagesDuration = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateImages_enterDuration, 500);
        animateImagesExitDuration = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateImages_exitDuration, 100);

        animateTexts = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateTexts_enter, 0);
        animateTextsExit = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateTexts_exit, 0);
        animateTextsDuration = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateTexts_enterDuration, 500);
        animateTextsExitDuration = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_animateTexts_exitDuration, 100);

        position = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_position, 0);
        lastPosition = position;

        typedArray.recycle();
    }

    private Interpolator interpolatorImage, interpolatorText, interpolatorSelector;
    private Interpolator interpolatorImageExit, interpolatorTextExit;

    private void initInterpolations() {
        ArrayList<Class> interpolatorList = new ArrayList<Class>() {{
            add(FastOutSlowInInterpolator.class); // default
            add(BounceInterpolator.class);
            add(LinearInterpolator.class);
            add(FastOutSlowInInterpolator.class);
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
            interpolatorText = (Interpolator) interpolatorList.get(animateTexts).newInstance();
            interpolatorImage = (Interpolator) interpolatorList.get(animateImages).newInstance();
            interpolatorSelector = (Interpolator) interpolatorList.get(animateSelector).newInstance();

            interpolatorTextExit = (Interpolator) interpolatorList.get(animateTextsExit).newInstance();
            interpolatorImageExit = (Interpolator) interpolatorList.get(animateImagesExit).newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
