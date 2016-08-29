package com.seart.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by EGE on 20.8.2016.
 */
public class SegmentedButton extends LinearLayout {
    public SegmentedButton(Context context) {
        super(context);
        init(context);
    }

    public SegmentedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SegmentedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SegmentedButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private View view;
    private Context context;
    private TextView textView;
    private ImageView imageView;
    private LinearLayout container;

    private void init(Context context) {
        view = inflate(getContext(), R.layout.test_button, this);

        if (!isInEditMode())
            this.context = context;

        container = (LinearLayout) view.findViewById(R.id.test_button_container);
        imageView = (ImageView) view.findViewById(R.id.test_button_imageView);
        textView = (TextView) view.findViewById(R.id.test_button_textView);

        imgId = R.mipmap.ic_launcher;
        textColor = android.R.color.white;
        text = "aaa";
        textSize = 24;

        imageView.setImageDrawable(ContextCompat.getDrawable(context, imgId));
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(context, textColor));
        textView.setTextSize(textSize);

        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < onClickedButtons.size(); i++) {
                    onClickedButtons.get(i).onClickedButton(view);
                }
            }
        });

        RippleHelper.setRipple(container, Color.GRAY, Color.GREEN);
    }

    private int imgId, textColor, textSize;
    private String text;

    public int getImgId() {
        return imgId;
    }

    public String getText() {
        return text;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getTextSize() {
        return textSize;
    }


    public void setBackgroundColor(int color) {
        container.setBackgroundColor(color);
    }

    public void setImageColor(int color) {
        imageView.setColorFilter(color);
    }

    public void clone(SegmentedButton segmentedButton) {
        // ImageView
        imageView.setImageDrawable(ContextCompat.getDrawable(context, segmentedButton.getImgId()));

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams2);


        // TextView
        textView.setText(segmentedButton.getText());
        textView.setTextColor(ContextCompat.getColor(context, segmentedButton.getTextColor()));
        textView.setTextSize(segmentedButton.getTextSize());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);

    }


    private ArrayList<OnClickedButton> onClickedButtons = new ArrayList<>();

    public void setOnClickedButton(OnClickedButton onClickedButton) {
        onClickedButtons.add(onClickedButton);
    }

    public interface OnClickedButton {
        void onClickedButton(View view);
    }


}
