package com.chends.opengl.renderer.window;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2019/12/12.
 */
public class CubeRenderer extends BaseRenderer {

    /**
     * 立方体的8个顶点
     */
    private float[] CubeCoords = new float[]{
            -0.5f, 0.5f, 0.5f, // 上左前顶点
            0.5f, 0.5f, 0.5f, // 上右前顶点
            -0.5f, 0.5f, -0.5f, // 上左后顶点
            0.5f, 0.5f, -0.5f, // 上右后顶点

            -0.5f, -0.5f, 0.5f, // 下左前顶点
            0.5f, -0.5f, 0.5f, // 下右前顶点
            -0.5f, -0.5f, -0.5f, // 下左后顶点
            0.5f, -0.5f, -0.5f, // 下右后顶点
    };
    /**
     * 索引
     */
    /*private short[] indices = new short[]{
            0, 1, 2, 1, 2, 3, // 上面
            4, 5, 6, 5, 6, 7,// 下面
            0, 1, 4, 1, 4, 5, // 前面
            2, 3, 6, 3, 6, 7, // 后面
            0, 2, 4, 2, 4, 6, // 左面
            1, 3, 5, 3, 5, 7 // 右面
    };*/
    /*private short[] indices = new short[]{
            2, 3, 0, 1, 5, 3, 7, 2, 6, 0, 4, 5, 6, 7
    };*/
    private short[] indices = new short[]{
            0, 2, 3, 1, 5, 4, 6, 2
    };
    private short[] indices2 = new short[]{
            7, 6, 4, 5, 1, 3, 2, 6
    };
    /**
     * 颜色
     */
    private float[] colors = {
            0f, 0f, 0f, 1f,
            0f, 0f, 1f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 1f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 1f, 1f,
            1f, 1f, 0f, 1f,
            1f, 1f, 1f, 1f,
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f
    };

    public CubeRenderer() {
        vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 aPosition;" +
                        "attribute vec4 aColor;" +
                        "varying vec4 ourColor;" +
                        "void main() {" +
                        "  gl_Position = uMVPMatrix * aPosition;" +
                        "  ourColor = aColor;" + // 将ourColor设置为我们输入的颜色
                        "}";
        fragmentShaderCode =
                "precision mediump float;" + //预定义的全局默认精度
                        "varying vec4 ourColor;" +
                        "void main() {" +
                        "  gl_FragColor = ourColor;" +
                        "}"; // 动态改变颜色
    }

    private final float[] vPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], rotationMatrix = new float[16], tempMatrix = new float[16];
    private int angle = 0;

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width / height;

        // 设置透视投影矩阵，近点是3，远点是7
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        // 设置观察点，当eyeZ是3时最大，是7时最小，超过这个范围时不可见
        Matrix.setLookAtM(viewMatrix, 0, 1, 1, 4f,
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(shaderProgram);

        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(CubeCoords);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, vertexBuffer);

        int colorHandle = GLES20.glGetAttribLocation(shaderProgram, "aColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        FloatBuffer colorBuffer = OpenGLUtil.createFloatBuffer(colors);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT,
                false, 4 * 4, colorBuffer);

        // 得到形状的变换矩阵的句柄
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        // 创建一个旋转矩阵
        Matrix.setRotateM(rotationMatrix, 0, angle, 0, 1, 0);
        // 计算
        Matrix.multiplyMM(tempMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        // 计算
        Matrix.multiplyMM(vPMatrix, 0, tempMatrix, 0, rotationMatrix, 0);

        // 将视图转换传递给着色器
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, vPMatrix, 0);
        /*GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, OpenGLUtil.createShortBuffer(indices));*/
        /*GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indices.length,
                GLES20.GL_UNSIGNED_SHORT, OpenGLUtil.createShortBuffer(indices));*/
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, indices.length,
                GLES20.GL_UNSIGNED_SHORT,  OpenGLUtil.createShortBuffer(indices));
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, indices2.length,
                GLES20.GL_UNSIGNED_SHORT,  OpenGLUtil.createShortBuffer(indices2));
        GLES20.glDisableVertexAttribArray(positionHandle);
        angle += 2;
    }
}
