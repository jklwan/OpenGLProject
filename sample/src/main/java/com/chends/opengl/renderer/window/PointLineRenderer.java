package com.chends.opengl.renderer.window;

import android.opengl.GLES20;

import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2019/12/7.
 */
public class PointLineRenderer extends BaseRenderer {
    private final String vertexShaderCode;
    private final String fragmentShaderCode;
    private final int COORDS_PER_VERTEX = 3;
    private final float[] triangleCoords = {
            -0.9f, 0.9f, 0.0f,
            -0.9f, 0.8f, 0.0f,

            -0.8f, -0.1f, 0.0f,
            -0.6f, -0.5f, 0.0f,
            -0.5f, -0.8f, 0.0f,

            -0.4f, 0.4f, 0.0f,
            -0.2f, 0.1f, 0.0f,
            -0.0f, 0.5f, 0.0f,
            0.1f, 0f, 0.0f,
            0.3f, -0.5f, 0.0f,

            0.4f, -0.2f, 0.0f,
            0.6f, -0.5f, 0.0f,
            0.9f, -0.6f, 0.0f,
            0.9f, -0.9f, 0.0f,
    };
    private float[] color = {0.70703125f, 0.10546875f, 0.84375f, 1.0f}; // 0 到1 代表 0- 256 如 181 用0.70703125f

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public PointLineRenderer() {
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
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(shaderProgram);
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(triangleCoords);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, vertexBuffer);
        int colorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0); // 设置颜色
        // 画点
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 13); // 画点
        // 设置线宽
        GLES20.glLineWidth(18);
        // 画线，不连续的线，例如：有1,2,3,4四个点，1和2是一条线，3,4是一条线
        GLES20.glDrawArrays(GLES20.GL_LINES, 2, 4);
        // 画线，封闭的线，例如：有1,2,3,4四个点，1,2,3,4，1会连接2，2连接3，3连接4，4连接1
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 6, 4);
        // 画线，不封闭的线
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 10, 4);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}