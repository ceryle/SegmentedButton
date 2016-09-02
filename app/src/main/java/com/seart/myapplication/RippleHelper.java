package com.seart.myapplication;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.View;

/**
 * Created by EGE on 18.8.2016.
 */
public class RippleHelper {

    public static void setRipple(View view, int normalColor, int pressedColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackground(getPressedColorRippleDrawable(normalColor, pressedColor));
        } else {
            view.setBackgroundDrawable(getStateListDrawable(normalColor, pressedColor));
        }
    }

    public static Drawable createRipple(int normalColor, int pressedColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getPressedColorRippleDrawable(normalColor, pressedColor);
        } else {
            return getStateListDrawable(normalColor, pressedColor);
        }
    }

    public static StateListDrawable getStateListDrawable(int normalColor, int pressedColor) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_focused}, new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_activated}, new ColorDrawable(pressedColor));
        states.addState(new int[]{}, new ColorDrawable(normalColor));
        return states;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getPressedColorRippleDrawable(int normalColor, int pressedColor) {
        return new RippleDrawable(getPressedColorSelector(normalColor, pressedColor), getColorDrawableFromColor(normalColor), null);
    }

    public static ColorStateList getPressedColorSelector(int normalColor, int pressedColor) {
        return new ColorStateList(
                new int[][]{
                        new int[]{}
                },
                new int[]{
                        pressedColor
                }
        );
    }

    public static ColorDrawable getColorDrawableFromColor(int color) {
        return new ColorDrawable(color);
    }


}
