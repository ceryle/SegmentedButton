package com.seart.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
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
import android.widget.Button;
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

        initInterpolations();
        setCardViewAttrs();
        setContainerAttrs();


        mainGroup.post(new Runnable() {
            @Override
            public void run() {
                int bColor = ((ColorDrawable) buttons.get(0).getBackground()).getColor();
                leftGroup.setImageBitmap(getViewBitmap(mainGroup));

                for (int i = 0; i < buttons.size(); i++)
                    buttons.get(i).setBackgroundColor(selectorColor);
                rightGroup.setImageBitmap(getViewBitmap(mainGroup));

                for (int i = 0; i < buttons.size(); i++)
                    buttons.get(i).setBackgroundColor(bColor);
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
                    default:
                        return false;
                }
                return true;
            }
        });
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
                buttonParams = new LinearLayout.LayoutParams(getWidth() / buttons.size(), LayoutParams.WRAP_CONTENT);
            buttonWidth = getWidth() / buttons.size() - 1;
        }
    }

    private void toggleSegmentedButton(int position) {
        int leftWidth = buttonWidth * (position - 1) - dividerSize / 2 * (position - 1);
        int rightWidth = buttonWidth * position - dividerSize / 2 * position;
        AnimationCollapse.expand(leftGroup, interpolatorSelector, animateSelectorDuration, leftWidth);
        AnimationCollapse.expand(rightGroup, interpolatorSelector, animateSelectorDuration, rightWidth);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mainGroup == null || leftGroup == null || rightGroup == null) {
            super.addView(child, index, params);
        } else {

            child.setClickable(false);
            child.setFocusable(false);

            mainGroup.addView(child, index, params);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
            child.setLayoutParams(param);

            if (position == buttons.size() - 1) {
                setAnimationAttrs();
            }

            if (child instanceof SegmentedButton) {
                segmentedButtons.add((SegmentedButton) child);
            } else {
                buttons.add((Button) child);
            }
        }
    }


    private void setContainerAttrs() {
        RoundHelper.makeDividerRound(mainGroup, Color.WHITE, 0, dividerSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mainGroup.setDividerPadding((int) 0);
        }
        mainGroup.setBackgroundColor(backgroundColor);
    }


    ArrayList<Button> buttons = new ArrayList<>();
    ArrayList<SegmentedButton> segmentedButtons = new ArrayList<>();

    private int selectorColor, animateSelector, animateSelectorDuration, position, backgroundColor, dividerColor, selectorImage, selectorImageTint, selectorTextColor, dividerSize;
    private float shadowElevation, shadowMargin, shadowMarginTop, shadowMarginBottom, shadowMarginLeft, shadowMarginRight, radius, dividerPadding, dividerRadius;
    private boolean shadow;
    private String selectorText;

    private int lastPosition = 0;


    private void getAttributes(AttributeSet attrs) {
        /** GET ATTRIBUTES FROM XML **/
        // Custom attributes
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SegmentedButtonGroup);

        dividerSize = (int) typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_dividerSize, 0);
        dividerColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_dividerColor, Color.WHITE);
        dividerPadding = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_dividerPadding, 0);
        dividerRadius = typedArray.getDimension(R.styleable.SegmentedButtonGroup_sbg_dividerRadius, 0);

        backgroundColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_backgroundColor, Color.WHITE);

        selectorTextColor = typedArray.getColor(R.styleable.SegmentedButtonGroup_sbg_selectorTextColor, Color.GRAY);
        selectorText = typedArray.getString(R.styleable.SegmentedButtonGroup_sbg_selectorText);
        selectorImage = typedArray.getResourceId(R.styleable.SegmentedButtonGroup_sbg_selectorImage, -1);
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
        lastPosition = position;

        typedArray.recycle();
    }

    int buttonWidth = -1;
    private boolean isAnimationAlreadySet = false;

    private void setAnimationAttrs() {
        if (buttons.size() > 0 && !isAnimationAlreadySet) {
            isAnimationAlreadySet = true;
            buttons.get(position).post(new Runnable() {
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


    public ArrayList<Button> getButtons() {
        return buttons;
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
