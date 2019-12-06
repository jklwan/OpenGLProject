package com.chends.opengl.utils;

import android.text.TextUtils;
import android.util.Log;

import com.chends.opengl.BuildConfig;

/**
 * LogUtils
 */
public class LogUtil {
    public static String TAG = "LogUtils";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    public static final String APP_NAME = "opengl";

    public static int v(String msg) {
        return v("", msg);
    }

    public static int d(String msg) {
        return d("", msg);
    }

    public static int i(String msg) {
        return i("", msg);
    }

    public static int w(String msg) {
        return w("", msg);
    }

    public static int e(String msg) {
        return e("", msg);
    }

    public static int e(Throwable ex) {
        if (!DEBUG) return -1;
        if (ex == null){
            return e("", "un know error");
        }
        StackTraceElement[] ste = ex.getStackTrace();
        StringBuilder sb = new StringBuilder();
        try {
            String name = ex.getClass().getName();
            String message = ex.getMessage();
            String content = message != null ? name + ":" + message : name;
            sb.append(content).append("\n");
            for (StackTraceElement s : ste) {
                sb.append(s.toString()).append("\n");
            }
        } catch (Exception ignore) {
        }
        return e("", sb.toString());
    }

    public static int v(String tag, String msg) {
        if (!DEBUG) return -1;
        return Log.v(TextUtils.isEmpty(tag) ? APP_NAME : tag, getTracePrefix("v") + msg);
    }

    public static int d(String tag, String msg) {
        if (!DEBUG) return -1;
        return Log.d(TextUtils.isEmpty(tag) ? APP_NAME : tag, getTracePrefix("d") + msg);
    }

    public static int i(String tag, String msg) {
        if (!DEBUG) return -1;
        return Log.i(TextUtils.isEmpty(tag) ? APP_NAME : tag, getTracePrefix("i") + msg);
    }

    public static int w(String tag, String msg) {
        if (!DEBUG) return -1;
        return Log.w(TextUtils.isEmpty(tag) ? APP_NAME : tag, getTracePrefix("w") + msg);
    }

    public static int e(String tag, String msg) {
        if (!DEBUG) return -1;
        return Log.e(TextUtils.isEmpty(tag) ? APP_NAME : tag, getTracePrefix("e") + msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (!DEBUG) return -1;
        return Log.v(TextUtils.isEmpty(tag) ? APP_NAME : tag, ": " + msg, tr);
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (!DEBUG) return -1;
        return Log.d(TextUtils.isEmpty(tag) ? APP_NAME : tag, ": " + msg, tr);
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (!DEBUG) return -1;
        return Log.i(TextUtils.isEmpty(tag) ? APP_NAME : tag, ": " + msg, tr);
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (!DEBUG) return -1;
        return Log.w(TextUtils.isEmpty(tag) ? APP_NAME : tag, ": " + msg, tr);
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (!DEBUG) return -1;
        return Log.e(TextUtils.isEmpty(tag) ? APP_NAME : tag, ": " + msg, tr);
    }

    private static String getTracePrefix(String logLevel) {
        StackTraceElement[] sts = new Throwable().getStackTrace();
        StackTraceElement st = null;
        for (int i = 0; i < sts.length; i++) {
            if ((sts[i].getMethodName().equalsIgnoreCase(logLevel)) && (i + 2 < sts.length)) {
                if (sts[(i + 1)].getMethodName().equalsIgnoreCase(logLevel)) {
                    st = sts[(i + 2)];
                    break;
                }
                st = sts[(i + 1)];
                break;
            }
        }
        if (st == null) {
            return "";
        }
        String clsName = st.getClassName();
        if (clsName.contains("$")) {
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1, clsName
                    .indexOf("$"));
        } else {
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
        }
        return clsName + "-> " + st.getMethodName() + "():";
    }

}

