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

/**
 * Created by EGE on 03/09/2016.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

public class RoundedCornerLayout extends FrameLayout {
    private final static float CORNER_RADIUS = 6.0f;
    private float cornerRadius;

    public RoundedCornerLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CORNER_RADIUS, metrics);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ButtonOutlineProvider());
        }
    }

    //Fixes incorrect outline drawn by default
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class ButtonOutlineProvider extends ViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), cornerRadius);
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        int count = canvas.save();

        final Path path = new Path();
        path.addRoundRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), cornerRadius, cornerRadius, Path.Direction.CW);
        canvas.clipPath(path, Region.Op.REPLACE);

        canvas.clipPath(path);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(count);
    }


    public void setRadius(float radius) {
        cornerRadius = radius;
    }
}