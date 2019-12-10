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

    protected String vertexShaderCode;
    protected String fragmentShaderCode;
    protected int bg;
    // 0 到1 代表 0- 256 如 181 用0.70703125f
    protected float[] color = {0.70703125f, 0.10546875f, 0.84375f, 1.0f};

    public BaseRenderer() {
        this(Color.BLACK);
    }

    public BaseRenderer(int bg) {
        this.bg = bg;
        vertexShaderCode =
                "attribute vec4 aPosition;" +
                        "void main() {" +
                        "  gl_Position = aPosition;" +
                        "  gl_PointSize = 19.0;" +
                        "}";
        fragmentShaderCode =
                "precision mediump float;" +
                        "uniform vec4 vColor;" +
                        "void main() {" +
                        "  gl_FragColor = vColor;" +
                        "}";
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
