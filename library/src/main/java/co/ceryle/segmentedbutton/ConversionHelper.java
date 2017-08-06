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

import android.content.Context;

class ConversionHelper {

    static float pxToDp(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    static int dpToPx(final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    static float spToPx(final Context context, int size) {
        return size * context.getResources().getDisplayMetrics().scaledDensity;
    }
}
