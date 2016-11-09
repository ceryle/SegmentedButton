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
import android.widget.LinearLayout;

/**
 * Created by EGE on 2.10.2016.
 */

public class ButtonAttributes {
    private int tintColor, textColor, rippleColor, width = 0;
    private float weight = 0;

    private boolean hasTintColor, hasTextColor, hasRippleColor, hasWidth, hasWeight;

    private View rippleView;
    private View dividerView;

    public View getRippleView() {
        return rippleView;
    }

    public LinearLayout.LayoutParams getRippleViewParams() {
        return (LinearLayout.LayoutParams) rippleView.getLayoutParams();
    }

    public void setRippleView(View rippleView) {
        this.rippleView = rippleView;
    }

    public View getDividerView() {
        return dividerView;
    }

    public LinearLayout.LayoutParams getDividerViewParams() {
        return (LinearLayout.LayoutParams) dividerView.getLayoutParams();
    }

    public void setDividerView(View dividerView) {
        this.dividerView = dividerView;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }


    public int getWidth() {
        return width;
    }

    public boolean hasWidth() {
        return hasWidth;
    }

    public void setHasWidth(boolean hasWidth) {
        this.hasWidth = hasWidth;
    }

    public boolean hasWeight() {
        return hasWeight;
    }

    public void setHasWeight(boolean hasWeight) {
        this.hasWeight = hasWeight;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ButtonAttributes() {
    }

    public ButtonAttributes(int width) {
        this.width = width;
    }

    public int getTintColor() {
        return tintColor;
    }

    public void setTintColor(int tintColor) {
        this.tintColor = tintColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public boolean hasTintColor() {
        return hasTintColor;
    }

    public void setTintColor(boolean hasTintColor) {
        this.hasTintColor = hasTintColor;
    }

    public boolean hasTextColor() {
        return hasTextColor;
    }

    public void setTextColor(boolean hasTextColor) {
        this.hasTextColor = hasTextColor;
    }

    public boolean hasRippleColor() {
        return hasRippleColor;
    }

    public void setRippleColor(boolean hasRippleColor) {
        this.hasRippleColor = hasRippleColor;
    }

    public int getRippleColor() {
        return rippleColor;
    }

    public void setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
    }
}