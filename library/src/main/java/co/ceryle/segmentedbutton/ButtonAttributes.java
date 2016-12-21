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

import android.view.View;
import android.widget.Button;

class ButtonAttributes {
    private int tintColor, textColor, rippleColor, width = 0;
    private float weight = 0;
    private boolean hasTint, hasTextColor, hasRippleColor, hasWidth, hasWeight;
    private View rippleView;


    static void setAttributes(Button b, ButtonAttributes a) {
        b.setTextColor(a.getTextColor());

        if (b instanceof SegmentedButton) {
            SegmentedButton sButton = (SegmentedButton) b;

            if (a.hasTintColor())
                sButton.setDrawableTint(a.getTintColor());
            else
                sButton.removeDrawableTint();
        }
    }


    View getRippleView() {
        return rippleView;
    }

    void setRippleView(View rippleView) {
        this.rippleView = rippleView;
    }

    float getWeight() {
        return weight;
    }

    void setWeight(float weight) {
        this.weight = weight;
    }

    int getWidth() {
        return width;
    }

    boolean hasWidth() {
        return hasWidth;
    }

    void setHasWidth(boolean hasWidth) {
        this.hasWidth = hasWidth;
    }

    boolean hasWeight() {
        return hasWeight;
    }

    void setHasWeight(boolean hasWeight) {
        this.hasWeight = hasWeight;
    }

    void setWidth(int width) {
        this.width = width;
    }

    int getTintColor() {
        return tintColor;
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

    int getTextColor() {
        return textColor;
    }

    void setTextColor(Button b, int textColor_new) {
        this.textColor = b.getCurrentTextColor();
        b.setTextColor(textColor_new);
    }

    boolean hasTintColor() {
        return hasTint;
    }

    void setTintColor(boolean hasTintColor) {
        this.hasTint = hasTintColor;
    }

    boolean hasTextColor() {
        return hasTextColor;
    }

    void setTextColor(boolean hasTextColor) {
        this.hasTextColor = hasTextColor;
    }

    boolean hasRippleColor() {
        return hasRippleColor;
    }

    void setRippleColor(boolean hasRippleColor) {
        this.hasRippleColor = hasRippleColor;
    }

    int getRippleColor() {
        return rippleColor;
    }

    void setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
    }
}