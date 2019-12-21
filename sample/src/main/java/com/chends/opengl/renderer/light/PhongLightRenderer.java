package com.chends.opengl.renderer.light;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2019/12/20.
 */
public class PhongLightRenderer extends BaseRenderer {
    private String vertexLightShaderCode, fragmentLightShaderCode;

    private float[] CubeCoords = new float[]{
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,

            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,

            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,

            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,

            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f
    };

    public PhongLightRenderer() {
        super();
        vertexShaderCode =
                "uniform mat4 model;" +
                        "uniform mat4 view;" +
                        "uniform mat4 projection;" +
                        "attribute vec3 aPosition;" +
                        "attribute vec3 aNormal;" +
                        "varying vec3 aColor;" +
                        "void main() {" +
                        " vec3 lColor = vec3(1.0, 1.0, 1.0);" +
                        " vec3 oColor = vec3(1.0, 0.5, 0.31);" +
                        // ambient
                        " float ambientStrength = 0.3;" +
                        " vec3 ambient = ambientStrength * lColor;" +
                        // 转换坐标
                        " FragPos = vec3(model * vec4(aPosition, 1.0));" +
                        " Normal = aNormal;" +
                        // diffuse
                        " vec3 norm = normalize(Normal);" +
                        " vec3 lightPos = vec3(1.2, 1.0, 2.0);" +
                        " vec3 lightDir = normalize(lightPos - FragPos);" +
                        " float diff = max(dot(norm, lightDir), 0.0);" +
                        " vec3 diffuse = diff * lColor;" +
                        " vec3 result = (ambient + diffuse) * oColor;" +
                        " aColor = vec4(result, 1.0);" +

                        " gl_Position = projection * view * vec4(aPosition, 1.0);" +
                        "}";
        fragmentShaderCode =
                "precision mediump float;" +
                        "varying vec3 aColor;" +
                        "void main() {" +
                        " gl_FragColor = aColor" +
                        "}";

        vertexLightShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec3 aPosition;" +
                        "void main() {" +
                        " gl_Position = uMVPMatrix * vec4(aPosition, 1.0);" +
                        "}";
        fragmentLightShaderCode =
                "precision mediump float;" +
                        "void main() {" +
                        " gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);" +
                        "}";

        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.setIdentityM(vPMatrix, 0);
    }

    private final float[] vPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16];

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width / height;

        // 设置透视投影矩阵，近点是3，远点是7
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 0.1f, 100);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f,
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
        // 计算
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // ---------- 绘制物品 ---------------
        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(shaderProgram);
        // 传入顶点坐标
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(CubeCoords);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, (3 + 3) * 4, vertexBuffer);
        // 法向量
        int normalHandle = GLES20.glGetAttribLocation(shaderProgram, "aNormal");
        GLES20.glEnableVertexAttribArray(normalHandle);
        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                false, (3 + 3) * 4, vertexBuffer);

        int projectHandle = GLES20.glGetUniformLocation(shaderProgram, "projection");
        GLES20.glUniformMatrix4fv(projectHandle, 1, false, projectionMatrix, 0);
        int modelHandle = GLES20.glGetUniformLocation(shaderProgram, "model");
        GLES20.glUniformMatrix4fv(modelHandle, 1, false, modelMatrix, 0);
        int viewHandle = GLES20.glGetUniformLocation(shaderProgram, "view");
        GLES20.glUniformMatrix4fv(modelHandle, 1, false, viewMatrix, 0);

        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, CubeCoords.length / (3 + 3));

        // ---------- 绘制光源 ---------------
        int lightProgram = OpenGLUtil.createProgram(vertexLightShaderCode, fragmentLightShaderCode);
        GLES20.glUseProgram(lightProgram);
        // 传入顶点坐标
        int lightPositionHandle = GLES20.glGetAttribLocation(lightProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(lightPositionHandle);
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(lightPositionHandle, 3, GLES20.GL_FLOAT,
                false, (3 + 3) * 4, vertexBuffer);

        int mMVPMatrixHandle1 = GLES20.glGetUniformLocation(lightProgram, "uMVPMatrix");
        // 移动光源的位置
        Matrix.translateM(vPMatrix, 0, 1.2f, 1.0f, 2.0f);
        // 缩放光源
        Matrix.scaleM(vPMatrix, 0, 0.2f, 0.2f, 0.2f);
        // 计算
        //Matrix.multiplyMM(vPMatrix, 0, tempMatrix, 0, translateMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle1, 1, false, vPMatrix, 0);

        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, CubeCoords.length / (3 + 3));

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glDisableVertexAttribArray(lightPositionHandle);
    }
}
