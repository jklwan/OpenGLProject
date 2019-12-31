package com.chends.opengl.utils;

import android.content.res.Resources;

/**
 * @author chends create on 2019/12/31.
 */
public class DisplayUtil {
    private static float density = 0.0f;
    private static int screenWidth = 0;
    private static int screenHeight = 0;

    private static float density() {
        if (density <= 0.0f) {
            density = Resources.getSystem().getDisplayMetrics().density;
        }
        return density;
    }

    public static int dp2px(float dpValue) {
        return (int) (dpValue * density() + 0.5f);
    }

    public static int screenWidth() {
        if (screenWidth <= 0) {
            screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        }
        return screenWidth;
    }

    public static int screenHeight() {
        if (screenHeight <= 0) {
            screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        }
        return screenHeight;
    }

}
