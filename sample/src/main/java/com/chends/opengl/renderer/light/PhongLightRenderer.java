package com.chends.opengl.renderer.light;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2019/12/20.
 */
public class PhongLightRenderer extends BaseRenderer {
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
            // 红
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,

            // 蓝
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

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

    public PhongLightRenderer() {
        super();
        vertexShaderCode =
                "uniform mat4 uMVMatrix;" +
                        "uniform mat4 uMVPMatrix;" +
                        // 光源坐标
                        "uniform vec3 aLightPos;" +

                        "attribute vec4 aPosition;" +
                        // 法向量
                        "attribute vec3 aNormal;" +
                        "attribute vec3 objectColor;" +
                        // 结果
                        "varying vec4 aColor;" +
                        "void main() {" +
                        " vec3 lightColor = vec3(1.0, 1.0, 1.0);" +
                        // 环境光照
                        " float ambientStrength = 0.3;" +
                        " vec3 ambient = ambientStrength * lightColor;" +
                        // 转换坐标
                        " vec3 fragPos = vec3(uMVMatrix * aPosition);" +

                        // 漫反射光照
                        // 归一化法向量
                        " vec3 modelViewNormal = vec3(uMVMatrix * vec4(aNormal, 0.0));" +
                        // 归一化光源线
                        " vec3 lightDir = normalize(aLightPos - fragPos);" +
                        " float diff = max(dot(modelViewNormal, lightDir), 0.0);" +
                        " vec3 diffuse = diff * lightColor;" +

                        // 镜面光照
                        " float specularStrength = 0.5;" +
                        " vec3 viewDir = normalize(-fragPos);" +
                        " vec3 reflectDir = reflect(-lightDir, modelViewNormal);" +
                        " float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);" +
                        " vec3 specular = specularStrength * spec * lightColor;" +
                        // 结果，使用ambient,diffuse,specular相加则为结合的效果
                        " vec3 result = (ambient + diffuse + specular) * objectColor;" +
                        " aColor = vec4(result, 1.0);" +

                        " gl_Position = uMVPMatrix * aPosition;" +
                        "}";
        fragmentShaderCode =
                "precision mediump float;" +
                        "varying vec4 aColor;" +
                        "void main() {" +
                        " gl_FragColor = aColor;" +
                        "}";

        vertexLightShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec3 aPosition;" +
                        "void main() {" +
                        " gl_Position = uMVPMatrix * vec4(aPosition, 1.0);" +
                        " gl_PointSize = 25.0;" +
                        "}";
        fragmentLightShaderCode =
                "precision mediump float;" +
                        "void main() {" +
                        " gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);" +
                        "}";

    }

    private final float[] mMVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16],
            mLightMVPMatrix = new float[16], mLightModelMatrix = new float[16];

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width / height;

        // 设置透视投影矩阵，近点是3，远点是7
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 2f, 8f);
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
        int mLightPosHandle = GLES20.glGetUniformLocation(shaderProgram, "aLightPos");

        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

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
        GLES20.glEnableVertexAttribArray(lightPositionHandle);
        GLES20.glVertexAttribPointer(lightPositionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, OpenGLUtil.createFloatBuffer(mLightPosInModelSpace));

        Matrix.multiplyMM(mLightMVPMatrix, 0, viewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mLightMVPMatrix, 0, projectionMatrix, 0, mLightMVPMatrix, 0);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(lightProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mLightMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);

        GLES20.glDisableVertexAttribArray(lightPositionHandle);
    }
}
