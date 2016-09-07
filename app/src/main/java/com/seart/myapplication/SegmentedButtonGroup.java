package com.seart.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

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

        View view = inflate(getContext(), R.layout.test_group, this);

        mainGroup = (LinearLayout) view.findViewById(R.id.main_view);
        leftGroup = (ImageView) view.findViewById(R.id.left_view);
        rightGroup = (ImageView) view.findViewById(R.id.right_view);
        roundedLayout = (RoundedCornerLayout) view.findViewById(R.id.ceryle_test_group_roundedCornerLayout);

        mainGroup.post(new Runnable() {
            @Override
            public void run() {
                int bColor = segmentedButtons.get(0).getBackgroundColor();
                leftGroup.setImageBitmap(getViewBitmap(mainGroup));

                for (int i = 0; i < segmentedButtons.size(); i++)
                    segmentedButtons.get(i).setBackgroundColor(selectorColor);
                rightGroup.setImageBitmap(getViewBitmap(mainGroup));

                for (int i = 0; i < segmentedButtons.size(); i++)
                    segmentedButtons.get(i).setBackgroundColor(bColor);
            }
        });


        mainGroup.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int position = (int) event.getX() / buttonWidth + 1;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        toggleSegmentedButton(position);
                        break;
                    /*case MotionEvent.ACTION_MOVE:
                        toggleSegmentedButtonX((int)event.getX());
                        break;*/
                    default:
                        return false;
                }
                return true;
            }
        });

        initInterpolations();
        setCardViewAttrs();
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
        } else {
            layoutParams.setMargins((int) shadowMarginLeft, (int) shadowMarginTop, (int) shadowMarginRight, (int) shadowMarginBottom);
        }

        roundedLayout.setRadius(radius);
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
            buttonWidth = getWidth() / segmentedButtons.size();
        }
    }

    private void toggleSegmentedButton(int position) {
        AnimationCollapse.expand(leftGroup, interpolatorSelector, animateSelectorDuration, buttonWidth * (position - 1));
        AnimationCollapse.expand(rightGroup, interpolatorSelector, animateSelectorDuration, buttonWidth * position);
    }

    /*private void toggleSegmentedButtonX(int position) {
        AnimationCollapse.expand(leftGroup, interpolatorSelector, animateSelectorDuration, position - buttonWidth);
        AnimationCollapse.expand(rightGroup, interpolatorSelector, animateSelectorDuration, position);
    }*/

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

            /*final int c = segmentedButtons.size();
            segmentedButton_main.setOnClickedButton(new SegmentedButton.OnClickedButton() {
                @Override
                public void onClickedButton(View view) {
                    toggleSegmentedButton(c);
                }
            });*/

            if (position == segmentedButtons.size() - 1) {
                setAnimationAttrs();
            }
        }
    }

    ArrayList<SegmentedButton> segmentedButtons = new ArrayList<>();
    ArrayList<SegmentedButton> segmentedButtonsLeft = new ArrayList<>();
    ArrayList<SegmentedButton> segmentedButtonsRight = new ArrayList<>();

    private int selectorColor, animateImages, animateSelector, animateSelectorDuration, position;

    private float shadowElevation,
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

        shadow = typedArray.getBoolean(R.styleable.SegmentedButtonGroup_sbg_shadow, false);
        shadowElevation = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowElevation, 0);
        shadowMargin = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMargin, -1);
        shadowMarginTop = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginTop, 0);
        shadowMarginBottom = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginBottom, 0);
        shadowMarginLeft = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginLeft, 0);
        shadowMarginRight = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_shadowMarginRight, 0);
        radius = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_radius, 0);

        position = typedArray.getInt(R.styleable.SegmentedButtonGroup_sbg_position, 0);
        lastPosition = position;

        typedArray.recycle();
    }

    int buttonWidth = -1;
    private boolean isAnimationAlreadySet = false;

    private void setAnimationAttrs() {
        if (segmentedButtons.size() > 0 && !isAnimationAlreadySet) {
            isAnimationAlreadySet = true;
            segmentedButtons.get(position).post(new Runnable() {
                @Override
                public void run() {
                    AnimationCollapse.expand(leftGroup, interpolatorSelector, 0, buttonWidth * (position - 1));
                    AnimationCollapse.expand(rightGroup, interpolatorSelector, 0, buttonWidth * position);
                }
            });
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


    public ArrayList<SegmentedButton> getSegmentedButtons() {
        return segmentedButtons;
    }


    /**
     * Draw the view into a bitmap.
     */
    private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }
}
