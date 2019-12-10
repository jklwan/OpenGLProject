package com.chends.opengl.renderer.window;

import android.opengl.GLES20;

import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2019/12/7.
 */
public class TriangleMatrixRenderer extends BaseRenderer {
    private final float[] TriangleCoords = {
            -0.9f, 0.9f, 0.0f,
            -0.1f, 0.8f, 0.0f,
            -0.5f, 0.6f, 0.0f,

            -0.4f, 0.55f, 0.0f,
            -0.3f, 0.3f, 0.0f,
            -0.8f, 0.2f, 0.0f,
            -0.2f, -0.1f, 0.0f,
            -0.9f, -0.4f, 0.0f,
            -0.3f, -0.7f, 0.0f,

            -0.1f, 0f, 0.0f,
            0.1f, 0.7f, 0.0f,
            0.9f, 0.5f, 0.0f,
            0.8f, -0.1f, 0.0f,
            0.4f, -0.5f, 0.0f,
            0.2f, -0.6f, 0.0f,
    };

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(shaderProgram);
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(TriangleCoords);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, vertexBuffer);
        int colorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor");
        // 设置颜色
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        // 画三角：每间隔三个顶点结合为一个三角
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        // 画三角：每相邻的三个顶点为一个三角
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 3, 6);
        // 画三角扇形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 9, 6);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
