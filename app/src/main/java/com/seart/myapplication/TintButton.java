package com.seart.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by EGE on 07/09/2016.
 */
public class TintButton extends Button {
    public TintButton(Context context) {
        super(context);
        init(null);
    }

    public TintButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TintButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TintButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        getAttributes(attrs);
        setTint(buttonImageTint);
    }

    public int getTint(){
        return buttonImageTint;
    }

    public void setTint(int color){
        int pos = 0;
        Drawable drawable = null;

        if (getCompoundDrawables().length > 0) {
            for (int i = 0; i < getCompoundDrawables().length; i++) {
                if (getCompoundDrawables()[i] != null) {
                    pos = i;
                    drawable = getCompoundDrawables()[i];
                }
            }

            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));

                if (buttonImageWidth != -1 && buttonImageHeight != -1) {
                    drawable.setBounds(0, 0, buttonImageWidth, buttonImageHeight);
                }
                if (pos == 0)
                    setCompoundDrawables(drawable, null, null, null);
                else if (pos == 1)
                    setCompoundDrawables(null, drawable, null, null);
                else if (pos == 2)
                    setCompoundDrawables(null, null, drawable, null);
                else
                    setCompoundDrawables(null, null, null, drawable);
            }
        }
    }


    private int buttonImage, buttonImageTint, buttonTextColor, buttonBackgroundColor, buttonRippleColor, buttonImageWidth, buttonImageHeight;
    private String buttonText;
    private boolean buttonRipple, hasButtonImageTint;

    private void getAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TintButton);
        buttonImageTint = typedArray.getColor(R.styleable.TintButton_buttonImageTint, 0);
        hasButtonImageTint = typedArray.hasValue(R.styleable.TintButton_buttonImageTint);
        buttonImageWidth = (int) typedArray.getDimension(R.styleable.TintButton_imageWidth, -1);
        buttonImageHeight = (int) typedArray.getDimension(R.styleable.TintButton_imageHeight, -1);
        typedArray.recycle();
    }

}
