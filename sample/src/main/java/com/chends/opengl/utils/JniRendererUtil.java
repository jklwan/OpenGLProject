package com.chends.opengl.utils;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2022/3/31.
 */
public class JniRendererUtil {
    static {
        System.loadLibrary("opengl_jni");
    }

    private final int type;

    private JniRendererUtil(int type) {
        this.type = type;
    }

    public static GLSurfaceView.Renderer create(int type) {
        return new InnerRenderer(new JniRendererUtil(type));
    }

    public native void surfaceCreated(int type);

    public native void surfaceChanged(int width, int height);

    public native void drawFrame();

    private static class InnerRenderer implements GLSurfaceView.Renderer {

        private final JniRendererUtil util;

        public InnerRenderer(JniRendererUtil util) {
            this.util = util;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            util.surfaceCreated(util.type);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            util.surfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            util.drawFrame();
        }
    }
}
