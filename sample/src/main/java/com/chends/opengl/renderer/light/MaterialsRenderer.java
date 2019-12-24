package com.chends.opengl.renderer.light;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.chends.opengl.R;
import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * 材质
 * @author chends create on 2019/12/24.
 */
public class MaterialsRenderer extends BaseRenderer {
    private String vertexLightShaderCode, fragmentLightShaderCode;
    private float[] mLightPosInModelSpace = new float[]{0f, 0.4f, 1f, 1f};
    private final float[] mLightPosInWorldSpace = new float[4], mLightPosInEyeSpace = new float[4];

    private float[] mViewPos = new float[]{1.5f, 1f, 4f, 1f};

    private float[] cubeCoords = new float[]{
            // ----顶点-----      ------法向量----
            // 后
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            // 前
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            // 左
            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
            // 右
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
            // 下
            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
            // 上
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f
    };

    /**
     * 各个面颜色
     */
    private final float[] cubeColorData = new float[]{
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

    public MaterialsRenderer(Context context) {
        super(context);
        vertexShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.light_materials_vertex);
        fragmentShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.light_materials_fragment);

        vertexLightShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec3 aPosition;" +
                        "attribute vec3 aColor;" +
                        "varying vec4 resultColor;" +
                        "void main() {" +
                        " gl_Position = uMVPMatrix * vec4(aPosition, 1.0);" +
                        " gl_PointSize = 25.0;" +
                        " resultColor = vec4(aColor, 1.0);" +
                        "}";
        fragmentLightShaderCode =
                "precision mediump float;" +
                        "varying vec4 resultColor;" +
                        "void main() {" +
                        " gl_FragColor = resultColor;" +
                        "}";

    }

    private final float[] mMVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16],
            mLightMVPMatrix = new float[16], mLightModelMatrix = new float[16];
    private float[] color = new float[3];
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width / height;

        // 设置透视投影矩阵，近点是3，远点是7
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3f, 10f);
        Matrix.setLookAtM(viewMatrix, 0, mViewPos[0], mViewPos[1], mViewPos[2],
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        float angleInDegrees = (360.0f / 10000.0f) * (SystemClock.uptimeMillis() % 10000L);
        Matrix.setIdentityM(mLightModelMatrix, 0);
        //Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        //Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 1.0f);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, viewMatrix, 0, mLightPosInWorldSpace, 0);

        Matrix.setIdentityM(modelMatrix, 0);
        //Matrix.translateM(modelMatrix, 0, 2.0f, 0.0f, -4.0f);
        //Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        float time = (80.0f / 10000.0f) * (SystemClock.uptimeMillis() % 10000L) + 10;
        color[0] = (float) Math.sin(time * 1.2f * Math.PI / 180f);
        color[1] = (float) Math.sin(time * 1.6f * Math.PI / 180f);
        color[2] = (float) Math.sin(time * 0.5f * Math.PI / 180f);

        drawCube();

        drawLight();
    }

    /**
     * 绘制立方体
     */
    private void drawCube() {
        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode, new String[]{
                "aPosition", "aNormal", "objectColor"});
        GLES20.glUseProgram(shaderProgram);
        // 传入顶点坐标
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(cubeCoords);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 6 * 4, vertexBuffer);
        // 法向量
        int normalHandle = GLES20.glGetAttribLocation(shaderProgram, "aNormal");
        GLES20.glEnableVertexAttribArray(normalHandle);
        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                false, 6 * 4, vertexBuffer);
        // 颜色
        int colorHandle = GLES20.glGetAttribLocation(shaderProgram, "objectColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, OpenGLUtil.createFloatBuffer(cubeColorData));

        int mMVMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVMatrix");
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        int materialAmbientPosHandle = GLES20.glGetUniformLocation(shaderProgram, "material.ambient");
        int materialDiffusePosHandle = GLES20.glGetUniformLocation(shaderProgram, "material.diffuse");
        int materialSpecularPosHandle = GLES20.glGetUniformLocation(shaderProgram, "material.specular");
        int materialShininessPosHandle = GLES20.glGetUniformLocation(shaderProgram, "material.shininess");
        GLES20.glUniform3f(materialAmbientPosHandle, 1.0f, 0.5f, 0.31f);
        GLES20.glUniform3f(materialDiffusePosHandle, 1.0f, 0.5f, 0.31f);
        GLES20.glUniform3f(materialSpecularPosHandle, 0.5f, 0.5f, 0.5f);
        GLES20.glUniform1f(materialShininessPosHandle, 128f);

        int lightAmbientPosHandle = GLES20.glGetUniformLocation(shaderProgram, "light.ambient");
        int lightDiffusePosHandle = GLES20.glGetUniformLocation(shaderProgram, "light.diffuse");
        int lightSpecularPosHandle = GLES20.glGetUniformLocation(shaderProgram, "light.specular");
        int lightPosHandle = GLES20.glGetUniformLocation(shaderProgram, "light.position");
        GLES20.glUniform3f(lightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);


        GLES20.glUniform3f(lightAmbientPosHandle, 0.2f * color[0], 0.2f * color[1], 0.2f * color[2]);
        GLES20.glUniform3f(lightDiffusePosHandle, 0.5f * color[0], 0.5f * color[1], 0.5f * color[2]);
        GLES20.glUniform3f(lightSpecularPosHandle, 1.0f, 1.0f, 1.0f);

        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, cubeCoords.length / (3 + 3));

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }

    /**
     * 绘制光源
     */
    private void drawLight() {
        // ---------- 绘制光源 ---------------
        int lightProgram = OpenGLUtil.createProgram(vertexLightShaderCode, fragmentLightShaderCode);
        GLES20.glUseProgram(lightProgram);
        // 传入顶点坐标
        int lightPositionHandle = GLES20.glGetAttribLocation(lightProgram, "aPosition");
        int lightColorHandle = GLES20.glGetAttribLocation(lightProgram, "aColor");
        GLES20.glEnableVertexAttribArray(lightPositionHandle);
        GLES20.glVertexAttribPointer(lightPositionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, OpenGLUtil.createFloatBuffer(mLightPosInModelSpace));

        GLES20.glEnableVertexAttribArray(lightColorHandle);
        GLES20.glVertexAttribPointer(lightColorHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, OpenGLUtil.createFloatBuffer(color));

        Matrix.multiplyMM(mLightMVPMatrix, 0, viewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mLightMVPMatrix, 0, projectionMatrix, 0, mLightMVPMatrix, 0);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(lightProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mLightMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);

        GLES20.glDisableVertexAttribArray(lightPositionHandle);
    }
}
