package android.johanmagnusson.se.utilitypanel.Util;

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class AnimUtils {
    // Big thanks to Nick Butcher :) https://github.com/nickbutcher

    private static Interpolator linearOutSlowIn;

    private AnimUtils() {}

    public static Interpolator getLinearOutSlowInInterpolator(Context context) {
        if (linearOutSlowIn == null) {
            linearOutSlowIn = AnimationUtils.loadInterpolator(context, android.R.interpolator.linear_out_slow_in);
        }
        return linearOutSlowIn;
    }
}
