package com.seart.myapplication;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;

/**
 * Created by EGE on 16/08/2016.
 */
public class AnimationCollapse {

    public static void expand(final View v, Interpolator interpolator, int duration, int targetWidth) {

        int prevWidth  = v.getWidth();

        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevWidth, targetWidth);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().width = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }
}