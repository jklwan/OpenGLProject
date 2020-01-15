package com.chends.opengl.renderer.light;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.renderscript.Matrix4f;

import com.chends.opengl.R;
import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 多光源
 * @author chends create on 2020/1/2.
 */
public class LightMultipleLightsRenderer extends BaseRenderer {
    private String vertexLightShaderCode, fragmentLightShaderCode;

    private float[] mViewPos = new float[]{0f, 0f, 5f, 1f};
    private float[] cubeCoords = new float[]{
            // 顶点               // 法向量          // 纹理坐标
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,

            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,

            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,

            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f
    };
    private float[][] cubePositions = {
            {0.0f, 0.0f, 0.0f},
            {2.0f, 5.0f, -15.0f},
            {-1.5f, -2.2f, -2.5f},
            {-3.8f, -2.0f, -12.3f},
            {2.4f, -0.4f, -3.5f},
            {-1.7f, 3.0f, -7.5f},
            {1.3f, -2.0f, -2.5f},
            {1.5f, 2.0f, -2.5f},
            {1.5f, 0.2f, -1.5f},
            {-1.3f, 1.0f, -1.5f}
    };

    public LightMultipleLightsRenderer(Context context) {
        super(context);
        vertexShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.light_multiple_light_vertex);
        fragmentShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.light_multiple_light_fragment);

        vertexLightShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec3 aPosition;" +
                        "void main() {" +
                        " gl_Position = uMVPMatrix * vec4(aPosition, 1.0);" +
                        " gl_PointSize = 35.0;" +
                        "}";
        fragmentLightShaderCode =
                "precision mediump float;" +
                        "void main() {" +
                        " gl_FragColor = vec4(1.0);" +
                        "}";
    }

    private final float[] mMVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16],
            mLightMVPMatrix = new float[16];
    private final float[][] mLightModelMatrix = new float[5][16];
    private int diffuse, specular;


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_light_maps_image1);
        Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_light_maps_image2);

        diffuse = OpenGLUtil.createTextureNormal(bitmap);
        specular = OpenGLUtil.createTextureNormal(bitmap2);
        bitmap.recycle();
        bitmap2.recycle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width / height;

        // 设置透视投影矩阵，近点是3，远点是7
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 100f);
        Matrix.setLookAtM(viewMatrix, 0, mViewPos[0], mViewPos[1], mViewPos[2],
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        drawCube();
        drawLight();
    }

    /**
     * 绘制立方体
     */
    private void drawCube() {
        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode, new String[]{
                "aPosition", "aNormal", "aTexCoords"});
        GLES20.glUseProgram(shaderProgram);
        // 传入顶点坐标
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(cubeCoords);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 8 * 4, vertexBuffer);
        // 法向量
        int normalHandle = GLES20.glGetAttribLocation(shaderProgram, "aNormal");
        GLES20.glEnableVertexAttribArray(normalHandle);
        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                false, 8 * 4, vertexBuffer);
        // 纹理坐标
        int textHandle = GLES20.glGetAttribLocation(shaderProgram, "aTexCoords");
        GLES20.glEnableVertexAttribArray(textHandle);
        vertexBuffer.position(6);
        GLES20.glVertexAttribPointer(textHandle, 2, GLES20.GL_FLOAT,
                false, 8 * 4, vertexBuffer);

        int mMVMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVMatrix");
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        int mNormalMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "normalMatrix");
        int mLightMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "lightMatrix");

        final Matrix4f normalMatrix = new Matrix4f();
        normalMatrix.loadMultiply(new Matrix4f(viewMatrix), new Matrix4f(modelMatrix));
        normalMatrix.inverse();
        normalMatrix.transpose();
        GLES20.glUniformMatrix4fv(mNormalMatrixHandle, 1, false, normalMatrix.getArray(), 0);
        // 在view空间计算光方向
        final Matrix4f lightDirectionMatrix = new Matrix4f(viewMatrix);
        lightDirectionMatrix.inverse();
        lightDirectionMatrix.transpose();
        GLES20.glUniformMatrix4fv(mLightMatrixHandle, 1, false, lightDirectionMatrix.getArray(), 0);


        int materialDiffusePosHandle = GLES20.glGetUniformLocation(shaderProgram, "material.diffuse");
        int materialSpecularPosHandle = GLES20.glGetUniformLocation(shaderProgram, "material.specular");
        int materialShininessPosHandle = GLES20.glGetUniformLocation(shaderProgram, "material.shininess");
        OpenGLUtil.bindTexture(materialDiffusePosHandle, diffuse, 0);
        OpenGLUtil.bindTexture(materialSpecularPosHandle, specular, 1);
        GLES20.glUniform1f(materialShininessPosHandle, 256.0f);

        setDirLight(shaderProgram);
        setPointLight(shaderProgram);
        setSpotLight(shaderProgram);

        for (int i = 0; i < 10; i++) {
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, cubePositions[i][0], cubePositions[i][1], cubePositions[i][2]);
            float angle = 20f * i;
            Matrix.rotateM(modelMatrix, 0, angle, 1.0f, 0.3f, 0.5f);

            float angleInDegrees = (360.0f / 10000.0f) * (SystemClock.uptimeMillis() % 10000L);
            Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

            Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

            // 绘制顶点
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, cubeCoords.length / (3 + 3 + 2));
        }

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glDisableVertexAttribArray(textHandle);
    }

    private float[] lightDirection = new float[]{-10f, -10f, -10f};

    // 定向光参数
    private void setDirLight(int shaderProgram) {
        int lightDirectionPosHandle = GLES20.glGetUniformLocation(shaderProgram, "dirLight.direction");
        int lightAmbientPosHandle = GLES20.glGetUniformLocation(shaderProgram, "dirLight.ambient");
        int lightDiffusePosHandle = GLES20.glGetUniformLocation(shaderProgram, "dirLight.diffuse");
        int lightSpecularPosHandle = GLES20.glGetUniformLocation(shaderProgram, "dirLight.specular");
        GLES20.glUniform3f(lightDirectionPosHandle, lightDirection[0], lightDirection[1], lightDirection[2]);
        GLES20.glUniform3f(lightAmbientPosHandle, 0.2f, 0.2f, 0.2f);
        GLES20.glUniform3f(lightDiffusePosHandle, 0.5f, 0.5f, 0.5f);
        GLES20.glUniform3f(lightSpecularPosHandle, 1.0f, 1.0f, 1.0f);
    }

    private float[][] mLightPosInModelSpace = new float[][]{
            {2.0f, 2.4f, 0.0f, 1f},
            {-3.0f, 0.0f, -3.0f, 1f},
            {0f, -3.3f, -4.0f, 1f},
            {-3.0f, -3.0f, -2.0f, 1f}};
    private final float[] mLightPosInWorldSpace = new float[4], mLightPosInEyeSpace = new float[4];

    // 点光源参数
    private void setPointLight(int shaderProgram) {

        for (int i = 0; i < 4; i++) {
            String pointLight = "pointLights[" + i + "]";
            Matrix.setIdentityM(mLightModelMatrix[i], 0);

            Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix[i], 0, mLightPosInModelSpace[i], 0);
            Matrix.multiplyMV(mLightPosInEyeSpace, 0, viewMatrix, 0, mLightPosInWorldSpace, 0);
            int lightPositionPosHandle = GLES20.glGetUniformLocation(shaderProgram, pointLight + ".position");
            int lightAmbientPosHandle = GLES20.glGetUniformLocation(shaderProgram, pointLight + ".ambient");
            int lightDiffusePosHandle = GLES20.glGetUniformLocation(shaderProgram, pointLight + ".diffuse");
            int lightSpecularPosHandle = GLES20.glGetUniformLocation(shaderProgram, pointLight + ".specular");
            int lightConstantPosHandle = GLES20.glGetUniformLocation(shaderProgram, pointLight + ".constant");
            int lightLinearPosHandle = GLES20.glGetUniformLocation(shaderProgram, pointLight + ".linear");
            int lightQuadraticPosHandle = GLES20.glGetUniformLocation(shaderProgram, pointLight + ".quadratic");

            GLES20.glUniform3f(lightPositionPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
            GLES20.glUniform3f(lightAmbientPosHandle, 0.2f, 0.2f, 0.2f);
            GLES20.glUniform3f(lightDiffusePosHandle, 0.5f, 0.5f, 0.5f);
            GLES20.glUniform3f(lightSpecularPosHandle, 1.0f, 1.0f, 1.0f);

            GLES20.glUniform1f(lightConstantPosHandle, 1.0f);
            GLES20.glUniform1f(lightLinearPosHandle, 0.09f);
            GLES20.glUniform1f(lightQuadraticPosHandle, 0.032f);
        }
    }

    private float[] mSpotLightPosInModelSpace = new float[]{0f, 0f, 4f, 1f};

    // 聚光参数
    private void setSpotLight(int shaderProgram) {
        Matrix.setIdentityM(mLightModelMatrix[4], 0);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix[4], 0, mSpotLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, viewMatrix, 0, mLightPosInWorldSpace, 0);

        int lightPositionPosHandle = GLES20.glGetUniformLocation(shaderProgram, "spotLight.position");
        int lightDirectionPosHandle = GLES20.glGetUniformLocation(shaderProgram, "spotLight.direction");
        int lightCutOffPosHandle = GLES20.glGetUniformLocation(shaderProgram, "spotLight.cutOff");
        int lightOuterCutOffPosHandle = GLES20.glGetUniformLocation(shaderProgram, "spotLight.outerCutOff");
        int lightAmbientPosHandle = GLES20.glGetUniformLocation(shaderProgram, "spotLight.ambient");
        int lightDiffusePosHandle = GLES20.glGetUniformLocation(shaderProgram, "spotLight.diffuse");
        int lightSpecularPosHandle = GLES20.glGetUniformLocation(shaderProgram, "spotLight.specular");
        int lightConstantPosHandle = GLES20.glGetUniformLocation(shaderProgram, "spotLight.constant");
        int lightLinearPosHandle = GLES20.glGetUniformLocation(shaderProgram, "spotLight.linear");
        int lightQuadraticPosHandle = GLES20.glGetUniformLocation(shaderProgram, "spotLight.quadratic");

        GLES20.glUniform3f(lightPositionPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
        GLES20.glUniform3f(lightDirectionPosHandle, 0f, 0f, -1f);
        GLES20.glUniform1f(lightCutOffPosHandle, (float) Math.cos(2 * Math.PI / 360 * 40));
        GLES20.glUniform1f(lightOuterCutOffPosHandle, (float) Math.cos(2 * Math.PI / 360 * 60));

        GLES20.glUniform3f(lightAmbientPosHandle, 0.2f, 0.2f, 0.2f);
        GLES20.glUniform3f(lightDiffusePosHandle, 0.5f, 0.5f, 0.5f);
        GLES20.glUniform3f(lightSpecularPosHandle, 1.0f, 1.0f, 1.0f);

        GLES20.glUniform1f(lightConstantPosHandle, 1.0f);
        GLES20.glUniform1f(lightLinearPosHandle, 0.09f);
        GLES20.glUniform1f(lightQuadraticPosHandle, 0.032f);
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
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(lightProgram, "uMVPMatrix");
        // 绘制点光源
        for (int i = 0; i < 4; i++) {
            GLES20.glVertexAttribPointer(lightPositionHandle, 3, GLES20.GL_FLOAT,
                    false, 3 * 4, OpenGLUtil.createFloatBuffer(mLightPosInModelSpace[i]));

            Matrix.multiplyMM(mLightMVPMatrix, 0, viewMatrix, 0, mLightModelMatrix[i], 0);
            Matrix.multiplyMM(mLightMVPMatrix, 0, projectionMatrix, 0, mLightMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mLightMVPMatrix, 0);

            GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
        }
        // 绘制聚光灯
        GLES20.glVertexAttribPointer(lightPositionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, OpenGLUtil.createFloatBuffer(mSpotLightPosInModelSpace));

        Matrix.multiplyMM(mLightMVPMatrix, 0, viewMatrix, 0, mLightModelMatrix[4], 0);
        Matrix.multiplyMM(mLightMVPMatrix, 0, projectionMatrix, 0, mLightMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mLightMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);

        GLES20.glDisableVertexAttribArray(lightPositionHandle);
    }
}
