package com.chends.opengl.renderer.light;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author cds created on 2019/12/13.
 */
public class LightRenderer extends BaseRenderer {
    private String vertexLightShaderCode, fragmentLightShaderCode;

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

    private short[] indices = new short[]{
            2, 3, 0, 1, 5, 3, 7, 2, 6, 0, 4, 5, 6, 7
    };
    private float[] lightPos = new float[]{1f, 1f, 1f, 1f};

    public LightRenderer(Context context) {
        super(context);
        vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 aPosition;" +
                        "void main() {" +
                        " gl_Position = uMVPMatrix * aPosition;" +
                        "}";
        fragmentShaderCode =
                "precision mediump float;" +
                        "void main() {" +
                        " vec3 lightColor = vec3(1.0, 1.0, 1.0);" +
                        " vec3 objectColor = vec3(1.0, 0.5, 0.31);" +
                        "  gl_FragColor = vec4(lightColor * objectColor, 1.0);" +
                        "}";

        vertexLightShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 aPosition;" +
                        "void main() {" +
                        " gl_Position = uMVPMatrix * aPosition;" +
                        " gl_PointSize = 25.0;" +
                        "}";
        fragmentLightShaderCode =
                "precision mediump float;" +
                        "void main() {" +
                        " gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);" +
                        "}";

    }

    private final float[] vPMatrix = new float[16], vPMatrix2 = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16];

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width / height;

        // 设置透视投影矩阵，近点是3，远点是7
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3f, 7f);
        Matrix.setLookAtM(viewMatrix, 0, 1.5f, 1.5f, 6f,
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        // 计算
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(vPMatrix2, 0, projectionMatrix, 0, viewMatrix, 0);

        drawCube();

        drawLight();
    }

    /**
     * 绘制光源
     */
    private void drawLight() {
        int lightProgram = OpenGLUtil.createProgram(vertexLightShaderCode, fragmentLightShaderCode);
        GLES20.glUseProgram(lightProgram);
        // 传入顶点坐标
        int lightPositionHandle = GLES20.glGetAttribLocation(lightProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(lightPositionHandle);
        GLES20.glVertexAttribPointer(lightPositionHandle, 4, GLES20.GL_FLOAT,
                false, 4 * 4, OpenGLUtil.createFloatBuffer(lightPos));

        int mLightMVPMatrixHandle = GLES20.glGetUniformLocation(lightProgram, "uMVPMatrix");
        // 计算
        //Matrix.multiplyMM(vPMatrix, 0, tempMatrix, 0, translateMatrix, 0);
        GLES20.glUniformMatrix4fv(mLightMVPMatrixHandle, 1, false, vPMatrix2, 0);

        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
        GLES20.glDisableVertexAttribArray(lightPositionHandle);
    }

    /**
     * 绘制立方体
     */
    private void drawCube() {
        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(shaderProgram);
        // 传入顶点坐标
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, OpenGLUtil.createFloatBuffer(CubeCoords));

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, vPMatrix, 0);

        // 绘制顶点
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indices.length,
                GLES20.GL_UNSIGNED_SHORT, OpenGLUtil.createShortBuffer(indices));

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
