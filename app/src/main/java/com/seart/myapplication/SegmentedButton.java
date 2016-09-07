package com.seart.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
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
        init(null);
    }

    public SegmentedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SegmentedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SegmentedButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private TextView textView;
    private ImageView imageView;
    private LinearLayout container;

    private void init(AttributeSet attrs) {
        getAttributes(attrs);

        View view = inflate(getContext(), R.layout.test_button, this);

        container = (LinearLayout) view.findViewById(R.id.test_button_container);
        imageView = (ImageView) view.findViewById(R.id.test_button_imageView);
        textView = (TextView) view.findViewById(R.id.test_button_textView);
/*
        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < onClickedButtons.size(); i++) {
                    onClickedButtons.get(i).onClickedButton(view);
                }
            }
        });
*/
        setOtherAttrs();
        setTextAttrs();
        setImageAttrs();
    }

    private int buttonImage, buttonImageTint, buttonTextColor, buttonBackgroundColor, buttonRippleColor, buttonImageWidth, buttonImageHeight;

    private String buttonText;
    private boolean buttonRipple, hasButtonImageTint;

    private void getAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SegmentedButton);

        buttonImage = typedArray.getResourceId(R.styleable.SegmentedButton_sb_image, -1);
        buttonImageTint = typedArray.getColor(R.styleable.SegmentedButton_sb_imageTint, 0);
        hasButtonImageTint = typedArray.hasValue(R.styleable.SegmentedButton_sb_imageTint);
        buttonImageWidth = (int) typedArray.getDimension(R.styleable.SegmentedButton_sb_imageWidth, -1);
        buttonImageHeight = (int) typedArray.getDimension(R.styleable.SegmentedButton_sb_imageHeight, -1);

        buttonText = typedArray.getString(R.styleable.SegmentedButton_sb_text);
        buttonTextColor = typedArray.getColor(R.styleable.SegmentedButton_sb_textColor, Color.BLACK);

        buttonRipple = typedArray.getBoolean(R.styleable.SegmentedButton_sb_ripple, false);
        buttonRippleColor = typedArray.getColor(R.styleable.SegmentedButton_sb_rippleColor, -1);

        buttonBackgroundColor = typedArray.getColor(R.styleable.SegmentedButton_sb_backgroundColor, Color.WHITE);

        typedArray.recycle();

    }

    private void setOtherAttrs() {
        container.setBackgroundColor(buttonBackgroundColor);
    }

    public void setImageSizePixel(int width, int height) {
        if (width != -1)
            imageView.getLayoutParams().width = width;
        if (height != -1)
            imageView.getLayoutParams().height = height;
    }

    private void setImageAttrs() {
        if (buttonImage != -1) {
            imageView.setVisibility(VISIBLE);
            imageView.setImageResource(buttonImage);
            if (hasButtonImageTint)
                imageView.setColorFilter(buttonImageTint);
        } else {
            imageView.setVisibility(GONE);
        }

        if (buttonImageWidth != -1)
            setImageSizePixel(buttonImageWidth, buttonImageHeight);
    }

    private void setTextAttrs() {
        textView.setText(buttonText);
        textView.setTextColor(buttonTextColor);
    }


    public int getImgId() {
        return buttonImage;
    }

    public String getText() {
        return buttonText;
    }

    public int getTextColor() {
        return buttonTextColor;
    }

    public int getBackgroundColor() {
        return buttonBackgroundColor;
    }

    public void setBackgroundColor(int color) {
        container.setBackgroundColor(color);
    }

    public void setImageColor(int color) {
        imageView.setColorFilter(color);
    }

    public void clone(SegmentedButton segmentedButton) {
        buttonText = segmentedButton.getText();
        buttonTextColor = segmentedButton.getTextColor();
        setTextAttrs();

        buttonImage = segmentedButton.getButtonImage();
        buttonImageHeight = segmentedButton.getButtonImageHeight();
        buttonImageWidth = segmentedButton.getButtonImageWidth();
        buttonImageTint = segmentedButton.getButtonImageTint();
        hasButtonImageTint = segmentedButton.isHasButtonImageTint();
        setImageAttrs();

        buttonBackgroundColor = segmentedButton.getButtonBackgroundColor();
        buttonRipple = segmentedButton.isButtonRipple();
        buttonRippleColor = segmentedButton.getButtonRippleColor();
        setOtherAttrs();
    }

    public boolean isButtonRipple() {
        return buttonRipple;
    }

    public int getButtonRippleColor() {
        return buttonRippleColor;
    }

    public boolean isHasButtonImageTint() {
        return hasButtonImageTint;
    }

    private ArrayList<OnClickedButton> onClickedButtons = new ArrayList<>();

    public void setOnClickedButton(OnClickedButton onClickedButton) {
        onClickedButtons.add(onClickedButton);
    }

    public interface OnClickedButton {
        void onClickedButton(View view);
    }

    public int getButtonImage() {
        return buttonImage;
    }

    public int getButtonImageTint() {
        return buttonImageTint;
    }

    public int getButtonImageWidth() {
        return buttonImageWidth;
    }

    public int getButtonImageHeight() {
        return buttonImageHeight;
    }

    public int getButtonBackgroundColor() {
        return buttonBackgroundColor;
    }


    protected void bounceImage(float scale, int duration, Interpolator interpolator) {
        imageView.animate().setDuration(duration).setInterpolator(interpolator).scaleXBy(scale).scaleYBy(scale);
    }

    protected void bounceText(float scale, int duration, Interpolator interpolator) {
        textView.animate().setDuration(duration).setInterpolator(interpolator).scaleXBy(scale).scaleYBy(scale);
    }
}
