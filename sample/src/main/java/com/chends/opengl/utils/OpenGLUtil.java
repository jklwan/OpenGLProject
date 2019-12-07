package com.chends.opengl.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;

import com.chends.opengl.interfaces.BaseListener;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * @author chends create on 2019/12/6.
 */
public class OpenGLUtil {

    public static GLSurfaceView.EGLContextFactory createFactory() {
        return createFactory(null);
    }

    public static GLSurfaceView.EGLContextFactory createFactory(BaseListener<Integer> listener) {
        return new ContextFactory(listener);
    }

    private static class ContextFactory implements GLSurfaceView.EGLContextFactory {

        private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
        private BaseListener<Integer> listener;

        public ContextFactory(BaseListener<Integer> listener) {
            this.listener = listener;
        }

        @Override
        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
            EGLContext context = null;
            Integer version = null;
            try {
                context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT,
                        new int[]{EGL_CONTEXT_CLIENT_VERSION, 3, EGL10.EGL_NONE});
            } catch (Exception ex) {
                LogUtil.e(ex);
            }

            if (context == null || context == EGL10.EGL_NO_CONTEXT) {
                LogUtil.d("un support OpenGL ES 3.0 ");
                try {
                    context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT,
                            new int[]{EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE});
                } catch (Exception ex) {
                    LogUtil.e(ex);
                }
            } else {
                version = 3;
            }
            if (context == null || context == EGL10.EGL_NO_CONTEXT) {
                LogUtil.d("un support OpenGL ES 2.0 ");
            } else {
                version = 2;
            }
            if (listener != null) {
                listener.onFinish(version);
            }
            return context;
        }

        @Override
        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            if (!egl.eglDestroyContext(display, context)) {
                LogUtil.d("destroyContext false");
            }
        }
    }

    public static boolean checkOpenGL(Activity activity, int version) {
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            return am.getDeviceConfigurationInfo().reqGlEsVersion >= version;
        }
        return false;
    }

    public static boolean checkOpenGLES20(Activity activity) {
        return checkOpenGL(activity, 0x20000);
    }

    public static boolean checkOpenGLES30(Activity activity) {
        return checkOpenGL(activity, 0x30000);
    }

    public static boolean checkOpenGLES31(Activity activity) {
        return checkOpenGL(activity, 0x30001);
    }
}
