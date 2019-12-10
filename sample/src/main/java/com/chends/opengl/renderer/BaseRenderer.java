package com.chends.opengl.renderer;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2019/12/6.
 */
public class BaseRenderer implements GLSurfaceView.Renderer {
    protected int bg = Color.BLACK;

    public BaseRenderer() {
    }

    public BaseRenderer(int bg) {
        this.bg = bg;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 设置背景色
        GLES20.glClearColor(Color.red(bg) / 255.0f, Color.green(bg) / 255.0f,
                Color.blue(bg) / 255.0f, Color.alpha(bg) / 255.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置显示范围
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
