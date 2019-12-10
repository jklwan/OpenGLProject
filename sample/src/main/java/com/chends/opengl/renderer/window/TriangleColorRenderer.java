package com.chends.opengl.renderer.window;

import android.opengl.GLES20;

import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2019/12/7.
 */
public class TriangleColorRenderer extends BaseRenderer {

    private final float[] vertices = {
            // 位置              // 颜色
            0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f
    };

    public TriangleColorRenderer() {
        vertexShaderCode =
                "attribute vec3 aPosition;" +
                        "attribute vec3 aColor;" + // 颜色变量的属性位置值为 1
                        "varying vec3 ourColor;" +
                        "void main() {" +
                        "  gl_Position = vec4(aPosition, 1.0);" +
                        "   ourColor = aColor;" +  // 将ourColor设置为我们从顶点数据那里得到的输入颜色
                        "}";
        fragmentShaderCode =
                "precision mediump float;" +
                        "varying vec3 ourColor;" +
                        "void main() {" +
                        "  gl_FragColor = vec4(ourColor, 1.0);" +
                        "}"; // 动态改变颜色
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(shaderProgram);
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        int colorHandle = GLES20.glGetAttribLocation(shaderProgram, "aColor");
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(vertices);
        GLES20.glEnableVertexAttribArray(positionHandle);
        vertexBuffer.position(0);
        // 传入顶点坐标
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, (3 + 3) * 4, vertexBuffer);

        vertexBuffer.position(3);
        GLES20.glEnableVertexAttribArray(colorHandle);
        // 传入颜色
        GLES20.glVertexAttribPointer(colorHandle, 3, GLES20.GL_FLOAT,
                false, (3 + 3) * 4, vertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length / (3 + 3));

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }
}
