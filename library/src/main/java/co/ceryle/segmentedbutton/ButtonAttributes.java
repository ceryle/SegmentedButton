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

import android.widget.Button;

class ButtonAttributes {
    private int tintColor, textColor;
    private boolean hasTint;

    static void setAttributes(Button b, ButtonAttributes a) {
        b.setTextColor(a.textColor);

        if (b instanceof SegmentedButton) {
            SegmentedButton sButton = (SegmentedButton) b;

            if (a.hasTint)
                sButton.setDrawableTint(a.tintColor);
            else
                sButton.removeDrawableTint();
        }
    }

    void setTintColor(Button b, int tintColor, boolean hasTint) {
        if (b instanceof SegmentedButton) {
            SegmentedButton s = (SegmentedButton) b;

            this.tintColor = s.getDrawableTint();
            this.hasTint = s.hasDrawableTint();

            if (hasTint)
                s.setDrawableTint(tintColor);
            else if (s.hasDrawableTintOnSelection())
                s.setDrawableTint(s.getDrawableTintOnSelection());
        }
    }

    void setTextColor(Button b, int textColor, boolean hasTint) {
        this.textColor = b.getCurrentTextColor();

        if (hasTint)
            b.setTextColor(textColor);
        else if (b instanceof SegmentedButton) {
            SegmentedButton s = (SegmentedButton) b;
            if (s.hasTextColorOnSelection())
                s.setTextColor(s.getTextColorOnSelection());
        }
    }
}