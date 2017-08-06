/*
 * Copyright (C) 2016 ceryle
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

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;

class RoundHelper {

    static void makeRound(View view, int dividerColor, int dividerRadius, int dividerSize) {
        GradientDrawable gradient = getGradientDrawable(dividerColor, dividerRadius, dividerSize);
        BackgroundHelper.setBackground(view, gradient);
    }

    static void makeDividerRound(LinearLayout layout, int dividerColor, int dividerRadius, int dividerSize, Drawable drawable) {
        GradientDrawable gradient = null;
        if (null != drawable) {
            if (drawable instanceof GradientDrawable) {
                gradient = (GradientDrawable) drawable;
                gradient.setSize(dividerSize, 0);
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
        GradientDrawable gradient =
                new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{dividerColor, dividerColor});
        gradient.setShape(GradientDrawable.RECTANGLE);
        gradient.setCornerRadius(dividerRadius);
        gradient.setSize(dividerSize, 0);
        return gradient;
    }
}
