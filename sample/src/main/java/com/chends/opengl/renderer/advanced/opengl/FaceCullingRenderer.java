package com.chends.opengl.renderer.advanced.opengl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2020/1/9.
 */
public class FaceCullingRenderer extends BaseRenderer {
    /**
     * 立方体的8个顶点
     */
    private float[] mCoords = new float[]{
            -0.5f, 0.5f, 0.5f, // 上左前顶点
            0.5f, 0.5f, 0.5f, // 上右前顶点
            -0.5f, 0.5f, -0.5f, // 上左后顶点
            0.5f, 0.5f, -0.5f, // 上右后顶点

            -0.5f, -0.5f, 0.5f, // 下左前顶点
            0.5f, -0.5f, 0.5f, // 下右前顶点
            -0.5f, -0.5f, -0.5f, // 下左后顶点
            0.5f, -0.5f, -0.5f, // 下右后顶点
    };

    private float[] cubeCoords = new float[3 * 6 * 6]/*{
            // 后
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            // 前
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            // 左
            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            // 右
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            // 下
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,
            // 上
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f
    }*/;

    /**
     * 各个面颜色
     */
    private final float[] colors = new float[]{
            // 蓝
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            // 红
            1.0f, 0.5f, 0.3f,
            1.0f, 0.5f, 0.3f,
            1.0f, 0.5f, 0.3f,
            1.0f, 0.5f, 0.3f,
            1.0f, 0.5f, 0.3f,
            1.0f, 0.5f, 0.3f,

            // 黄
            1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,

            // 青
            0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f,

            // 绿
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,

            // 品红
            1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f
    };

    /**
     * 顶点顺序，逆时针
     */
    private int[] indices = new int[]{
            2, 3, 6, 3, 7, 6, // 后面
            0, 4, 1, 1, 4, 5,// 前面
            0, 2, 6, 0, 6, 4, // 左面
            1, 5, 3, 3, 5, 7, // 右面
            0, 1, 2, 1, 3, 2, // 上面
            4, 6, 5, 5, 6, 7 // 下面
    };

    public FaceCullingRenderer() {
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

        for (int i = 0; i < indices.length; i++) {
            cubeCoords[i * 3] = mCoords[indices[i] * 3];
            cubeCoords[i * 3 + 1] = mCoords[indices[i] * 3 + 1];
            cubeCoords[i * 3 + 2] = mCoords[indices[i] * 3 + 2];
        }

    }

    private final float[] vPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], rotationMatrix = new float[16], tempMatrix = new float[16];
    private int angle = 0;

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        // 开启面剔除
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        // 设置哪种环绕顺序是面向观察点的
        GLES20.glFrontFace(GLES20.GL_CCW);
        // 设置剔除那些面
        GLES20.glCullFace(GLES20.GL_FRONT);

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
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(cubeCoords);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, vertexBuffer);

        int colorHandle = GLES20.glGetAttribLocation(shaderProgram, "aColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        FloatBuffer colorBuffer = OpenGLUtil.createFloatBuffer(colors);
        GLES20.glVertexAttribPointer(colorHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, colorBuffer);

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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, cubeCoords.length / 3);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);

        GLES20.glDeleteProgram(shaderProgram);
        angle += 2;
    }
}