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
package co.ceryle.segmentedcontrol;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by EGE on 22/08/2016.
 */
public class RoundHelper {

    private static GradientDrawable getGradientDrawable(int dividerColor, int dividerRadius, int dividerSize) {
        GradientDrawable gradient =
                new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{dividerColor, dividerColor});
        gradient.setShape(GradientDrawable.RECTANGLE);
        gradient.setCornerRadius(dividerRadius);
        gradient.setSize(dividerSize, 0);
        return gradient;
    }

    public static void makeRound(View view, int dividerColor, int dividerRadius, int dividerSize) {
        GradientDrawable gradient = getGradientDrawable(dividerColor, dividerRadius, dividerSize);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            view.setBackground(gradient);
        else
            view.setBackgroundDrawable(gradient);
    }


    public static void makeDividerRound(LinearLayout layout, int dividerColor, int dividerRadius, int dividerSize){
        GradientDrawable gradient = getGradientDrawable(dividerColor, dividerRadius, dividerSize);
        layout.setDividerDrawable(gradient);
    }
}
