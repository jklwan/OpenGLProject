package com.chends.opengl.renderer.window;

import android.opengl.GLES20;

import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2019/12/10.
 */
public class SquareRenderer extends BaseRenderer {

    private final float[] TriangleCoords = {
            -0.7f, 0.5f, 0.0f,
            -0.2f, 0.5f, 0.0f,
            -0.7f, -0.5f, 0.0f,
            -0.2f, -0.5f, 0.0f,

            0.2f, 0.5f, 0.0f,
            0.7f, 0.5f, 0.0f,
            0.2f, -0.5f, 0.0f,
            0.7f, -0.5f, 0.0f,
    };

    private short[] drawOrder = {
            4, 5, 6, // 第一个三角形
            5, 6, 7 // 第二个三角形
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
        // 绘制矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        ShortBuffer drawListBuffer = OpenGLUtil.createShortBuffer(drawOrder);
        // 绘制矩形
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
