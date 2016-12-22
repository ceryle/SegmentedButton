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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;

class Util {

    static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    /**
     * Ripple Utility
     **/
    static void setRipple(View view, int pressedColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackground(getPressedColorRippleDrawable(pressedColor));
        } else {
            view.setBackgroundDrawable(getStateListDrawable(pressedColor));
        }
    }

    static void setSelectableItemBackground(Context context, View view) {
        int[] attrs = new int[]{android.R.attr.selectableItemBackground};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0 /* index */);
        ta.recycle();
        setBackground(view, drawableFromTheme);
    }

    private static StateListDrawable getStateListDrawable(int pressedColor) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_focused},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_activated},
                new ColorDrawable(pressedColor));
        return states;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Drawable getPressedColorRippleDrawable(int pressedColor) {
        return new RippleDrawable(getPressedColorSelector(pressedColor), null, new ShapeDrawable());
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
    /**
     * Ripple Utility - END
     **/


    /**
     * Round Utility
     **/
    static void roundDivider(LinearLayout layout, int dividerColor, int dividerRadius, int dividerSize, Drawable drawable) {
        GradientDrawable gradient = null;
        if (null != drawable) {
            if (drawable instanceof GradientDrawable) {
                gradient = (GradientDrawable) drawable;
                if (dividerSize != 0)
                    gradient.setSize(dividerSize, 0);
                if (dividerRadius != 0)
                    gradient.setCornerRadius(dividerRadius);
            } else {
                layout.setDividerDrawable(drawable);
            }
        } else {
            gradient = getGradientDrawable(dividerColor, dividerRadius, dividerSize);
        }
        layout.setDividerDrawable(gradient);
    }

    private static GradientDrawable getGradientDrawable(int dividerColor, int dividerRadius, int dividerSize) {
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{dividerColor, dividerColor});
        gradient.setShape(GradientDrawable.RECTANGLE);
        gradient.setCornerRadius(dividerRadius);
        gradient.setSize(dividerSize, 0);
        return gradient;
    }

    /**
     * Round Utility - END
     **/
}
