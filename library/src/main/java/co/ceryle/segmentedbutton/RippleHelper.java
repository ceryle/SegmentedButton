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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.view.View;

class RippleHelper {

    static void setSelectableItemBackground(Context context, View view) {
        int[] attrs = new int[]{android.R.attr.selectableItemBackground};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0 /* index */);
        ta.recycle();
        BackgroundHelper.setBackground(view, drawableFromTheme);
    }

    static void setRipple(View view, int pressedColor) {
        setRipple(view, pressedColor, null);
    }

    static void setRipple(View view, int pressedColor, Integer normalColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackground(getRippleDrawable(pressedColor, normalColor));
        } else {
            view.setBackgroundDrawable(getStateListDrawable(pressedColor, normalColor));
        }
    }

    private static StateListDrawable getStateListDrawable(int pressedColor, Integer normalColor) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_focused}, new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_activated}, new ColorDrawable(pressedColor));
        if (null != normalColor)
            states.addState(new int[]{}, new ColorDrawable(normalColor));
        return states;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Drawable getRippleDrawable(int pressedColor, Integer normalColor) {
        ColorStateList colorStateList = getPressedColorSelector(pressedColor);
        Drawable mask, content = null;

        if (null == normalColor) {
            mask = new ShapeDrawable();
        } else {
            content = new ColorDrawable(normalColor);
            mask = getRippleMask(Color.WHITE);
        }
        return new RippleDrawable(colorStateList, content, mask);
    }

    private static ColorStateList getPressedColorSelector(int pressedColor) {
        return new ColorStateList(
                new int[][]{
                        new int[]{}
                },
                new int[]{
                        pressedColor
                }
        );
    }

    private static Drawable getRippleMask(int color) {
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape());
        shapeDrawable.getPaint().setColor(color);
        return shapeDrawable;
    }
}
