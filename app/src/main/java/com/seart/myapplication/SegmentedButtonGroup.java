package com.seart.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
        init(context);
    }

    public SegmentedButtonGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SegmentedButtonGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SegmentedButtonGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private View view;
    private Context context;
    private LinearLayout mainGroup, leftGroup, rightGroup;

    private void init(final Context context) {
        view = inflate(getContext(), R.layout.test_group, this);

        if (!isInEditMode())
            this.context = context;

        mainGroup = (LinearLayout) view.findViewById(R.id.main_view);
        leftGroup = (LinearLayout) view.findViewById(R.id.left_view);
        rightGroup = (LinearLayout) view.findViewById(R.id.right_view);

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

        AnimationCollapse.expand(leftGroup, 500, width * (position - 1));
        AnimationCollapse.expand(rightGroup, 500, width * position);
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
                SegmentedButton segmentedButton_left = new SegmentedButton(context);
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
                SegmentedButton segmentedButton_right = new SegmentedButton(context);
                segmentedButton_right.clone(segmentedButton_main);

                // segmentedButton_right.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                segmentedButton_right.setImageColor(ContextCompat.getColor(context, android.R.color.black));

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
}
