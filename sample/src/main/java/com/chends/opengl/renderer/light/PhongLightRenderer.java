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
                "uniform mat4 uMVPMatrix;" +
                        "uniform mat4 model;" +
                        "attribute vec3 aPosition;" +
                        "attribute vec3 aNormal;" +
                        "varying vec3 lColor;" +
                        "varying vec3 oColor;" +
                        "varying vec3 Normal;" +
                        "varying vec3 FragPos;" +
                        "void main() {" +
                        // 转换坐标
                        " FragPos = vec3(model * vec4(aPosition, 1.0));" +
                        " lColor = vec3(1.0, 1.0, 1.0);" +
                        " oColor = vec3(1.0, 0.5, 0.31);" +
                        // 法向量
                        " Normal =  aNormal;" +
                        " gl_Position = uMVPMatrix * vec4(aPosition, 1.0);" +
                        "}";
        fragmentShaderCode =
                "precision mediump float;" +
                        "varying vec3 lColor;" +
                        "varying vec3 oColor;" +
                        "varying vec3 Normal;" +
                        "varying vec3 FragPos;" +
                        "varying vec3 viewPos;" +
                        "void main() {" +
                        // ambient
                        " float ambientStrength = 0.3;" +
                        " vec3 ambient = ambientStrength * lColor;" +
                        // diffuse
                        " vec3 norm = normalize(Normal);" +
                        " vec3 lightPos = vec3(1.8, 1.8, 5);" +
                        " vec3 lightDir = normalize(lightPos - FragPos);" +
                        " float diff = max(dot(norm, lightDir), 0.0);" +
                        " vec3 diffuse = diff * lColor;" +
                        // specular
                        " vec3 viewPos = vec3(0,0,3);" +
                        " float specularStrength = 0.5;" +
                        " vec3 viewDir = normalize(viewPos - FragPos);" +
                        " vec3 reflectDir = reflect(-lightDir, norm);" +
                        " float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);" +
                        " vec3 specular = specularStrength * spec * lColor;" +

                        " vec3 result = (ambient + diffuse + specular) * oColor;" +
                        " gl_FragColor = vec4(result, 1.0);" +
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

        Matrix.setIdentityM(vPMatrix, 0);
        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setIdentityM(vPMatrix2, 0);
        Matrix.setIdentityM(model, 0);
        /*for (int i = 0; i < 16; i++) {
            model[i] = 1f;
        }*/
    }

    private final float[] vPMatrix = new float[16], vPMatrix2 = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], model = new float[16];

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width / height;

        // 设置透视投影矩阵，近点是3，远点是7
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f,
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
        // 计算
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(vPMatrix2, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
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

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, vPMatrix, 0);
        int modelHandle = GLES20.glGetUniformLocation(shaderProgram, "model");
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
        Matrix.translateM(vPMatrix2, 0, 0.7f, 0.8f, 0f);
        // 缩放光源
        Matrix.scaleM(vPMatrix2, 0, 0.1f, 0.1f, 0.1f);
        // 计算
        //Matrix.multiplyMM(vPMatrix, 0, tempMatrix, 0, translateMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle1, 1, false, vPMatrix2, 0);

        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, CubeCoords.length / (3 + 3));

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glDisableVertexAttribArray(lightPositionHandle);
    }
}
